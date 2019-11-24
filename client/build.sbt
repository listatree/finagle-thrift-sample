// import com.twitter.scrooge.ScroogeSBT
import sbt.Keys.libraryDependencies

name := "quickstart-client"

version := "1.0"
scalaVersion := "2.11.6"

lazy val app = project.in(file("."))
  .settings(
    libraryDependencies += "com.twitter" %% "finagle-http" % "19.11.0",
    libraryDependencies += "com.twitter" %% "finagle-thrift" % "19.11.0"
  )

