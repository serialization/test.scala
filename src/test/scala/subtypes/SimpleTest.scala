package subtypes

import common.CommonTest
import subtypes.api.SkillState
import subtypes.internal.SerializableState
import java.io.File

class SimpleTest extends CommonTest {

  test("subtypes read foreign") {
    val state = SkillState.read("annotationTest.sf")
    state.asInstanceOf[SerializableState].dumpDebugInfo
  }

  test("subtypes read") {
    val state = SkillState.read("localBasePoolStartIndex.sf")
    state.asInstanceOf[SerializableState].dumpDebugInfo
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