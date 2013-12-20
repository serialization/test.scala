package date

import common.CommonTest
import junit.framework.Assert
import date.api.SkillState

class CoreTest extends CommonTest {

  // read unknown types
  test("read unknown: nodes") { assert(null != SkillState.read("node.sf")) }
  test("read unknown: two node blocks") { assert(null != SkillState.read("twoNodeBlocks.sf")) }
  test("read unknown: colored nodes") { Assert.assertNotNull(SkillState.read("coloredNodes.sf")) }
  test("read unknown: four colored nodes") { Assert.assertNotNull(SkillState.read("fourColoredNodes.sf")) }
  test("read unknown: empty blocks") { Assert.assertNotNull(SkillState.read("emptyBlocks.sf")) }
  test("read unknown: two types") { Assert.assertNotNull(SkillState.read("twoTypes.sf")) }
  test("read unknown: trivial type definition") { Assert.assertNotNull(SkillState.read("trivialType.sf")) }
  test("read unknown: nullable restricted null pointer") { SkillState.read("nullableNode.sf").Date.all }
  test("read unknown: null pointer in an annotation") { SkillState.read("nullAnnotation.sf").Date.all }

  // on-demand deserialization
  // TODO

  // append
  // TODO

  // full 64-bit support
  // TODO create an example with more than 4GiB size
  // TODO create an example with more than 2^32 instances

  // compound types
  //! @note not here
}