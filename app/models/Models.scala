package models

// representations od table rows

case class AddressRow(addressId: Int, street: String)

case class PersonRow(personId: Int, name: String)

case class PhoneRow(phoneId: Int, personId: Int, number: String)

case class PersonAddressRow(personId: Int, addressId: Int)