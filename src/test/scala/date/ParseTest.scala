package date

import org.junit.Assert
import common.CommonTest
import org.junit.runner.RunWith
import date.api.SkillState
import date.internal.SkillException
import date.internal.SerializableState

/**
 * Tests the file reading capabilities.
 */
class ParseTest extends CommonTest {

  test("two dates") {
    val σ = SkillState.read("date-example.sf")
    σ.asInstanceOf[SerializableState].dumpDebugInfo

    val it = σ.Date.all;
    Assert.assertEquals(1L, it.next.date)
    Assert.assertEquals(-1L, it.next.date)
    assert(!it.hasNext, "there shouldn't be any more elemnets!")
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
  test("nullable restricted null pointer") { SkillState.read("nullableNode.sf").Date.all }
  /**
   * null pointers are legal in annotations
   */
  test("null pointer in an annotation") { SkillState.read("nullAnnotation.sf").Date.all }

  /**
   * null pointers are not legal in regular fields
   *
   * @note this is the lazy case, i.e. the node pointer is never evaluated
   */
  test("null pointer in a nonnull field; lazy case!") {
    SkillState.read("illformed/nullNode.sf").Date.all
  }

  test("data chunk is too long") {
    intercept[SkillException] {
      SkillState.read("illformed/longerDataChunk.sf").Date.all
    }
  }
  test("data chunk is too short") {
    intercept[SkillException] {
      SkillState.read("illformed/shorterDataChunk.sf").Date.all
    }
  }
  test("incompatible field types") {
    intercept[SkillException] {
      SkillState.read("illformed/incompatibleType.sf").Date.all
    }
  }
  test("reserved type ID") {
    intercept[SkillException] {
      SkillState.read("illformed/illegalTypeID.sf").Date.all
    }
  }
  test("missing user type") {
    intercept[SkillException] {
      SkillState.read("illformed/missingUserType.sf").Date.all
    }
  }
  test("illegal string pool offset") {
    intercept[SkillException] {
      SkillState.read("illformed/illegalStringPoolOffsets.sf").Date.all
    }
  }
  test("missing field declarations in second block") {
    intercept[SkillException] {
      SkillState.read("illformed/missingFieldInSecondBlock.sf").Date.all
    }
  }
  test("duplicate type definition in the first block") {
    intercept[SkillException] {
      SkillState.read("illformed/duplicateDefinition.sf").Date.all
    }
  }
  test("append in the first block") {
    intercept[SkillException] {
      SkillState.read("illformed/duplicateDefinitionMixed.sf").Date.all
    }
  }
  test("duplicate append in the same block") {
    intercept[SkillException] {
      SkillState.read("illformed/duplicateDefinitionSecondBlock.sf").Date.all
    }
  }
}