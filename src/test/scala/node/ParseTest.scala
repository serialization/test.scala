package node

import org.junit.Assert
import common.CommonTest
import node.api.SkillState
import node.internal.SkillException

/**
 * Tests the file reading capabilities.
 */
class ParseTest extends CommonTest {
  @inline def read(s: String) = SkillState.read("src/test/resources/"+s)

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

  /**
   * null pointers are legal in regular fields if restricted to be nullable
   *  (although the behavior is not visible here due to lazyness)
   */
  test("nullable restricted null pointer") { read("nullableNode.sf").Node.all }
  /**
   * null pointers are legal in annotations
   */
  test("null pointer in an annotation") { read("nullAnnotation.sf").Node.all }

  /**
   * null pointers are not legal in regular fields
   *
   * @note this is the lazy case, i.e. the node pointer is never evaluated
   */
  test("null pointer in a nonnull field; lazy case!") {
    read("illformed/nullNode.sf").Node.all
  }

  test("data chunk is too long; lazy case!") {
    read("illformed/longerDataChunk.sf").Node.all
  }
  test("data chunk is too short; lazy case!") {
    read("illformed/shorterDataChunk.sf").Node.all
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
  test("missing field declarations in second block") {
    intercept[SkillException] {
      read("illformed/missingFieldInSecondBlock.sf").Node.all
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