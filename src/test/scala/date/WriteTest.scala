package date

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import common.CommonTest
import date.api.SkillState
import java.io.File

@RunWith(classOf[JUnitRunner])
class WriteTest extends CommonTest {

  test("§6.6 date example") {
    val file = File.createTempFile("writetest", ".sf")
    val path = file.toPath

    val σ = SkillState.create
    σ.addDate(1)
    σ.addDate(-1)
    σ.write(path)

    assert(sha256(path) === sha256("date-example.sf"))
    assert(SkillState.read(path).getDates.map(_.date).toList.sameElements(List(1, -1)))

    file.delete
  }

  test("write 1001 dates") {
    val file = File.createTempFile("writetest", ".sf")
    val path = file.toPath()

    val σ = SkillState.create
    (-500 to 500) foreach (σ.addDate(_))
    σ.write(path)

    assert(SkillState.read(path).getDates.map(_.date).toList.sameElements((-500 to 500).toList))

    file.delete
  }
}
