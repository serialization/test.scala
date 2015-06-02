import java.lang.Runtime

name := "skillScalaTestsuite"

version := "0.5"

scalaVersion := "2.11.2"

javacOptions ++= Seq("-encoding", "UTF-8")

javaOptions ++= Seq("-Xmx4G","-Xms4G","-XX:MaxHeapFreeRatio=99","-XX:MaxPermSize=512M")

scalacOptions ++= Opts.compile.encoding("UTF8")

libraryDependencies ++= Seq(
	"junit" % "junit" % "4.10" % "test",
	"org.scalatest" % "scalatest_2.11" % "2.1.7" % "test"
)

(testOptions in Test) += Tests.Argument(TestFrameworks.ScalaTest, "-u", "target/tests")

testResultLogger in (Test, test) := new TestResultLogger {
    import sbt.Tests._
    import sbt.TestResultLogger.Defaults._
    def run(log: Logger, results: Output, taskName: String): Unit = {
        def run(r: TestResultLogger): Unit = r.run(log, results, taskName)

        run(printSummary)

        if (printStandard_?(results))
          run(printStandard)

        if (results.events.isEmpty)
          run(printNoTests)
        else
          run(printFailures)
    }
}

org.scalastyle.sbt.ScalastylePlugin.Settings
