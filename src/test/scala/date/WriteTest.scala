package date

import java.io.File

import scala.collection.mutable.ArrayBuffer

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import common.CommonTest
import date.api.SkillState

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

  test("write 1.6M dates") {
    val low = -8e5.toInt
    val high = 8e5.toInt
    val file = File.createTempFile("writetest", ".sf")
    val path = file.toPath()

    val σ = SkillState.create
    for (i ← low until high)
      σ.addDate(i)

    σ.write(path)

    val d = SkillState.read(path).getDates
    var cond = true
    for (i ← low until high)
      cond &&= (i == d.next.getDate)
    assert(cond, "match failed")

    file.delete
  }

  test("normalize 10MB v64") {
    val σ = SkillState.read("normalizedInput.sf")

    var min = Long.MaxValue
    for (d ← σ.getDates)
      min = Math.min(d.getDate, min)

    for (d ← σ.getDates)
      d.date -= min

    val file = File.createTempFile("normalized", ".sf")
    σ.write(file.toPath);
  }
}
