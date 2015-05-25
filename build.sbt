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
  "org.scalatest"             %   "scalatest_2.11"            % "2.2.1"     % "test"
)
