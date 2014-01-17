import de.johoop.jacoco4sbt._
import JacocoPlugin._

name := "skillScalaTestsuite"

version := "0.1"

scalaVersion := "2.10.3"

javacOptions ++= Seq("-encoding", "UTF-8")

javaOptions ++= Seq("-Xmx4G","-Xms4G","-XX:MaxHeapFreeRatio=99")

scalacOptions ++= Opts.compile.encoding("UTF8")

libraryDependencies ++= Seq(
	"junit" % "junit" % "4.10" % "test",
	"org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test"
)

testOptions in Test <+= (target in Test) map {
  t => Tests.Argument(TestFrameworks.ScalaTest, "junitxml(directory=\"%s\")" format (t / "test-reports"))
}

org.scalastyle.sbt.ScalastylePlugin.Settings

jacoco.settings
