package models

import play.api.libs.json.Json

// public JSON API models

case class PhoneAPI(
  phoneId: Int,
  number: String
)

case class AddressAPI(
  addressId: Int,
  street: String
)

case class PersonAPI(
  personId: Int,
  name: String,
  phones: List[PhoneAPI],
  addresses: List[AddressAPI]
)

object PhoneAPI {
  implicit val phoneFormat = Json.format[PhoneAPI]
}

object AddressAPI {
  implicit val addressFormat = Json.format[AddressAPI]
}

object PersonAPI {
  implicit val personFormat = Json.format[PersonAPI]
}