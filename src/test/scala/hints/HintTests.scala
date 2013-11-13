package hints

import scala.reflect.Manifest
import org.scalatest.Engine
import org.scalatest.events.Formatter
import common.CommonTest
import hints.ignore.api.SkillState

class HintTests extends CommonTest {

  test("access ignored field") {
    val state = SkillState.read("date-example.sf")

    val err = intercept[IllegalAccessError] {
      state.getDates.next.date
    }
    assert(err.getMessage() === "date has an !ignore hint")
  }

  test("access ignored type") {
    val state = SkillState.read("node.sf")

    val err = intercept[IllegalAccessError] {
      state.getNodes.next.node
    }
    assert(err.getMessage() === "node has a type with an !ignore hint")
  }
}