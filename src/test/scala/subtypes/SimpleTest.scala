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
    assert(actualTypes === types, state.asInstanceOf[SerializableState].dumpDebugInfo)

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
    fail("write in type order")

    val file = File.createTempFile("writetest", ".sf")
    val path = file.toPath

    val state = SkillState.read("localBasePoolStartIndex.sf")

    state.write(path)
    val state2 = SkillState.read(path)
    assert(state.getAs.map(_.getClass.getSimpleName).sameElements(state2.getAs.map(_.getClass.getSimpleName)))
  }
}