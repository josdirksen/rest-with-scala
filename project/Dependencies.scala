import sbt._

object DependenciesCommon {

  val backendDeps = Seq(
      "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
      "ch.qos.logback" % "logback-classic" % "1.1.2")
  val resolvers = Seq(
    "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/")
}

object DependenciesChapter1 {
  lazy val http4sVersion = "0.7.0"

  val backendDeps = Seq("org.http4s" %% "http4s-dsl" % http4sVersion,
        "org.http4s" %% "http4s-blazeserver"  % http4sVersion)

  val resolvers =
    Seq("Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases")

}

object DependenciesChapter2 {
  lazy val finchVersion = "0.7.0"
  val backendDeps = Seq(
    "com.github.finagle" %% "finch-core" % finchVersion,
    "com.github.finagle" %% "finch-argonaut" % finchVersion
  )


  val resolvers =
    Seq("Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")


}

object DependenciesChapter3 {
  lazy val unfilteredVersion = "0.8.4"
  val backendDeps = Seq(
    "net.databinder" %% "unfiltered-filter" % unfilteredVersion,
    "net.databinder" %% "unfiltered-jetty" % unfilteredVersion,
    "net.databinder" %% "unfiltered-netty" % unfilteredVersion,
    "net.databinder" %% "unfiltered-netty-server" % unfilteredVersion,
    "net.databinder" %% "unfiltered-directives" % unfilteredVersion,
    "no.shiplog" %% "directives2" % "0.9.2",
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"
  )
}


object DependenciesChapter4 {

  lazy val scalatraVersion = "2.3.0"
  val backendDeps = Seq(
    "org.scalatra" %% "scalatra" % scalatraVersion,
    "org.scalatra" %% "scalatra-json" % scalatraVersion,
    "org.scalatra" %% "scalatra-commands" % scalatraVersion,
    "org.json4s"   %% "json4s-jackson" % "3.2.9",
    "ch.qos.logback"    %  "logback-classic"   % "1.1.3"            ,
    "org.eclipse.jetty" %  "jetty-webapp"      % "9.2.10.v20150310",
    "com.typesafe.akka" %% "akka-actor" % "2.3.4"
  )
}

object DependenciesChapter5 {

  lazy val akkaHttpVersion = "1.0"

  val backendDeps = Seq (
    "com.typesafe.akka" %% "akka-stream-experimental" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-core-experimental" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaHttpVersion
  )
}

object DependenciesChapter6 {

  lazy val playVersion = "2.4.0"

  val backendDeps = Seq (
    "com.typesafe.play" %% "play" % playVersion,
    "com.typesafe.play" %% "play-docs" % playVersion
  )
}