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


Seq(Compile, Test).flatMap(c =>
  scalacOptions in (c, console) --= unusedWarnings)
