package org.restwithscala.chapter4.gettingstarted

import org.restwithscala.chapter4.JettyLauncher
import org.scalatra.{ScalatraServlet, LifeCycle}
import javax.servlet.ServletContext

// run this example, by specifying the name of the bootstrap to use
object ScalatraRunner extends App {
  JettyLauncher.launch("org.restwithscala.chapter4.gettingstarted.ScalatraBootstrap")
}

// used by jetty to mount the specified servlet
class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context mount (new HelloScalatra, "/*")
  }
}

// the real servlet code
class HelloScalatra extends ScalatraServlet  {

  notFound {
    "Route not found"
  }

  get("/") {
    "Hello from scalatra"
  }
}