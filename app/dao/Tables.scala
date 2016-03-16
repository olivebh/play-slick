package dao

import models._

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {

  protected val driver: slick.driver.JdbcProfile // I renamed profile to driver..
  import driver.api._

  import slick.model.ForeignKeyAction
  import slick.jdbc.{ GetResult => GR }

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: driver.SchemaDescription = Address.schema ++ Person.schema ++ PersonAddress.schema ++ Phone.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /**
   * ADDRESS
   */
  implicit def GetResultAddressRow(implicit e0: GR[Int], e1: GR[String]): GR[AddressRow] = GR {
    prs =>
      import prs._
      AddressRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table address. Objects of this class serve as prototypes for rows in queries. */
  class Address(_tableTag: Tag) extends Table[AddressRow](_tableTag, "address") {
    def * = (addressId, street) <> (AddressRow.tupled, AddressRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(addressId), Rep.Some(street)).shaped.<>({ r => import r._; _1.map(_ => AddressRow.tupled((_1.get, _2.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column address_id SqlType(INT), AutoInc, PrimaryKey */
    val addressId: Rep[Int] = column[Int]("address_id", O.AutoInc, O.PrimaryKey)
    /** Database column street SqlType(VARCHAR), Length(100,true) */
    val street: Rep[String] = column[String]("street", O.Length(100, varying = true))
  }
  /** Collection-like TableQuery object for table Address */
  lazy val Address = new TableQuery(tag => new Address(tag))

  /**
   * PERSON
   */
  implicit def GetResultPersonRow(implicit e0: GR[Int], e1: GR[String]): GR[PersonRow] = GR {
    prs =>
      import prs._
      PersonRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table person. Objects of this class serve as prototypes for rows in queries. */
  class Person(_tableTag: Tag) extends Table[PersonRow](_tableTag, "person") {
    def * = (personId, name) <> (PersonRow.tupled, PersonRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(personId), Rep.Some(name)).shaped.<>({ r => import r._; _1.map(_ => PersonRow.tupled((_1.get, _2.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column person_id SqlType(INT), AutoInc, PrimaryKey */
    val personId: Rep[Int] = column[Int]("person_id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(50,true) */
    val name: Rep[String] = column[String]("name", O.Length(50, varying = true))
  }
  /** Collection-like TableQuery object for table Person */
  lazy val Person = new TableQuery(tag => new Person(tag))

  /**
   * PERSON-ADDRESS
   */
  implicit def GetResultPersonAddressRow(implicit e0: GR[Int]): GR[PersonAddressRow] = GR {
    prs =>
      import prs._
      PersonAddressRow.tupled((<<[Int], <<[Int]))
  }
  /** Table description of table person_address. Objects of this class serve as prototypes for rows in queries. */
  class PersonAddress(_tableTag: Tag) extends Table[PersonAddressRow](_tableTag, "person_address") {
    def * = (personId, addressId) <> (PersonAddressRow.tupled, PersonAddressRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(personId), Rep.Some(addressId)).shaped.<>({ r => import r._; _1.map(_ => PersonAddressRow.tupled((_1.get, _2.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column person_id SqlType(INT) */
    val personId: Rep[Int] = column[Int]("person_id")
    /** Database column address_id SqlType(INT) */
    val addressId: Rep[Int] = column[Int]("address_id")

    /** Primary key of PersonAddress (database name person_address_PK) */
    val pk = primaryKey("person_address_PK", (personId, addressId))

    /** Foreign key referencing Address (database name address_person_address_fk) */
    lazy val addressFk = foreignKey("address_person_address_fk", addressId, Address)(r => r.addressId, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
    /** Foreign key referencing Person (database name person_person_address_fk) */
    lazy val personFk = foreignKey("person_person_address_fk", personId, Person)(r => r.personId, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table PersonAddress */
  lazy val PersonAddress = new TableQuery(tag => new PersonAddress(tag))

  /**
   * PHONE
   */
  implicit def GetResultPhoneRow(implicit e0: GR[Int], e1: GR[String]): GR[PhoneRow] = GR {
    prs =>
      import prs._
      PhoneRow.tupled((<<[Int], <<[Int], <<[String]))
  }
  /** Table description of table phone. Objects of this class serve as prototypes for rows in queries. */
  class Phone(_tableTag: Tag) extends Table[PhoneRow](_tableTag, "phone") {
    def * = (phoneId, personId, number) <> (PhoneRow.tupled, PhoneRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(phoneId), Rep.Some(personId), Rep.Some(number)).shaped.<>({ r => import r._; _1.map(_ => PhoneRow.tupled((_1.get, _2.get, _3.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    /** Database column phone_id SqlType(INT), AutoInc */
    val phoneId: Rep[Int] = column[Int]("phone_id", O.AutoInc)
    /** Database column person_id SqlType(INT) */
    val personId: Rep[Int] = column[Int]("person_id")
    /** Database column number SqlType(VARCHAR), Length(50,true) */
    val number: Rep[String] = column[String]("number", O.Length(50, varying = true))

    /** Primary key of Phone (database name phone_PK) */
    val pk = primaryKey("phone_PK", (phoneId, personId))

    /** Foreign key referencing Person (database name person_phone_fk) */
    lazy val personFk = foreignKey("person_phone_fk", personId, Person)(r => r.personId, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Phone */
  lazy val Phone = new TableQuery(tag => new Phone(tag))
}
