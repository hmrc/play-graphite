import play.core.PlayVersion
import sbt.Keys._
import sbt._


val libName = "play-graphite"


val compileDependencies = Seq(
  "com.typesafe.play"     %% "play"            % PlayVersion.current,
  "uk.gov.hmrc"           %% "play-config"     % "7.2.0",
  "ch.qos.logback"        % "logback-classic" % "1.2.3",
  "io.dropwizard.metrics" % "metrics-graphite" % "3.2.5",
  "de.threedimensions"    %% "metrics-play"    % "2.5.13",
   // force dependencies due to security flaws found in jackson-databind < 2.9.x using XRay
   "com.fasterxml.jackson.core"     % "jackson-core"            % "2.9.7",
   "com.fasterxml.jackson.core"     % "jackson-databind"        % "2.9.7",
   "com.fasterxml.jackson.core"     % "jackson-annotations"     % "2.9.7",
   "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8"   % "2.9.7",
   "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.9.7",
   // force dependencies due to security flaws found in xercesImpl 2.11.0
   "xerces" % "xercesImpl" % "2.12.0"

)

val testDependencies = Seq(
  "com.typesafe.play"      %% "play-test"          % PlayVersion.current % "test",
  "org.scalatest"          %% "scalatest"          % "3.0.4"             % "test",
  "org.scalacheck"         %  "scalacheck_2.11"    % "1.13.5"            % "test",
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1"             % "test",
  "org.mockito"            %  "mockito-all"        % "1.9.5"             % "test",
  "org.pegdown"            %  "pegdown"            % "1.5.0"             % "test"
)

lazy val playGraphite = Project(libName, file("."))
  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, SbtArtifactory)
  .settings(
    majorVersion := 4,
    makePublicallyAvailableOnBintray := true,
    scalaVersion := "2.11.12",
    libraryDependencies ++= compileDependencies ++ testDependencies,
    crossScalaVersions := Seq("2.11.12"),
    resolvers := Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.typesafeRepo("releases"),
      Resolver.jcenterRepo
    )
  )
  .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)
