package controllers

import javax.inject.Inject
import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import scala.concurrent.Future
import models._
import dao.AddressDAO

class AddressController @Inject() (addressDAO: AddressDAO) extends Controller {

  def findAll = Action.async { implicit request =>

    addressDAO.findAll map {
      addresses => Ok(Json.toJson(addresses))
    }
  }

  def findById(id: Int) = Action.async { implicit request =>

    addressDAO.findById(id) map {
      case Some(address) => Ok(Json.toJson(address))
      case _ => BadRequest(Json.obj("error" -> s"Address with id $id doesn't exist"))
    }
  }

  def delete(id: Int) = Action.async { implicit request =>

    addressDAO.delete(id) map {
      numDeleted => Ok(Json.obj("count" -> numDeleted))
    }
  }

  def create = Action.async(parse.json) { implicit request =>

    request.body.validate[AddressAPI].map {
      addressAPI =>
        addressDAO.create(addressAPI) map {
          addressId => Ok(Json.obj("id" -> addressId))
        }
    } recoverTotal { t =>
      Future.successful(BadRequest(Json.obj("error" -> "Wrong JSON format")))
    }
  }

  def update(id: Int) = Action.async(parse.json) { implicit request =>

    request.body.validate[AddressAPI].map {
      addressAPI =>
        addressDAO.update(id, addressAPI) map {
          numUpdated => Ok(Json.obj("count" -> numUpdated))
        }
    } recoverTotal { t =>
      Future.successful(BadRequest(Json.obj("error" -> "Wrong JSON format")))
    }
  }

}