package dao

import javax.inject.Inject
import javax.inject.Singleton
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }
import models._

@Singleton()
class PersonDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends Tables with HasDatabaseConfigProvider[JdbcProfile] {

  val profile = driver
  import driver.api._

  def findAll(implicit ec: ExecutionContext): Future[List[PersonAPI]] = {

    db.run(getPersonsQuery().result) map {
      dataTuples =>
        val groupedByPerson = dataTuples.groupBy(_._1)
        groupedByPerson.map {
          case (personRow, tuples) =>
            val phones = tuples.map(_._2).distinct.map { p => PhoneAPI(p.phoneId, p.number) }
            val addresses = tuples.flatMap(_._3).distinct.map { a => // notice flatMap
              AddressAPI(
                addressId = a.addressId,
                street = a.street
              )
            }
            PersonAPI(
              personId = personRow.personId,
              name = personRow.name,
              phones = phones.toList,
              addresses = addresses.toList
            )
        }.toList
    }
  }

  def findById(id: Int)(implicit ec: ExecutionContext): Future[Option[PersonAPI]] = {

    db.run(getPersonsQuery(Option(id)).result) map {
      dataTuples =>
        val groupedByPerson = dataTuples.groupBy(_._1)
        groupedByPerson.map {
          case (personRow, tuples) => // this could've been a "transform" method...
            val phones = tuples.map(_._2).distinct.map { p => PhoneAPI(p.phoneId, p.number) }
            val addresses = tuples.flatMap(_._3).distinct.map { a => // notice flatMap
              AddressAPI(
                addressId = a.addressId,
                street = a.street
              )
            }
            PersonAPI(
              personId = personRow.personId,
              name = personRow.name,
              phones = phones.toList,
              addresses = addresses.toList
            )
        }.headOption
    }
  }

  /**
   * common query for findAll and findById
   */
  private def getPersonsQuery(maybeId: Option[Int] = None) = {

    val personsQuery = maybeId match {
      case None => Person
      case Some(id) => Person.filter(_.personId === id)
    }

    val withPhonesQuery = for {
      (person, phone) <- personsQuery.join(Phone).on(_.personId === _.personId)
    } yield (person, phone)

    val withAddressesQuery = for {
      (((person, phone), _), address) <- withPhonesQuery.
        joinLeft(PersonAddress).on(_._1.personId === _.personId).
        joinLeft(Address).on(_._2.map(_.addressId) === _.addressId)
    } yield (person, phone, address)

    withAddressesQuery
  }

  def create(person: PersonAPI)(implicit ec: ExecutionContext): Future[Int] = {

    val personRow = PersonRow(personId = 0, name = person.name)
    db.run((Person returning Person.map(_.personId)) += personRow) flatMap {
      personId =>
        val phoneRows = person.phones.map(p => PhoneRow(phoneId = 0, personId = personId, number = p.number)) // insert new phone numbers
        val addressLinkRows = person.addresses.map(a => PersonAddressRow(personId = personId, addressId = a.addressId)) // link with EXISTING! addresses
        val queries = DBIO.seq(
          Phone ++= phoneRows,
          PersonAddress ++= addressLinkRows
        )
        db.run(queries).map(_ => personId) // return personId
    }
  }

  def delete(id: Int)(implicit ec: ExecutionContext): Future[Int] = {

    db.run(Person.filter(_.personId === id).delete)
  }

  def update(id: Int, person: PersonAPI)(implicit ec: ExecutionContext): Future[Int] = {

    findById(id) flatMap {
      case None => Future.successful(0) // not found
      case Some(oldPerson) => {
        val updatePersonQuery = Person.filter(_.personId === id).map(_.name).update(person.name)

        // delete old phones and add new
        // you could've compare the old phones list and new phones list if you want
        // then delete the missing, update appropriate phones, and add new ones...
        val deleteOldPhones = Phone.filter(_.personId === id).delete
        val newPhones = person.phones.map(p => PhoneRow(phoneId = 0, personId = oldPerson.personId, number = p.number))
        val insertNewPhones = Phone ++= newPhones

        // same for addresses
        val deleteOldAddresses = PersonAddress.filter(_.personId === id).delete
        val newAddresses = person.addresses.map(a => PersonAddressRow(personId = oldPerson.personId, addressId = a.addressId))
        val insertNewAddresses = PersonAddress ++= newAddresses

        val queries = DBIO.seq(
          updatePersonQuery,
          deleteOldPhones,
          insertNewPhones,
          deleteOldAddresses,
          insertNewAddresses
        )
        db.run(queries).map(_ => 1)
      }
    }
  }

}