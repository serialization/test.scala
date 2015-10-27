package subtypes

import common.CommonTest
import subtypes.api._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import de.ust.skill.common.scala.api.Write
import de.ust.skill.common.scala.api.Create

@RunWith(classOf[JUnitRunner])
class BasicTest extends CommonTest {

  test("create and write") {
    val p = tmpFile("createAndWrite")

    val state = SkillFile.open(p, Create, Write)
    val c = state.C.make(null, null)
    c.a = c
    c.c = c
    state.close
  }

  test("insert a and b") {
    val σ = SkillFile.open(tmpFile("insertAB"), Create, Write)

    σ.A.make(σ.B.make(null, null))

    val b = σ.B.head
    b.a = b
    b.b = b

    σ.close
  }
}