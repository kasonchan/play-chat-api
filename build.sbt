name := "PlayChatAPI"

version := "0.1"

lazy val `playchatapi` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.5"

libraryDependencies ++= Seq("org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23")

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")