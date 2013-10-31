package date

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import common.CommonTest
import date.api.SkillState
import java.io.File

@RunWith(classOf[JUnitRunner])
class WriteTest extends CommonTest {

  test("simple write date test") {
    val f: File = File.createTempFile("writetest_", ".sf")

    val σ = SkillState.create
    σ.addDate(1)
    σ.addDate(-1)
    σ.write("writetest-date-(1,-1).sf") // f.getAbsolutePath()

    f.deleteOnExit()
  }

  ignore("simple write test") {
    val σ = SkillState.create
    (1 to 100) foreach (σ.addDate(_))
    σ.write("writetest-date-(1..100).sf")
  }

}
