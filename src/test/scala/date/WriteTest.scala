package date

import java.io.File

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import common.CommonTest
import date.api.SkillState

@RunWith(classOf[JUnitRunner])
class WriteTest extends CommonTest {

  test("copy of §6.6 example") {
    val path = tmpFile("write.copy")

    SkillState.read("src/test/resources/date-example.sf").write(path)

    assert(sha256(path) === sha256(new File("src/test/resources/date-example.sf").toPath))
    assert(SkillState.read(path).Date.all.map(_.date).toList.sameElements(List(1, -1)))
  }

  test("§6.6 date example") {
    val path = tmpFile("write.make")

    val σ = SkillState.create
    σ.Date(1)
    σ.Date(-1)
    σ.write(path)

    assert(sha256(path) === sha256(new File("src/test/resources/date-example.sf").toPath))
    assert(SkillState.read(path).Date.all.map(_.date).toList.sameElements(List(1, -1)))
  }

  test("write 1.6M dates") {
    val low = -8e5.toInt
    val high = 8e5.toInt
    val path = tmpFile("write.10m")

    val σ = SkillState.create
    for (i ← low until high)
      σ.Date(i)

    σ.write(path)

    val d = SkillState.read(path).Date.all
    var cond = true
    for (i ← low until high)
      cond &&= (i == d.next.date)
    assert(cond, "match failed")
  }

  test("normalize 10MB v64") {
    val σ = SkillState.read("src/test/resources/normalizedInput.sf")

    var min = Long.MaxValue
    for (d ← σ.Date.all)
      min = Math.min(d.date, min)

    for (d ← σ.Date.all)
      d.date -= min

    σ.write(tmpFile("normalized"))
  }
}
