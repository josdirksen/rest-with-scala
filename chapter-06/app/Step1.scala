package controllers

import play.api.mvc._

object Step1 extends Controller {

  def createTask = Action { request =>
    val body: Option[String] = request.body.asText
    Ok(s"Create a task with body: ${body.getOrElse("No body provided")}")
  }

  def getTasks = Action {
    Ok("Getting all tasks")
  }

  def getTask(id: Long) = Action {
    Ok(s"Getting task with id: $id")
  }

  def deleteTask(id: Long) = Action {
    Ok(s"Delete task with id: $id")
  }

  def updateTask(id: Long) = Action { request =>
    val body: Option[String] = request.body.asText
    Ok(s"Update a task with body: ${body.getOrElse("No body provided")}")
  }

  def notImplemented(path: String) = Action {
    NotFound(s"Specified route not found: $path")
  }
}