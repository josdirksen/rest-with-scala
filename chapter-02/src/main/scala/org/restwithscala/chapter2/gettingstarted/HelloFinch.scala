package org.restwithscala.chapter2.gettingstarted

import io.finch.route._
import com.twitter.finagle.Httpx

object HelloFinch extends App {
  Httpx.serve(":8080", (Get / "hello" /> "Hello, Finch!").toService)

  println("Press <enter> to exit.")
  Console.in.read.toChar
}
