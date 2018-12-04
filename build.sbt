name := "reproducebylog"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
)

val unusedWarnings = (
  "-Ywarn-unused" ::
    "-Ywarn-unused-import" ::
    Nil
)

scalacOptions ++= (
  "-deprecation" ::
    "-unchecked" ::
    "-Xlint" ::
    "-Xfuture" ::
    "-language:existentials" ::
    "-language:higherKinds" ::
    "-language:implicitConversions" ::
    "-Yno-adapted-args" ::
    Nil
) ::: unusedWarnings

assemblyJarName in assembly := "reproducebylog.jar"

/* skip the test during assembly */
test in assembly := {}

/* set an explicit main class */
mainClass in assembly := Some("behiron.reproducebylog.sample.accessLogSample")

Seq(Compile, Test).flatMap(c =>
  scalacOptions in (c, console) --= unusedWarnings)
