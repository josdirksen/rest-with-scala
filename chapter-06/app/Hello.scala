package controllers

import play.api.mvc._

object Hello extends Controller {

  def helloWorld = Action {

    Ok("Hello Play, now with reload")
  }

}