package subtypes

import common.CommonTest
import subtypes.api._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BasicTest extends CommonTest {

  test("create and write") {
    val p = tmpFile("createAndWrite")

    val state = SkillFile.open(p, Create, Write)
    val c = state.C(null, null)
    c.a = c
    c.c = c
    state.close
  }

  test("insert a and b") {
    val σ = SkillFile.open(tmpFile("insertAB"), Create, Write)

    σ.A(σ.B(null, null))

    val b = σ.B.all.next
    b.a = b
    b.b = b

    σ.close
  }
}