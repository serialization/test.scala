package date

import common.CommonTest
import date.api.SkillState
import junit.framework.Assert

/**
 * @author Timm Felden
 */
class BasicTest extends CommonTest {

  // read exact files
  test("read date example") {
    assert(null != SkillState.read("date-example.sf"))
  }

  test("check contents of date example") {
    val state = SkillState.read("date-example.sf")

    val iterator = state.Date.all;
    Assert.assertEquals(1L, iterator.next.date)
    Assert.assertEquals(-1L, iterator.next.date)
    assert(!iterator.hasNext, "there shouldn't be any more elemnets!")
  }

  test("read empty blocks") { Assert.assertNotNull(SkillState.read("emptyBlocks.sf")) }

  // create state
  test("create state") {
    val p = tmpFile("create")
    val state = SkillState.create

    state.Date(1)
    state.Date(-1)

    assert(2 === state.Date.size)
    val seq: Seq[Date] = state.Date.all.to
    assert(1L === seq.head.date)
    assert(-1L === seq.last.date)
  }

  // write
  test("create and write") {
    val p = tmpFile("create")
    val state = SkillState.create

    state.Date(1)
    state.Date(-1)

    state.write(p)
  }

  test("read and write date example") {
    val p = tmpFile("dateExample")

    SkillState.read("date-example.sf").write(p)

    assert(sha256(p) === sha256("date-example.sf"), "the file did not match the expected output")
  }

  test("create state, write and check against example") {
    val p = tmpFile("create")
    val state = SkillState.create

    state.Date(1)
    state.Date(-1)

    state.write(p)

    assert(sha256(p) === sha256("date-example.sf"), "the file did not match the expected output")
  }
}