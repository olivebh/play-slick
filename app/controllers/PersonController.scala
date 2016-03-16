package controllers

import javax.inject.Inject
import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import scala.concurrent.Future
import models._
import dao._

class PersonController @Inject() (personDAO: PersonDAO) extends Controller {

  def findAll = Action.async { implicit request =>

    personDAO.findAll map {
      persons => Ok(Json.toJson(persons))
    }
  }

  def findById(id: Int) = Action.async { implicit request =>

    personDAO.findById(id) map {
      case Some(person) => Ok(Json.toJson(person))
      case _ => BadRequest(Json.obj("error" -> s"Person with id $id doesn't exist"))
    }
  }

  def delete(id: Int) = Action.async { implicit request =>

    personDAO.delete(id) map {
      numDeleted => Ok(Json.obj("count" -> numDeleted))
    }
  }

  def create = Action.async(parse.json) { implicit request =>

    request.body.validate[PersonAPI].map {
      personAPI =>
        personDAO.create(personAPI) map {
          personID => Ok(Json.obj("id" -> personID))
        }
    } recoverTotal { t =>
      Future.successful(BadRequest(Json.obj("error" -> "Wrong JSON format")))
    }
  }

  def update(id: Int) = Action.async(parse.json) { implicit request =>

    request.body.validate[PersonAPI].map {
      personAPI =>
        personDAO.update(id, personAPI) map {
          numUpdated => Ok(Json.obj("count" -> numUpdated))
        }
    } recoverTotal { t =>
      Future.successful(BadRequest(Json.obj("error" -> "Wrong JSON format")))
    }
  }

}