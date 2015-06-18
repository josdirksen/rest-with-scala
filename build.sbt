import sbt._

lazy val commonSettings = Seq(
  organization := "org.restwithscala",
  version := "0.1.0",
  scalaVersion := "2.11.6"
)

lazy val common = (project in file ("common"))
  .settings(commonSettings: _*)
  .settings(
    name := "common",
    resolvers := DependenciesCommon.resolvers,
    libraryDependencies := DependenciesCommon.backendDeps
  )

lazy val runCH01HelloWorld = taskKey[Unit]("Run chapter 1 - HelloWorld")
lazy val runCH01EchoServer = taskKey[Unit]("Run chapter 1 - EchoServer")
lazy val runCH02HelloFinch = taskKey[Unit]("Run chapter 2 - Finch HelloWorld")
lazy val runCH02FinchRouters = taskKey[Unit]("Run chapter 2 - Finch HelloWorld")
lazy val runCH02Step1 = taskKey[Unit]("Run chapter 2 - Step1")

addCommandAlias("runCH01-HelloWorld", "; chapter01/runCH01HelloWorld")

addCommandAlias("runCH01-EchoServer", "; chapter01/runCH01EchoServer")

addCommandAlias("runCH02-HelloFinch", "; chapter02/runCH02HelloFinch")

addCommandAlias("runCH02-FinchRouters", "; chapter02/runCH02FinchRouters")

addCommandAlias("runCH02-runCH02Step1", "; chapter02/runCH02Step1")

lazy val chapter01 = (project in file ("chapter-01"))
  .dependsOn(common)
  .settings(commonSettings: _*)
  .settings(name := "chapter-01",
            resolvers := DependenciesChapter1.resolvers,
            libraryDependencies := DependenciesChapter1.backendDeps,
            fullRunTask(runCH01HelloWorld, Compile, "org.restwithscala.chapter1.HelloWorld"),
            fullRunTask(runCH01EchoServer, Compile, "org.restwithscala.chapter1.EchoService")
            )

lazy val chapter02 = (project in file ("chapter-02"))
  .dependsOn(common)
  .settings(commonSettings: _*)
  .settings(name := "chapter-02",
    resolvers := DependenciesChapter2.resolvers,
    libraryDependencies := DependenciesChapter2.backendDeps,
    fullRunTask(runCH02HelloFinch, Compile, "org.restwithscala.chapter2.gettingstarted.HelloFinch"),
    fullRunTask(runCH02FinchRouters, Compile, "org.restwithscala.chapter2.routes.FinchRoutes"),
    fullRunTask(runCH02Step1, Compile, "org.restwithscala.chapter2.step1.FinchStep1"))

//lazy val chapter03 = project in file ("chapter-03") dependsOn common
//lazy val chapter04 = project in file ("chapter-04") dependsOn common
//lazy val chapter05 = project in file ("chapter-05") dependsOn common
//lazy val chapter06 = project in file ("chapter-06") dependsOn common
//lazy val chapter07 = project in file ("chapter-07") dependsOn common


