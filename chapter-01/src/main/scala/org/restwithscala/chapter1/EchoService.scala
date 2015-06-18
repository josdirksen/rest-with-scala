package org.restwithscala.chapter1

import org.http4s.dsl.Root
import org.http4s.server.blaze.BlazeBuilder

import org.http4s.dsl._
import org.http4s.server._
import org.http4s.util.task

object EchoService extends App {
  val task = BlazeBuilder.bindHttp(8080)
    .mountService(ExampleService.service, "/")
    .run

  println("Server available on port 8080")
  task.awaitShutdown()
}

object ExampleService {

  def service = HttpService {
    case req @ GET -> Root / "echo" =>
      Ok(req.uri.params.get("msg").getOrElse("Please use url in form of http://localhost:8080/echo?msg=hello"))

    case _ -> Root => MethodNotAllowed()
  }
}
