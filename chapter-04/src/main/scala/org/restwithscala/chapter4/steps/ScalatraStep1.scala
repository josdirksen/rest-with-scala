package org.restwithscala.chapter4.steps

import javax.servlet.ServletContext

import org.restwithscala.chapter4.JettyLauncher
import org.scalatra.{NotFound, BadRequest, ScalatraServlet, LifeCycle}

import scala.util.{Failure, Success, Try}

// run this example, by specifying the name of the bootstrap to use
object ScalatraRunnerStep1 extends App {
  JettyLauncher.launch("org.restwithscala.chapter4.steps.ScalatraBootstrapStep1")
}

class ScalatraBootstrapStep1 extends LifeCycle {
  override def init(context: ServletContext) {
    context mount (new ScalatraStep1, "/*")
  }
}

/**
 * The servler containing the route to execute. For this first part we just
 * create a very simple route, where we just return some strings.
 */
class ScalatraStep1 extends ScalatraServlet  {

  notFound { "Route not implemented" }

  post("/tasks") { s"create a new task with body ${request.body}"}
  get("/tasks") { "Get all the tasks" }
  get("/tasks/:id") {
    Try { params("id").toInt }  match {
      case Success(id) => s"Get task with id: ${params("id")} "
      case Failure(e) => BadRequest(reason = "Can't parse id")
    }
  }
  delete("/tasks/:id") { s"Delete task with id: ${params("id")} "}
  put("/tasks/:id") { s"Update an existing task " +
    s"with id:  ${params("id")} " +
    s"and body ${request.body}"}
}


