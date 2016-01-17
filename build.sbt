import sbt._

lazy val commonSettings = Seq(
  organization := "org.restwithscala",
  version := "0.1.0",
  scalaVersion := "2.11.6",
  checksums in update := Nil
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
lazy val runCH02Step2 = taskKey[Unit]("Run chapter 2 - Step2")
lazy val runCH02Step3 = taskKey[Unit]("Run chapter 2 - Step3")
lazy val runCH02Step4 = taskKey[Unit]("Run chapter 2 - Step4")
lazy val runCH07Finch = taskKey[Unit]("Run chapter 7 - Finch")

addCommandAlias("runCH01-HelloWorld", "; chapter01/runCH01HelloWorld")

addCommandAlias("runCH01-EchoServer", "; chapter01/runCH01EchoServer")

addCommandAlias("runCH02-HelloFinch", "; chapter02/runCH02HelloFinch")

addCommandAlias("runCH02-FinchRouters", "; chapter02/runCH02FinchRouters")

addCommandAlias("runCH02-runCH02Step1", "; chapter02/runCH02Step1")

addCommandAlias("runCH02-runCH02Step2", "; chapter02/runCH02Step2")

addCommandAlias("runCH02-runCH02Step3", "; chapter02/runCH02Step3")

addCommandAlias("runCH02-runCH02Step4", "; chapter02/runCH02Step4")

addCommandAlias("runCH07-Finch", "; chapter02/runCH07")

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
    fullRunTask(runCH02Step1, Compile, "org.restwithscala.chapter2.steps.FinchStep1"),
    fullRunTask(runCH02Step2, Compile, "org.restwithscala.chapter2.steps.FinchStep2"),
    fullRunTask(runCH02Step3, Compile, "org.restwithscala.chapter2.steps.FinchStep3"),
    fullRunTask(runCH02Step4, Compile, "org.restwithscala.chapter2.steps.FinchStep4"),
    fullRunTask(runCH07Finch, Compile, "org.restwithscala.chapter2.steps.FinchChapter7"))

lazy val runCH03HelloUnfiltered = taskKey[Unit]("Run chapter 3 - Unfiltered HelloWorld")
lazy val runCH03Step1 = taskKey[Unit]("Run chapter 3 - Step1")
lazy val runCH03Step2 = taskKey[Unit]("Run chapter 3 - Step2")
lazy val runCH03Step3 = taskKey[Unit]("Run chapter 3 - Step3")
lazy val runCH03Step4 = taskKey[Unit]("Run chapter 3 - Step4")
lazy val runCH07Unfiltered = taskKey[Unit]("Run chapter 7 - Unfiltered")

addCommandAlias("runCH03-HelloUnfiltered", "; chapter03/runCH03HelloUnfiltered")

addCommandAlias("runCH03-runCH03Step1", "; chapter03/runCH03Step1")

addCommandAlias("runCH03-runCH03Step2", "; chapter03/runCH03Step2")

addCommandAlias("runCH03-runCH03Step3", "; chapter03/runCH03Step3")

addCommandAlias("runCH03-runCH03Step4", "; chapter03/runCH03Step4")

addCommandAlias("runCH07-Unfiltered", "; chapter03/runCH07Unfiltered")

lazy val chapter03 = (project in file ("chapter-03"))
  .dependsOn(common)
  .settings(commonSettings: _*)
  .settings(name := "chapter-03",
    libraryDependencies := DependenciesChapter3.backendDeps,
    fullRunTask(runCH03HelloUnfiltered, Compile, "org.restwithscala.chapter3.gettingstarted.HelloUnfiltered"),
    fullRunTask(runCH03Step1, Compile, "org.restwithscala.chapter3.steps.Step1"),
    fullRunTask(runCH03Step2, Compile, "org.restwithscala.chapter3.steps.Step2"),
    fullRunTask(runCH03Step3, Compile, "org.restwithscala.chapter3.steps.Step3"),
    fullRunTask(runCH03Step4, Compile, "org.restwithscala.chapter3.steps.FinchStep4"),
    fullRunTask(runCH07Unfiltered, Compile, "org.restwithscala.chapter3.steps.UnfilteredChapter7"))


lazy val runCH04HelloScalatra = taskKey[Unit]("Run chapter 3 - Scalatra HelloWorld")
lazy val runCH04Step1 = taskKey[Unit]("Run chapter 4 - Step1")
lazy val runCH04Step2 = taskKey[Unit]("Run chapter 4 - Step2")
lazy val runCH04Step3 = taskKey[Unit]("Run chapter 4 - Step3")


addCommandAlias("runCH04-HelloScalatra", "; chapter04/runCH04HelloScalatra")

addCommandAlias("runCH04-runCH04Step1", "; chapter04/runCH04Step1")

