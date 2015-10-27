package node

import org.junit.Assert
import common.CommonTest
import node.api.SkillFile
import de.ust.skill.common.scala.api.PoolSizeMissmatchError
import de.ust.skill.common.scala.api.SkillException

/**
 * Tests the file reading capabilities.
 */
class ParseTest extends CommonTest {
  @inline def read(s : String) = SkillFile.open("src/test/resources/"+s)

  test("two dates") {
    read("date-example.sf").Node.all
  }

  test("simple nodes") { Assert.assertNotNull(read("node.sf")) }
  test("simple test") { Assert.assertNotNull(read("date-example.sf")) }
  /**
   * @see § 6.2.3.Fig.3
   */
  test("two node blocks") { Assert.assertNotNull(read("twoNodeBlocks.sf")) }
  /**
   * @see § 6.2.3.Fig.4
   */
  test("colored nodes") { Assert.assertNotNull(read("coloredNodes.sf")) }
  test("four colored nodes") { Assert.assertNotNull(read("fourColoredNodes.sf")) }
  test("empty blocks") { Assert.assertNotNull(read("emptyBlocks.sf")) }
  test("two types") { Assert.assertNotNull(read("twoTypes.sf")) }
  test("trivial type definition") { Assert.assertNotNull(read("trivialType.sf")) }

  test("data chunk is too long -- reflective read") {
    val thrown = intercept[PoolSizeMissmatchError] {
      val sf = read("illformed/longerDataChunk.sf")
      for (t ← sf; i ← t.all; f ← t.allFields)
        println(s"${i.prettyString}.${f.name} = ${i.get(f)}")
    }
    assert(thrown.getMessage === """Corrupted data chunk in block 1 between 0x14 and 0x17
 Field date.date of type: v64
 Last position: 16""")
  }
  test("data chunk is too short -- reflective read") {
    val thrown = intercept[PoolSizeMissmatchError] {
      val sf = read("illformed/shorterDataChunk.sf")
      for (t ← sf; i ← t.all; f ← t.allFields)
        println(s"${i.prettyString}.${f.name} = ${i.get(f)}")
    }
    assert(thrown.getMessage === """Corrupted data chunk in block 1 between 0x14 and 0x15
 Field date.date of type: v64
 Last position: 15""")
  }
  test("incompatible field types; lazy case!") {
    read("illformed/incompatibleType.sf").Node.all
  }
  test("reserved type ID") {
    intercept[SkillException] {
      read("illformed/illegalTypeID.sf").Node.all
    }
  }
  test("missing user type") {
    intercept[SkillException] {
      read("illformed/missingUserType.sf").Node.all
    }
  }
  test("illegal string pool offset") {
    intercept[SkillException] {
      read("illformed/illegalStringPoolOffsets.sf").Node.all
    }
  }
  test("duplicate type definition in the first block") {
    intercept[SkillException] {
      read("illformed/duplicateDefinition.sf").Node.all
    }
  }
  test("append in the first block") {
    intercept[SkillException] {
      read("illformed/duplicateDefinitionMixed.sf").Node.all
    }
  }
  test("duplicate append in the same block") {
    intercept[SkillException] {
      read("illformed/duplicateDefinitionSecondBlock.sf").Node.all
    }
  }
  test("missing type block") {
    intercept[SkillException] {
      read("illformed/missingTypeBlock.sf").Node.all
    }
  }
}
