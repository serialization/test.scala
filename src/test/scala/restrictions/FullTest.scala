package restrictions

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common.CommonTest

@RunWith(classOf[JUnitRunner])
class FullTest extends CommonTest {
  import restrictions._;
  import restrictions.range.api.{ SkillFile ⇒ Range }
  import restrictions.singleton.api.{ SkillFile ⇒ Singleton }

  test("range") {
    import restrictions.range.api.Create

    val p = tmpFile("range")
    val sf = Range.open(p, Create)
    intercept[range.internal.SkillException] {
      sf.RangeRestricted(0.0f, 0, 0, 0, 0)
      sf.close
    }
  }

  ignore("singleton") {
    import restrictions.singleton.api.Create

    val p = tmpFile("singleton")
    val sf = Singleton.open(p, Create)
    sf.LifeUniverseAndEverything.get.size = Float.NaN
    sf.close
  }
}