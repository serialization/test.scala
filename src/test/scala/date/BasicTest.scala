package date

import common.CommonTest
import date.api._
import junit.framework.Assert
import java.io.File
import de.ust.skill.common.scala.api.Write
import de.ust.skill.common.scala.api.Read
import de.ust.skill.common.scala.api.Create

/**
 * @author Timm Felden
 */
class BasicTest extends CommonTest {
  def read(s : String) = SkillFile.open(new File("src/test/resources/"+s).toPath, Read, Write)

  // read exact files
  test("read date example") {
    assert(null != read("date-example.sf"))
  }

  test("check contents of date example") {
    val state = read("date-example.sf")

    val iterator = state.Date.all;
    Assert.assertEquals(1L, iterator.next.date)
    Assert.assertEquals(-1L, iterator.next.date)
    assert(!iterator.hasNext, "there shouldn't be any more elemnets!")
  }

  test("read empty blocks") { Assert.assertNotNull(read("emptyBlocks.sf")) }

  // create state
  test("create state") {
    val p = tmpFile("create")
    val state = SkillFile.open(p, Create, Write)

    state.Date.make(1)
    state.Date.make(-1)

    assert(2 === state.Date.size)
    val seq : Seq[Date] = state.Date.all.to
    assert(1L === seq.head.date)
    assert(-1L === seq.last.date)
  }

  // write
  test("create and write") {
    val p = tmpFile("create")
    val state = SkillFile.open(p, Create, Write)

    state.Date.make(1)
    state.Date.make(-1)

    state.close
  }

  test("read and write date example") {
    val p = tmpFile("dateExample")

    val sf = read("date-example.sf")
    sf.changePath(p)
    sf.close

    assert(sha256(p) === sha256(new File("src/test/resources/date-example.sf").toPath), "the file did not match the expected output")
  }

  test("create state, write and check against example") {
    val p = tmpFile("create")
    val state = SkillFile.open(p, Create, Write)

    state.Date.make(1)
    state.Date.make(-1)

    state.close

    assert(sha256(p) === sha256(new File("src/test/resources/date-example.sf").toPath), "the file did not match the expected output")
  }
}