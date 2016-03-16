name := """PlaySlickBasics"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "1.1.1",		// PlaySlick
  "mysql" % "mysql-connector-java" % "5.1.38",			// JDBC driver
  "com.typesafe.slick" %% "slick-codegen" % "3.1.1"		// for generating code
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator