package restrictions

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common.CommonTest
import de.ust.skill.common.scala.api.Create
import de.ust.skill.common.scala.api.Write
import de.ust.skill.common.scala.api.SkillException

@RunWith(classOf[JUnitRunner])
class FullTest extends CommonTest {
  import restrictions._;
  import restrictions.range.api.{ SkillFile ⇒ Range }
  import restrictions.singleton.api.{ SkillFile ⇒ Singleton }

  test("range") {
    val p = tmpFile("range")
    val sf = Range.open(p, Create, Write)
    intercept[SkillException] {
      sf.RangeRestricted.make(0.0f, 0, 0, 0, 0)
      sf.close
    }
  }

  test("singleton") {
    val p = tmpFile("singleton")
    val sf = Singleton.open(p, Create, Write)
    sf.LifeUniverseAndEverything.get.size = Float.NaN
    sf.close
  }
}