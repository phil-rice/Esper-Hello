import sbt._
object Dependencies {
  // Versions
  val scalaVersionNo = "2.11.7"
  val scalaPlusPlayTestVersion = "1.4.0-M3"
  val esperVersion = "5.3.0"

  // Libraries
  val scalaPlusPlay = "org.scalatestplus" %% "play" % scalaPlusPlayTestVersion % Test
  val esper = "com.espertech" % "esper" % esperVersion


  //Repositories
  val playRepositories = Seq(
    "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases")

  // Projects Dependencies
  val commonDependencies = Seq(esper, scalaPlusPlay)
}