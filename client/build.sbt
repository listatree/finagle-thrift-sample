name := "finagle-thrift-client-sample"

version := "1.0.0"

scalaVersion  := "2.10.4"

resolvers ++= Seq(
  "Twitter repository" at "http://maven.twttr.com"
)

libraryDependencies ++= Seq(
    "com.twitter" 			%% "scrooge-core"    % "3.17.0",
    "com.twitter"       %% "finagle-thrift"  % "6.24.0"
)

com.twitter.scrooge.ScroogeSBT.newSettings