addCommandAlias("runCH04-runCH04Step2", "; chapter04/runCH04Step2")

addCommandAlias("runCH04-runCH04Step3", "; chapter04/runCH04Step3")

addCommandAlias("runCH07-Scalatra", "; chapter04/runCH07Unfiltered")


lazy val chapter04 = (project in file ("chapter-04"))
  .dependsOn(common)
  .settings(commonSettings: _*)
  .settings(name := "chapter-04",
    libraryDependencies := DependenciesChapter4.backendDeps,
    fullRunTask(runCH04HelloScalatra, Compile, "org.restwithscala.chapter4.gettingstarted.ScalatraRunner"),
    fullRunTask(runCH04Step1, Compile, "org.restwithscala.chapter4.steps.ScalatraRunnerStep1"),
    fullRunTask(runCH04Step2, Compile, "org.restwithscala.chapter4.steps.ScalatraRunnerStep2"),
    fullRunTask(runCH04Step3, Compile, "org.restwithscala.chapter4.steps.ScalatraRunnerStep3"),
    fullRunTask(runCH07Unfiltered, Compile, "org.restwithscala.chapter4.steps.ScalatraRunnerChapter7"))


lazy val runCH05HelloAkkaDSL= taskKey[Unit]("Run chapter 5 - Scalatra HelloWorld")
lazy val runCH05Step1 = taskKey[Unit]("Run chapter 5 - Step1")
lazy val runCH05Step2 = taskKey[Unit]("Run chapter 5 - Step2")
lazy val runCH05Step3 = taskKey[Unit]("Run chapter 5 - Step3")
lazy val runCH05Step4 = taskKey[Unit]("Run chapter 5 - Step4")
lazy val runCH07Akka = taskKey[Unit]("Run chapter 5 - Step4")


addCommandAlias("runCH05-HelloAkka-DSL", "; chapter05/runCH05HelloAkkaDSL")

addCommandAlias("runCH05-runCH05Step1", "; chapter05/runCH05Step1")

addCommandAlias("runCH05-runCH05Step2", "; chapter05/runCH05Step2")

addCommandAlias("runCH05-runCH05Step3", "; chapter05/runCH05Step3")

addCommandAlias("runCH05-runCH05Step4", "; chapter05/runCH05Step4")

addCommandAlias("runCH07-akkahttp", "; chapter05/runCH07Akka")

lazy val chapter05 = (project in file ("chapter-05"))
  .dependsOn(common)
  .settings(commonSettings: _*)
  .settings(name := "chapter-05",
    libraryDependencies := DependenciesChapter5.backendDeps,
    fullRunTask(runCH05HelloAkkaDSL, Compile, "org.restwithscala.chapter5.gettingstarted.HelloDSL"),
    fullRunTask(runCH05Step1, Compile, "org.restwithscala.chapter5.steps.AkkaHttpDSLStep1"),
    fullRunTask(runCH05Step2, Compile, "org.restwithscala.chapter5.steps.AkkaHttpDSLStep2"),
    fullRunTask(runCH05Step3, Compile, "org.restwithscala.chapter5.steps.AkkaHttpDSLStep3"),
    fullRunTask(runCH05Step4, Compile, "org.restwithscala.chapter5.steps.AkkaHttpDSLStep4"),
    fullRunTask(runCH07Akka, Compile, "org.restwithscala.chapter5.steps.AkkaHttpChapter7"))

addCommandAlias("runCH06-HelloPlay", "; chapter06/run -Dhttp.port=8080  -Dplay.http.router=hello.Routes")

addCommandAlias("runCH06-runCH06Step1", "; chapter06/run -Dhttp.port=8080  -Dplay.http.router=step01.Routes")

addCommandAlias("runCH06-runCH06Step2", "; chapter06/run -Dhttp.port=8080  -Dplay.http.router=step02.Routes")

addCommandAlias("runCH06-runCH06Step3", "; chapter06/run -Dhttp.port=8080  -Dplay.http.router=step03.Routes -Dplay.http.errorHandler=controllers.ErrorHandler")

addCommandAlias("runCH07-play", "; chapter06/run -Dhttp.port=8080  -Dplay.http.router=chapter7.Routes -Dplay.http.errorHandler=controllers.ErrorHandler")

import PlayKeys._
lazy val chapter06 = (project in file ("chapter-06"))
  .enablePlugins(PlayScala)
  .dependsOn(common)
  .settings(commonSettings: _*)
  .settings(
    devSettings := Seq("play.server.http.port" -> "8000"),
    name := "chapter-06",
    libraryDependencies := DependenciesChapter6.backendDeps
  )

lazy val chapter07 = (project in file ("chapter-07"))
  .dependsOn(common)
  .settings(commonSettings: _*)
  .settings(
    name := "chapter-07",
    libraryDependencies := DependenciesChapter7.backendDeps
  )
