package restrictions

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import common.CommonTest

@RunWith(classOf[JUnitRunner])
class FullTest extends CommonTest {
  import restrictions._;
  import restrictions.range.api.{ SkillState ⇒ Range }
  import restrictions.singleton.api.{ SkillState ⇒ Singleton }

  ignore("range") {
    val p = tmpFile("range")
    val state = Range.create
    intercept[range.internal.SkillException] {
      state.RangeRestricted(0, 0, 0, 0.0f, 0)
      state.write(p)
    }
  }

  ignore("singleton") {
    val p = tmpFile("singleton")
    val state = Singleton.create
    state.LifeUniverseAndEverything.get.size = Float.NaN
    state.write(p)
  }
}