package date

import common.CommonTest
import junit.framework.Assert
import date.api.SkillState

class CoreTest extends CommonTest {

  def read(s: String) = SkillState.read("src/test/resources/"+s)

  // read unknown types
  test("read unknown: nodes") { assert(null != read("node.sf")) }
  test("read unknown: two node blocks") { assert(null != read("twoNodeBlocks.sf")) }
  test("read unknown: colored nodes") { Assert.assertNotNull(read("coloredNodes.sf")) }
  test("read unknown: four colored nodes") { Assert.assertNotNull(read("fourColoredNodes.sf")) }
  test("read unknown: empty blocks") { Assert.assertNotNull(read("emptyBlocks.sf")) }
  test("read unknown: two types") { Assert.assertNotNull(read("twoTypes.sf")) }
  test("read unknown: trivial type definition") { Assert.assertNotNull(read("trivialType.sf")) }
  test("read unknown: nullable restricted null pointer") { read("nullableNode.sf").Date.all }
  test("read unknown: null pointer in an annotation") { read("nullAnnotation.sf").Date.all }

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