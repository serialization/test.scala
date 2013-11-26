package subtypes

import common.CommonTest
import subtypes.api.SkillState
import subtypes.internal.SerializableState
import java.io.File

class SimpleTest extends CommonTest {

  test("subtypes read foreign") {
    val state = SkillState.read("annotationTest.sf")
    assert(null != state)
  }

  test("subtypes read; see §6.3.2") {
    val state = SkillState.read("localBasePoolStartIndex.sf")
    val types = "aabbbcbbddacd"

    // check types
    val actualTypes = state.getAs.map(_.getClass.getSimpleName.toLowerCase).mkString("");
    assert(actualTypes === types)

    // check fields (all fields are self-references)
    state.getAs.foreach { instance ⇒
      assert(instance.getA === instance)
    }
    state.getBs.foreach { instance ⇒
      assert(instance.getB === instance)
    }
    state.getCs.foreach { instance ⇒
      assert(instance.getC === instance)
    }
    state.getDs.foreach { instance ⇒
      assert(instance.getD === instance)
    }
  }

  test("subtypes write") {
    val path = tmpFile("writetest")

    val state = SkillState.read("localBasePoolStartIndex.sf")

    // check self references
    for ((instance, index) ← state.getAs.zipWithIndex) {
      assert(instance.getA.getSkillID === index + 1L, "index missmatch")
      assert(instance.getA === instance, "self reference corrupted")
    }

    state.write(path)

    // check self references again (write might not have restored them)
    for ((instance, index) ← state.getAs.zipWithIndex) {
      assert(instance.getA.getSkillID === index + 1L, "index missmatch after write")
      assert(instance.getA === instance, "self reference corrupted after write")
    }

    val state2 = SkillState.read(path)

    // check type of deserialized instances
    assert(state.getAsInTypeOrder.map(_.getClass.getSimpleName).sameElements(state2.getAsInTypeOrder.map(_.getClass.getSimpleName)))
  }
}