package org.restwithscala

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.util.component.AbstractLifeCycle.AbstractLifeCycleListener
import org.eclipse.jetty.util.component.LifeCycle
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

package object chapter4 {

  /**
   * Simple helper object which starts jetty and sets the specified scalatra
   * bootstrap class.
   */
  object JettyLauncher {

    def launch(bootstrapClass: String): Server = {

      // define the servlet context, point to our scalatra servlet
      val context = new WebAppContext()
      context setContextPath "/"
      context.setResourceBase("src/main/webapp")
      context.setInitParameter(ScalatraListener.LifeCycleKey, bootstrapClass)
      context.addEventListener(new ScalatraListener)
      context.addServlet(classOf[DefaultServlet], "/")

      // create a server and attach the context
      val server = new Server(8080)
      server.setHandler(context)

      // add a lifecycle listener so we can stop the server from the console
      server.addLifeCycleListener(new AbstractLifeCycleListener() {
        override def lifeCycleStarted(event: LifeCycle): Unit = {
          println("Press <enter> to exit.")
          Console.in.read.toChar
          server.stop()
        }
      })

      // start and return the server
      server.start
      server.join
      server
    }
  }
}
