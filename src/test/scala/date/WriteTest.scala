package date

import java.io.File
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common.CommonTest
import date.api._
import de.ust.skill.common.scala.api.Write
import de.ust.skill.common.scala.api.Create
import de.ust.skill.common.scala.api.Read
import de.ust.skill.common.scala.api.ReadOnly

@RunWith(classOf[JUnitRunner])
class WriteTest extends CommonTest {

  test("double write") {
    val sf = SkillFile.create(tmpFile("double.write."))
    sf.Date.make(1)
    sf.Date.make(9)
    sf.Date.make(1989)

    sf.flush()
    sf.close
  }

  test("copy of §6.6 example") {
    val path = tmpFile("write.copy")

    val sf = SkillFile.open("src/test/resources/date-example.sf")
    sf.changePath(path)
    sf.close

    assert(sha256(path) === sha256(new File("src/test/resources/date-example.sf").toPath))
    assert(SkillFile.open(path, Read, ReadOnly).Date.all.map(_.date).toList.sameElements(List(1, -1)))
  }

  test("TR13 §6.6 date example") {
    val path = tmpFile("write.make")

    val σ = SkillFile.open(path, Create, Write)
    σ.Date.make(1)
    σ.Date.make(-1)
    σ.close

    assert(sha256(path) === sha256(new File("src/test/resources/date-example.sf").toPath))
    assert(SkillFile.open(path, Read, ReadOnly).Date.all.map(_.date).toList.sameElements(List(1, -1)))
  }

  test("write 1.6M dates") {
    val low = -8e5.toInt
    val high = 8e5.toInt
    val path = tmpFile("write.10m")

    val σ = SkillFile.open(path, Create, Write)
    for (i ← low until high)
      σ.Date.make(i)

    σ.close

    val d = SkillFile.open(path, Read, ReadOnly).Date.all
    assert(∀(low until high)(d.next.date == _), "match failed")
  }

  test("normalize 10MB v64") {
    val σ = SkillFile.open("src/test/resources/normalizedInput.sf")

    val min = σ.Date.foldLeft(Long.MaxValue) { case (m, d) ⇒ Math.min(m, d.date) }

    for (d ← σ.Date)
      d.date -= min

    σ.changePath(tmpFile("normalized"))
    σ.close
  }
}
