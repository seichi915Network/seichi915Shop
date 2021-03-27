ThisBuild / scalaVersion := "2.13.4"
ThisBuild / version := "1.0.2"
ThisBuild / description := "seichi915Network ショッププラグイン"

resolvers ++= Seq(
  "hub.spigotmc.org" at "https://hub.spigotmc.org/nexus/content/repositories/snapshots/",
  "oss.sonatype.org" at "https://oss.sonatype.org/content/repositories/snapshots",
  "maven.elmakers.com" at "https://maven.elmakers.com/repository/",
  "papermc.io" at "https://papermc.io/repo/repository/maven-public/",
  "maven.playpro.com" at "https://maven.playpro.com",
  "maven.enginehub.org" at "https://maven.enginehub.org/repo/",
  "jitpack.io" at "https://jitpack.io"
)

libraryDependencies ++= Seq(
  "com.destroystokyo.paper" % "paper-api" % "1.14.4-R0.1-SNAPSHOT",
  "com.sk89q.worldguard" % "worldguard-bukkit" % "7.0.4",
  "com.github.TechFortress" % "GriefPrevention" % "16.17.1",
  "org.scalikejdbc" %% "scalikejdbc" % "3.5.0",
  "org.xerial" % "sqlite-jdbc" % "3.7.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.typelevel" %% "cats-effect" % "2.3.1",
  "javax.xml.bind" % "jaxb-api" % "2.3.0"
)

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", _ @ _*) => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".xml" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".types" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "plugin.yml" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "config.yml" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "Syntax.java" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith "database.db" => MergeStrategy.first
  case "application.conf" => MergeStrategy.concat
  case "unwanted.txt" => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

lazy val root = (project in file("."))
  .settings(
    name := "seichi915Shop",
    assemblyOutputPath in assembly := baseDirectory.value / "target" / "build" / s"seichi915Shop-${version.value}.jar"
  )
