name := "scala-websocket"

version := "0.1"

scalaVersion := "2.12.12"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"     % "2.6.7",
  "com.typesafe.akka" %% "akka-http"      % "10.2.3",
  "com.typesafe.akka" %% "akka-stream"    % "2.6.7"
)
