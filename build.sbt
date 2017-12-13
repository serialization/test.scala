import java.lang.Runtime

name := "skillScalaTestsuite"

version := "0.5"

scalaVersion := "2.12.4"

javacOptions ++= Seq("-encoding", "UTF-8")

javaOptions ++= Seq("-Xmx4G","-Xms4G","-XX:MaxHeapFreeRatio=99","-XX:MaxPermSize=512M")

scalacOptions ++= Opts.compile.encoding("UTF8")

libraryDependencies ++= Seq(
	"junit" % "junit" % "4.12" % "test",
    "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)

(testOptions in Test) += Tests.Argument(TestFrameworks.ScalaTest, "-u", "target/tests")

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"
