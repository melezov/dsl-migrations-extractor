organization := "com.dslplatform.extractor"
name := "dsl-migrations-extractor"
version := "0.0.1"

libraryDependencies ++= Seq(
  "com.github.tminglei" %% "slick-pg"       % "0.14.1"
, "com.github.tminglei" %% "slick-pg_date2" % "0.14.1"
, "org.postgresql"      %  "postgresql"     % "9.4.1208"

, "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3-1"

, "com.typesafe.scala-logging" %% "scala-logging"   % "3.4.0"
, "ch.qos.logback"             %  "logback-classic" % "1.1.7"
)

scalaVersion := "2.11.8"
scalacOptions ++= Seq(
  "-deprecation"
, "-encoding", "UTF-8"
, "-feature"
, "-language:_"
, "-optimise"
, "-unchecked"
, "-Xlint"
, "-Xno-forwarders"
, "-Xverify"
, "-Yclosure-elim"
, "-Yconst-opt"
, "-Ydead-code"
, "-Yinline-warnings"
, "-Yinline"
, "-Yrepl-sync"
, "-Ywarn-adapted-args"
, "-Ywarn-dead-code"
, "-Ywarn-inaccessible"
, "-Ywarn-infer-any"
, "-Ywarn-nullary-override"
, "-Ywarn-nullary-unit"
, "-Ywarn-numeric-widen"
, "-Ywarn-unused"
)

unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value)
unmanagedSourceDirectories in Test := Nil
