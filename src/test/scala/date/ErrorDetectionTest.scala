package date

import java.io.File
import java.nio.file.FileSystems
import java.nio.file.NoSuchFileException
import java.nio.file.Path

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import common.CommonTest
import date.api._
import date.internal.ParseException
import date.internal.PoolSizeMissmatchError
import date.internal.TypeMissmatchError

/**
 * @author Timm Felden
 */
@RunWith(classOf[JUnitRunner])
class ErrorDetectionTest extends CommonTest {
  def read(s: String) = SkillFile.open(new File("src/test/resources/"+s).toPath, Read)

  test("read inexistent file") {
    val fileName = "IKillYouIfYouCreateThisFile.sf"
    val p = FileSystems.getDefault().getPath(fileName)
    val thrown = intercept[NoSuchFileException] {
      for (d ← SkillFile.open(p, Read).Date.all) println(d.prettyString)
    }
    assert(thrown.getMessage === fileName)
  }

  test("data chunk is too long") {
    val thrown = intercept[PoolSizeMissmatchError] {
      read("illformed/longerDataChunk.sf").Date.all
    }
    assert(thrown.getMessage === "Corrupted data chunk in block 1 between 0x14 and 0x17 in Field date.date of type: v64")
  }
  test("data chunk is too short") {
    val thrown = intercept[PoolSizeMissmatchError] {
      read("illformed/shorterDataChunk.sf").Date.all
    }
    assert(thrown.getMessage === "Corrupted data chunk in block 1 between 0x14 and 0x15 in Field date.date of type: v64")
  }
  test("incompatible field types") {
    val thrown = intercept[TypeMissmatchError] {
      read("illformed/incompatibleType.sf").Date.all
    }
    assert(thrown.getMessage === """During construction of date.date: Encountered incompatible type "date" (expected: v64)""")
  }
  test("reserved type ID") {
    val thrown = intercept[ParseException] {
      read("illformed/illegalTypeID.sf").Date.all
    }
    assert(thrown.getMessage === """In block 1 @0x12: Invalid type ID: 16""")
  }
  test("missing user type") {
    val thrown = intercept[ParseException] {
      read("illformed/missingUserType.sf").Date.all
    }
    assert(thrown.getMessage === "In block 1 @0x12: inexistent user type 1 (user types: 0 -> date)")
  }
  test("illegal string pool offset") {
    val thrown = intercept[ParseException] {
      read("illformed/illegalStringPoolOffsets.sf").Date.all
    }
    assert(thrown.getMessage === "In block 1 @0x5: corrupted string block")
  }

  test("duplicate type definition in the first block") {
    val thrown = intercept[ParseException] {
      read("illformed/duplicateDefinition.sf").Date.all
    }
    assert(thrown.getMessage === "In block 1 @0xd: Duplicate definition of type a")
  }
  test("append in the first block") {
    val thrown = intercept[ParseException] {
      read("illformed/duplicateDefinitionMixed.sf").Date.all
    }
    assert(thrown.getMessage === "In block 1 @0xd: Duplicate definition of type a")
  }
  test("duplicate append in the same block") {
    val thrown = intercept[ParseException] {
      read("illformed/duplicateDefinitionSecondBlock.sf").Date.all
    }
    assert(thrown.getMessage === "In block 2 @0x12: Duplicate definition of type a")
  }
}