name := """cybrez-zoom"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.scalikejdbc"           %%  "scalikejdbc-play-plugin"   % "2.3.1",
  "org.skinny-framework"      %%  "skinny-orm"                % "1.3.6",
  "ch.qos.logback"            %   "logback-classic"           % "1.1.2",
  "mysql"                     %   "mysql-connector-java"      % "5.1.31",
  "commons-io"                %   "commons-io"                % "2.4",
  "com.github.nscala-time"    %%  "nscala-time"               % "1.2.0",
  "com.h2database"            %   "h2"                        % "1.4.+",
  "com.amazonaws"             %   "aws-java-sdk"              % "1.9.37",
  "com.google.api-client"     %   "google-api-client"         % "1.20.0",
  "com.propensive"            %%  "rapture-core"              % "1.0.0",
  "com.propensive"            %%  "rapture-json-jawn"         % "1.0.8",
  "commons-codec"             %   "commons-codec"             % "1.10",
  "org.scalatest"             %   "scalatest_2.11"            % "2.2.1"     % "test"
)


initialCommands := """
import scalikejdbc._
import skinny.orm._, feature._
import org.joda.time._
skinny.DBSettings.initialize()
implicit val session = AutoSession
"""

