package org.restwithscala.chapter3.gettingstarted

import unfiltered.request._
import unfiltered.response._

object HelloUnfiltered extends App {

  // explicitly set the thread name. If not the server can't be stopped easily
  // when started from an IDE
  Thread.currentThread().setName("swj");

  // Start a minimalistic server
  val echo = unfiltered.filter.Planify {
    case GET(Path("/hello")) => ResponseString("Hello Unfiltered")
  }

  unfiltered.jetty.Server.http(8080).plan(echo).run()

  println("Press <enter> to exit.")
  Console.in.read.toChar

  HTTP

}
