import de.johoop.jacoco4sbt._
import JacocoPlugin._

name := "skillScalaTestsuite"

version := "0.5"

scalaVersion := "2.11.1"

javacOptions ++= Seq("-encoding", "UTF-8")

javaOptions ++= Seq("-Xmx4G","-Xms4G","-XX:MaxHeapFreeRatio=99","-XX:MaxPermSize=512M")

scalacOptions ++= Opts.compile.encoding("UTF8")

libraryDependencies ++= Seq(
	"junit" % "junit" % "4.10" % "test",
	"org.scalatest" % "scalatest_2.11" % "2.1.7" % "test"
)

testOptions in Test <+= (target in Test) map {
  t => Tests.Argument(TestFrameworks.ScalaTest, "junitxml(directory=\"%s\")" format (t / "test-reports"))
}

org.scalastyle.sbt.ScalastylePlugin.Settings

jacoco.settings
