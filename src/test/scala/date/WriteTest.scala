package date
import org.junit.runner.RunWith
import common.CommonTest
import date.api.SkillState
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class WriteTest extends CommonTest {

  test("§6.6 date example") {
    val path = tmpFile("writetest")

    val σ = SkillState.create
    σ.Date(1)
    σ.Date(-1)
    σ.write(path)

    assert(sha256(path) === sha256("date-example.sf"))
    assert(SkillState.read(path).Date.all.map(_.date).toList.sameElements(List(1, -1)))
  }

  test("write 1.6M dates") {
    val low = -8e5.toInt
    val high = 8e5.toInt
    val path = tmpFile("writetest")

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
    val σ = SkillState.read("normalizedInput.sf")

    var min = Long.MaxValue
    for (d ← σ.Date.all)
      min = Math.min(d.date, min)

    for (d ← σ.Date.all)
      d.date -= min

    σ.write(tmpFile("normalized"))
  }
}
