package date

import common.CommonTest
import junit.framework.Assert
import date.api._

class CoreTest extends CommonTest {

  def read(s : String) = SkillFile.open("src/test/resources/"+s, Read)
  def check(name : String, size : Long) = assert(read(name).Date.size === size)

  // read unknown types
  test("read unknown: nodes")(check("node.sf", 0))
  test("read unknown: two node blocks")(check("twoNodeBlocks.sf", 0))
  test("read unknown: colored nodes")(check("coloredNodes.sf", 0))
  test("read unknown: four colored nodes")(check("fourColoredNodes.sf", 0))
  test("read unknown: empty blocks")(check("emptyBlocks.sf", 0))
  test("read unknown: two types")(check("twoTypes.sf", 0))
  test("read unknown: trivial type definition") { check("trivialType.sf", 0) }
  test("read unknown: nullable restricted null pointer") { check("nullableNode.sf", 0) }
  test("read unknown: null pointer in an annotation") { check("nullAnnotation.sf", 2) }

  // on-demand deserialization
  // TODO

  // append
  // TODO

  // full 64-bit support
  // TODO create an example with more than 4GiB size
  // TODO create an example with more than 2^32 instances

  // compound types
  //! @note not here

  // full reflection
  test("reflection: two types") {
    val σ = read("twoTypes.sf")
    val fields = (for (
      t ← σ.all;
      f ← t.allFields;
      i ← t.all
    ) yield s"${t.name}${f.name}").mkString(",")

    assert(fields.contains("aa"))
    assert(fields.contains("bb"))
  }
}