package subtypes

import common.CommonTest
import subtypes.api.SkillState
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BasicTest extends CommonTest {

  test("create and write") {
    val p = tmpFile("createAndWrite")

    val state = SkillState.create
    val c = state.C(null, null)
    c.a = c
    c.c = c
    state.write(p)
  }

}