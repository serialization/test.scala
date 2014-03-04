package container

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import org.scalatest.junit.JUnitRunner
import common.CommonTest
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import java.io.File
import container.api.SkillState
import container.internal.TypeMissmatchError

@RunWith(classOf[JUnitRunner])
class CoreTest extends FunSuite {

  @inline final def tmpFile(s: String) = {
    val r = File.createTempFile(s, ".sf")
    r.deleteOnExit
    r.toPath
  }

  // read known types
  test("read known: container") { SkillState.read("src/test/resources/container.sf") }
  test("detect error: container") {
    val e = intercept[TypeMissmatchError](SkillState.read("src/test/resources/mayfail/containerTypeMissmatch.sf"))
    assert(e.getMessage === """During construction of container.someset: Encountered incompatible type "set<container>" (expected: set<<type definition name: somethingelse>>)""")
  }

  // read unknown types
  test("read unknown: nodes") { assert(null != SkillState.read("src/test/resources/node.sf")) }
  test("read unknown: two node blocks") { assert(null != SkillState.read("src/test/resources/twoNodeBlocks.sf")) }
  test("read unknown: colored nodes") { assert(null != SkillState.read("src/test/resources/coloredNodes.sf")) }
  test("read unknown: four colored nodes") { assert(null != SkillState.read("src/test/resources/fourColoredNodes.sf")) }
  test("read unknown: empty blocks") { assert(null != SkillState.read("src/test/resources/emptyBlocks.sf")) }
  test("read unknown: two types") { assert(null != SkillState.read("src/test/resources/twoTypes.sf")) }
  test("read unknown: trivial type definition") { assert(null != SkillState.read("src/test/resources/trivialType.sf")) }
  test("read unknown: nullable restricted null pointer") { SkillState.read("src/test/resources/nullableNode.sf") }
  test("read unknown: null pointer in an annotation") { SkillState.read("src/test/resources/nullAnnotation.sf") }
  test("read unknown: subtypes") { SkillState.read("src/test/resources/localBasePoolStartIndex.sf") }

  // on-demand deserialization
  // TODO

  // append
  // TODO

  // full 64-bit support
  // TODO create an example with more than 4GiB size
  // TODO create an example with more than 2^32 instances

  // compound types
  test("create container instances") {
    //    val p = tmpFile("container.create")
    //
    //    locally {
    //      val state = SkillState.create
    //      state.Container(
    //        Arr = ArrayBuffer(0, 0, 0),
    //        Varr = ArrayBuffer(1, 2, 3),
    //        L = ListBuffer(),
    //        S = Set().to,
    //        F = HashMap("f" -> HashMap(0L -> 0L)),
    //        SomeSet = Set().to
    //      )
    //      for (c ← state.Container.all)
    //        c.s = c.arr.toSet.to
    //
    //      state.write(p)
    //    }
    //
    //    locally {
    //      val state = SkillState.read(p)
    //      val c = state.Container.all.next
    //      assert(c.arr.size === 3)
    //      assert(c.varr.sameElements(1 to 3))
    //      assert(c.l.isEmpty)
    //      assert(c.s.sameElements(0 to 0))
    //      assert(c.f("f")(c.s.head) == 0)
    //    }
  }
}