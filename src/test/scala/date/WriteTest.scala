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
    val σ = SkillState.create
    σ.addDate(1)
    σ.addDate(-1)
    σ.write(File.createTempFile("writetest", ".sf").toPath)
  }

  test("simple write test") {
    val σ = SkillState.create
    (1 to 100) foreach (σ.addDate(_))
    σ.write(File.createTempFile("writetest", ".sf").toPath)
  }
}
