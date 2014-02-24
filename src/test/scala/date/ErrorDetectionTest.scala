package date

import common.CommonTest
import date.internal.SkillException
import date.api.SkillState
import date.internal.TypeMissmatchError
import date.internal.PoolSizeMissmatchError
import date.internal.UnexpectedEOF
import date.internal.ParseException
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.io.File
import java.nio.file.Path
import java.nio.file.FileSystems
import java.io.IOException

/**
 * TODO improve checks
 * @author Timm Felden
 */
@RunWith(classOf[JUnitRunner])
class ErrorDetectionTest extends CommonTest {

  test("read inexistent file") {
    val p = FileSystems.getDefault().getPath("IKillYouIfYouCreateThisFile.sf")
    val thrown = intercept[IOException] {
      for (d ← SkillState.read(p).Date.all) println(d.prettyString)
    }
    assert(thrown.getMessage === s"The file $p does not exist.")
  }

  test("data chunk is too long") {
    val thrown = intercept[PoolSizeMissmatchError] {
      SkillState.read("illformed/longerDataChunk.sf").Date.all
    }
    assert(thrown.getMessage === "expected: 2, was: 3, field type: v64")
  }
  test("data chunk is too short") {
    val thrown = intercept[PoolSizeMissmatchError] {
      SkillState.read("illformed/shorterDataChunk.sf").Date.all
    }
    assert(thrown.getMessage === "expected: 2, was: 1, field type: v64")
  }
  test("incompatible field types") {
    val thrown = intercept[TypeMissmatchError] {
      SkillState.read("illformed/incompatibleType.sf").Date.all
    }
    assert(thrown.getMessage === """During construction of date.date: Encountered incompatible type "date" (expected: v64)""")
  }
  test("reserved type ID") {
    val thrown = intercept[ParseException] {
      SkillState.read("illformed/illegalTypeID.sf").Date.all
    }
    assert(thrown.getMessage === """In block 1 @0x11: Invalid type ID: 16""")
  }
  test("missing user type") {
    val thrown = intercept[ParseException] {
      SkillState.read("illformed/missingUserType.sf").Date.all
    }
    assert(thrown.getMessage === "In block 1 @0x13: date.date refers to inexistent user type 1 (user types: 0 -> date)")
  }
  test("illegal string pool offset") {
    val thrown = intercept[UnexpectedEOF] {
      SkillState.read("illformed/illegalStringPoolOffsets.sf").Date.all
    }
    assert(thrown.getMessage === "@5 while dropping 353 bytes")
  }
  test("missing field declarations in second block") {
    val thrown = intercept[ParseException] {
      SkillState.read("illformed/missingFieldInSecondBlock.sf").Date.all
    }
    assert(thrown.getMessage === "In block 2 @0x16: Type a has 0 fields (requires 1)")
  }

  test("duplicate type definition in the first block") {
    val thrown = intercept[ParseException] {
      SkillState.read("illformed/duplicateDefinition.sf").Date.all
    }
    assert(thrown.getMessage === "In block 1 @0xd: Duplicate definition of type a")
  }
  test("append in the first block") {
    val thrown = intercept[ParseException] {
      SkillState.read("illformed/duplicateDefinitionMixed.sf").Date.all
    }
    assert(thrown.getMessage === "In block 1 @0xd: Duplicate definition of type a")
  }
  test("duplicate append in the same block") {
    val thrown = intercept[ParseException] {
      SkillState.read("illformed/duplicateDefinitionSecondBlock.sf").Date.all
    }
    assert(thrown.getMessage === "In block 2 @0x12: Duplicate definition of type a")
  }
}