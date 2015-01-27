package subtypes

import common.CommonTest
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import subtypes.api.SkillFile

@RunWith(classOf[JUnitRunner])
class FullTest extends CommonTest {

  /**
   * not fully implemented
   */
  test("delete and write") {
    val sf = SkillFile.open("localBasePoolOffset.sf")
    for (d ← sf.D.all)
      d.delete

    val path = tmpFile("delete")
    sf.changePath(path)
    sf.close

    assert(0 === SkillFile.open(path).D.size, "there should be no D, because we deleted them all!")
  }

  test("delete -- marked") {
    val σ = SkillFile.open("localBasePoolOffset.sf")
    for (d ← σ.D.all)
      d.delete

    assert(σ.D.all.forall(_.markedForDeletion), "some D is not marked for deletion?!")
  }
}