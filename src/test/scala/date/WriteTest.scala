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

  test("write 10MB dates") {
    val startTime = System.nanoTime

    val limit: Int = 8e5.toInt
    val file = File.createTempFile("writetest", ".sf")
    val path = file.toPath()

    val σ = SkillState.create
    for (i ← -limit until limit)
      σ.addDate(i)

    σ.write(path)

    val d = SkillState.read(path).getDates
    var cond = true
    for (i ← -limit until limit)
      cond &&= (i == d.next.getDate)
    assert(cond, "match failed")

    assert(System.nanoTime - startTime < 2e9, s"test should run faster: ${(System.nanoTime - startTime).toDouble * 1e-9}")

    file.delete
  }
}
