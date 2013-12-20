package date

import common.CommonTest
import date.internal.SkillException
import date.api.SkillState

/**
 * TODO improve checks
 * @author Timm Felden
 */
class ErrorDetectionTest extends CommonTest {

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