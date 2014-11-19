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
  ignore("delete and write") {
//    val σ = SkillState.read("localBasePoolStartIndex.sf")
//    for (d ← σ.D.all)
//      d.delete
//
//    val path = tmpFile("delete")
//    σ.write(path)
//
//    assert(0 === SkillState.read(path).D.size, "there should be no D")
  }

  ignore("delete -- marked") {
    val σ = SkillFile.open("localBasePoolStartIndex.sf")
    for (d ← σ.D.all)
      d.delete

    assert(σ.D.all.forall(_.markedForDeletion), "some D is not marked for deletion?!")
  }
}