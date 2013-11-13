package node

import org.junit.Assert
import common.CommonTest
import node.api.SkillState
import node.internal.SkillException

/**
 * Tests the file reading capabilities.
 */
class ParseTest extends CommonTest {

  test("two dates") {
    SkillState.read("date-example.sf").getNodes
  }

  test("simple nodes") { Assert.assertNotNull(SkillState.read("node.sf")) }
  test("simple test") { Assert.assertNotNull(SkillState.read("date-example.sf")) }
  /**
   * @see § 6.2.3.Fig.3
   */
  test("two node blocks") { Assert.assertNotNull(SkillState.read("twoNodeBlocks.sf")) }
  /**
   * @see § 6.2.3.Fig.4
   */
  test("colored nodes") { Assert.assertNotNull(SkillState.read("coloredNodes.sf")) }
  test("four colored nodes") { Assert.assertNotNull(SkillState.read("fourColoredNodes.sf")) }
  test("empty blocks") { Assert.assertNotNull(SkillState.read("emptyBlocks.sf")) }
  test("two types") { Assert.assertNotNull(SkillState.read("twoTypes.sf")) }
  test("trivial type definition") { Assert.assertNotNull(SkillState.read("trivialType.sf")) }

  /**
   * null pointers are legal in regular fields if restricted to be nullable
   *  (although the behavior is not visible here due to lazyness)
   */
  test("nullable restricted null pointer") { SkillState.read("nullableNode.sf").getNodes }
  /**
   * null pointers are legal in annotations
   */
  test("null pointer in an annotation") { SkillState.read("nullAnnotation.sf").getNodes }

  /**
   * null pointers are not legal in regular fields
   *
   * @note this is the lazy case, i.e. the node pointer is never evaluated
   */
  test("null pointer in a nonnull field; lazy case!") {
    SkillState.read("illformed/nullNode.sf").getNodes
  }

  test("data chunk is too long; lazy case!") {
    SkillState.read("illformed/longerDataChunk.sf").getNodes
  }
  test("data chunk is too short; lazy case!") {
    SkillState.read("illformed/shorterDataChunk.sf").getNodes
  }
  test("incompatible field types; lazy case!") {
    SkillState.read("illformed/incompatibleType.sf").getNodes
  }
  test("reserved type ID") {
    intercept[SkillException] {
      SkillState.read("illformed/illegalTypeID.sf").getNodes
    }
  }
  test("missing user type") {
    intercept[SkillException] {
      SkillState.read("illformed/missingUserType.sf").getNodes
    }
  }
  test("illegal string pool offset") {
    intercept[SkillException] {
      SkillState.read("illformed/illegalStringPoolOffsets.sf").getNodes
    }
  }
  test("missing field declarations in second block") {
    intercept[SkillException] {
      SkillState.read("illformed/missingFieldInSecondBlock.sf").getNodes
    }
  }
  test("duplicate type definition in the first block") {
    intercept[SkillException] {
      SkillState.read("illformed/duplicateDefinition.sf").getNodes
    }
  }
  test("append in the first block") {
    intercept[SkillException] {
      SkillState.read("illformed/duplicateDefinitionMixed.sf").getNodes
    }
  }
  test("duplicate append in the same block") {
    intercept[SkillException] {
      SkillState.read("illformed/duplicateDefinitionSecondBlock.sf").getNodes
    }
  }
}