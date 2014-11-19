package subtypes

import common.CommonTest
import subtypes.api.SkillFile
import java.io.File
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

// we need to be internal to check skill IDs
// TODO make skillid available over the reflective interface; this will be the safe way to do this as of TR14! (skillID will be reserved for its purpose in this implementation)
package internal {
  /**
   * @author Timm Felden
   */
  @RunWith(classOf[JUnitRunner])
  class InternalTest extends CommonTest {
    @inline def read(s : String) = SkillFile.open("src/test/resources/"+s)

    ignore("subtypes read foreign") {
      val state = read("annotationTest.sf")
      assert(null != state)
    }

    ignore("simple read §6.3.2") {
      assert(null != read("localBasePoolStartIndex.sf"))
    }

    ignore("subtypes read; see §6.3.2") {
      val state = read("localBasePoolStartIndex.sf")
      val types = "aabbbcbbddacd"

      // check types
      val actualTypes = state.A.all.map(_.getClass.getSimpleName.toLowerCase).mkString("");
      assert(actualTypes === types)

      // check fields (all fields are self-references)
      for (a ← state.A.all)
        assert(a.a === a)
      for (b ← state.B.all)
        assert(b.b === b)
      for (c ← state.C.all)
        assert(c.c === c)
      for (d ← state.D.all)
        assert(d.d === d)
    }

    ignore("subtypes write") {
      val path = tmpFile("lbpsi.write")

      val state = read("localBasePoolStartIndex.sf")

      // check self references
      for ((instance, index) ← state.A.all.zipWithIndex) {
        assert(instance.a.getSkillID === index + 1L, "index missmatch")
        assert(instance.a === instance, "self reference corrupted")
      }

      fail("state.write(path)")

      // check self references again (write might not have restored them)
      for ((instance, index) ← state.A.all.zipWithIndex) {
        assert(instance.a.getSkillID === index + 1L, "index missmatch after write")
        assert(instance.a === instance, "self reference corrupted after write")
      }

      val state2 = SkillFile.open(path)

      // check type of deserialized instances
      assert(state.A.allInTypeOrder.map(_.getClass.getSimpleName).sameElements(state2.A.allInTypeOrder.map(_.getClass.getSimpleName)))
    }
  }
}