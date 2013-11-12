package subtypes

import common.CommonTest
import subtypes.api.SkillState
import subtypes.internal.SerializableState

class SimpleTest extends CommonTest {

  test("subtypes read foreign") {
    val state = SkillState.read("annotationTest.sf")
    state.asInstanceOf[SerializableState].dumpDebugInfo
  }

  test("subtypes read") {
    val state = SkillState.read("localBasePoolStartIndex.sf")
    state.asInstanceOf[SerializableState].dumpDebugInfo
  }
}