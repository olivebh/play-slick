package dao

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }
import models._
import javax.inject.Singleton

@Singleton()
class AddressDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends Tables with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  /**
   * @param ec
   *  @return all addresses
   */
  def findAll(implicit ec: ExecutionContext): Future[List[AddressAPI]] = {

    val query = Address // TableQuery[Address]
    // query.filter ... .drop ... .take
    val action = query.result // Action...
    val futureAddresses = db.run(action) // Future[Seq[AddressRow]]

    futureAddresses.map(
      _.map {
      a => AddressAPI(addressId = a.addressId, street = a.street)
    }.toList // or query.to[List]
    )
  }

  /**
   * @param id
   *  @param ec
   *  @return address by id
   */
  def findById(id: Int)(implicit ec: ExecutionContext): Future[Option[AddressAPI]] = {

    db.run(Address.filter(_.addressId === id).result).map(_.headOption).map {
      _.map {
        a => AddressAPI(addressId = a.addressId, street = a.street)
      }
    }
  }

  /**
   * @param address
   *  @param ec
   *  @return generated AutoInc phoneId
   */
  def create(address: AddressAPI)(implicit ec: ExecutionContext): Future[Int] = {

    val addressRow = AddressRow(addressId = 0, street = address.street)
    db.run((Address returning Address.map(_.addressId)) += addressRow)
  }

  /**
   * @param id
   *  @param ec
   *  @return num of deleted rows
   */
  def delete(id: Int)(implicit ec: ExecutionContext): Future[Int] = {

    db.run(Address.filter(_.addressId === id).delete)
  }

  /**
   * updates addressssss' street
   */
  def update(id: Int, address: AddressAPI)(implicit ec: ExecutionContext): Future[Int] = {

    findById(id) flatMap {
      case None => Future.successful(0) // not found
      case Some(oldAddress) => { // don't forget the FILTER!
        val updateQuery = Address.filter(_.addressId === id).map(_.street).update(address.street)
        db.run(updateQuery)
      }
    }
  }

}