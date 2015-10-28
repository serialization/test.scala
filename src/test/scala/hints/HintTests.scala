package hints

import common.CommonTest
import hints.ignore.api.SkillFile

class HintTests extends CommonTest {
  def read(s : String) = SkillFile.open("src/test/resources/"+s)

  test("access ignored field") {
    val state = read("date-example.sf")

    val err = intercept[IllegalAccessError] {
      state.Date.all.next.date
    }
    assert(err.getMessage() === "date has an !ignore hint")
  }

  test("access ignored type") {
    val state = read("node.sf")

    val err = intercept[IllegalAccessError] {
      state.Node.all.next.node
    }
    assert(err.getMessage() === "node has a type with an !ignore hint")
  }
}