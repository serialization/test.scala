package subtypes

import common.CommonTest
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import subtypes.api.SkillFile
import de.ust.skill.common.scala.api.FieldDeclaration
import de.ust.skill.common.scala.api.Read
import de.ust.skill.common.scala.api.ReadOnly
import de.ust.skill.common.scala.api.SkillException
import de.ust.skill.common.scala.api.ThrowException
import de.ust.skill.common.scala.api.ClosureException
import de.ust.skill.common.scala.api.NoClosure

@RunWith(classOf[JUnitRunner])
class ClosureTest extends CommonTest {
  @inline def read(s : String = "localBasePoolOffset.sf") = SkillFile.open("src/test/resources/" + s)

  /**
   * not fully implemented
   */
  test("closure by: error") {
    val sf = read()
    val sf2 = read()

    sf.closure(ThrowException)

    sf.A.head.a = sf2.A.head

    assert(null != intercept[ClosureException] {
      sf.closure(ThrowException)
    })
  }
  
  /**
   * not fully implemented
   */
  test("closure by: none") {
    val sf = read()
    val sf2 = read()

    sf.closure(NoClosure)

    sf.A.head.a = sf2.A.head
    
    // no check -> no detection
    sf.closure(NoClosure)
  }
}