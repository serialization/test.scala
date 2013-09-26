package date.internal
import org.junit.Assert
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit
import java.io.File

/**
 * Tests the file reading capabilities.
 */
class ParseTest extends AssertionsForJUnit {
  private implicit def nameToPath(s: String) = new File("src/test/resources/"+s).toPath

  @Test def testTwoDates: Unit = {
    val σ = SerializableState.read("test.sf")

    val it = σ getDates;
    Assert.assertEquals(1L, it.next.getDate)
    Assert.assertEquals(-1L, it.next.getDate)
    assert(!it.hasNext, "there shouldn't be any more elemnets!")
  }

  @Test def simpleNodes: Unit = {
    val σ = SerializableState.read("node.sf")
    Assert.assertNotNull(σ)
  }

  @Test def simpleTwoDates: Unit = {
    val σ = SerializableState.read("test.sf")
    Assert.assertNotNull(σ)
  }

  /**
   * see § 6.2.3.Fig.3
   */
  @Test def simpleTwoNodeBlocks: Unit = {
    val σ = SerializableState.read("twoNodeBlocks.sf")
    Assert.assertNotNull(σ)
    σ.dumpDebugInfo
  }

  /**
   * see § 6.2.3.Fig.4
   */
  @Test def simpleTwoColoredNodes: Unit = {
    val σ = SerializableState.read("coloredNodes.sf")
    Assert.assertNotNull(σ)
    σ.dumpDebugInfo
  }

  @Test def simpleFourColoredNodes: Unit = {
    val σ = SerializableState.read("fourColoredNodes.sf")
    Assert.assertNotNull(σ)
    σ.dumpDebugInfo
  }

  /**
   * read a degenerated file
   */
  @Test def emptyBlocks: Unit = {
    val σ = SerializableState.read("emptyBlocks.sf")
    Assert.assertNotNull(σ)
    σ.dumpDebugInfo
  }

  /**
   * read a file with two user types with instances referencing themselves
   */
  @Test def twoUserTypes: Unit = {
    val σ = SerializableState.read("twoTypes.sf")
    Assert.assertNotNull(σ)
    σ.dumpDebugInfo
  }

  /**
   * read a file, where the data chunk is too long
   */
  @Test(expected = classOf[SkillException]) def failLongerDataChunks: Unit = SerializableState.read(
    "illformed/longerDataChunk.sf").getDates

  /**
   * read a file, where the data chunk is too short
   */
  @Test(expected = classOf[SkillException]) def failShorterDataChunks: Unit = SerializableState.read(
    "illformed/shorterDataChunk.sf").getDates

  /**
   * read a file containing an incompatible field type
   */
  @Test(expected = classOf[SkillException]) def failTypeMissmatch: Unit = SerializableState.read(
    "illformed/incompatibleType.sf").getDates

  /**
   * read a file containing an illegal type ID
   */
  @Test(expected = classOf[SkillException])
  def failBuiltINTypeID: Unit = SerializableState.read("illformed/illegalTypeID.sf").getDates

  /**
   * read a file containing an type ID referring to an inexistent type
   */
  @Test(expected = classOf[SkillException])
  def failMissingUserType: Unit = SerializableState.read(
    "illformed/missingUserType.sf").getDates

  /**
   * has way to long string offset
   */
  @Test(expected = classOf[SkillException])
  def failBrokenStringOffsets: Unit = SerializableState.read("illformed/illegalStringPoolOffsets.sf").getDates

  /**
   * lacks a field definition in the second block
   */
  @Test(expected = classOf[SkillException])
  def missingFieldInSecondBlock: Unit = SerializableState.read("illformed/missingFieldInSecondBlock.sf")

  /**
   * duplicate type definition in the first block
   */
  @Test(expected = classOf[SkillException])
  def duplicateDefinitionFirstBlock: Unit = SerializableState.read("illformed/duplicateDefinition.sf")

  /**
   * duplicate type definition in the first block
   */
  @Test(expected = classOf[SkillException])
  def duplicateDefinitionFirstBlockMixed: Unit = SerializableState.read("illformed/duplicateDefinitionMixed.sf")

  /**
   * duplicate type definition in the second block
   */
  @Test(expected = classOf[SkillException])
  def duplicateDefinitionSecondBlock: Unit = SerializableState.read("illformed/duplicateDefinitionSecondBlock.sf")

  /**
   * checks if a file containing a trivial type can be loaded
   */
  @Test
  def trivialType: Unit = SerializableState.read("trivialType.sf")
}