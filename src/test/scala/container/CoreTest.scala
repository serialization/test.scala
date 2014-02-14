package container

import org.junit.runner.RunWith
import common.CommonTest
import org.scalatest.junit.JUnitRunner
import junit.framework.Assert
import container.api.SkillState
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap

@RunWith(classOf[JUnitRunner])
class CoreTest extends CommonTest {

  // read unknown types
  test("read unknown: nodes") { assert(null != SkillState.read("node.sf")) }
  test("read unknown: two node blocks") { assert(null != SkillState.read("twoNodeBlocks.sf")) }
  test("read unknown: colored nodes") { Assert.assertNotNull(SkillState.read("coloredNodes.sf")) }
  test("read unknown: four colored nodes") { Assert.assertNotNull(SkillState.read("fourColoredNodes.sf")) }
  test("read unknown: empty blocks") { Assert.assertNotNull(SkillState.read("emptyBlocks.sf")) }
  test("read unknown: two types") { Assert.assertNotNull(SkillState.read("twoTypes.sf")) }
  test("read unknown: trivial type definition") { Assert.assertNotNull(SkillState.read("trivialType.sf")) }
  test("read unknown: nullable restricted null pointer") { SkillState.read("nullableNode.sf") }
  test("read unknown: null pointer in an annotation") { SkillState.read("nullAnnotation.sf") }

  // on-demand deserialization
  // TODO

  // append
  // TODO

  // full 64-bit support
  // TODO create an example with more than 4GiB size
  // TODO create an example with more than 2^32 instances

  // compound types
  test("create container instances") {
    val p = tmpFile("container.create")

    locally {
      val state = SkillState.create
      state.Container(ArrayBuffer(0, 0, 0), ArrayBuffer(1, 2, 3), ListBuffer(), Set().to, HashMap("f" -> HashMap(0L -> 0L)))
      for (c ← state.Container.all)
        c.s = c.arr.toSet.to

      state.write(p)
    }

    locally {
      val state = SkillState.read(p)
      val c = state.Container.all.next
      assert(c.arr.size === 3)
      assert(c.varr.sameElements(1 to 3))
      assert(c.l.isEmpty)
      assert(c.s.sameElements(0 to 0))
      assert(c.f("f")(c.s.head) == 0)
    }
  }
}