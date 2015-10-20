package subtypes

import common.CommonTest
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import subtypes.api.SkillFile
import de.ust.skill.common.scala.api.FieldDeclaration
import de.ust.skill.common.scala.api.Read
import de.ust.skill.common.scala.api.ReadOnly

@RunWith(classOf[JUnitRunner])
class FullTest extends CommonTest {
  @inline def read(s : String) = SkillFile.open("src/test/resources/"+s)

  /**
   * not fully implemented
   */
  test("delete and write") {
    val sf = read("localBasePoolOffset.sf")
    for (d ← sf.D.all)
      sf.delete(d)

    val path = tmpFile("delete")
    sf.changePath(path)
    sf.close

    assert(0 === SkillFile.open(path, Read, ReadOnly).D.size, "there should be no D, because we deleted them all!")
  }

  test("delete -- marked") {
    val σ = read("localBasePoolOffset.sf")
    for (d ← σ.D.all)
      σ.delete(d)

    assert(σ.D.all.forall(_.markedForDeletion), "some D is not marked for deletion?!")
  }

  ignore("reflective count ages") {
    val sf = SkillFile.open("../java/test/age16.sf")
    for (p ← sf; if p.name == "age") {
      val f = p.allFields.find(_.name == "age").get.asInstanceOf[FieldDeclaration[Long]];

      assert(53725 === p.all.count(_.get(f) == 0))
    }
  }
}