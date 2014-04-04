package floatTest

import common.CommonTest
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import api.SkillState
import java.text.DecimalFormat
import java.math.RoundingMode

@RunWith(classOf[JUnitRunner])
class BasicTest extends CommonTest {

  test("create, write, check") {
    val p = tmpFile("float.check")

    locally {
      val state = SkillState.create
      val f = state.FloatTest.get

      f.zero = 0
      f.minusZero = 1 / Float.NegativeInfinity
      f.two = 2.0f
      f.pi = Math.PI.toFloat
      f.NaN = Float.NaN

      val d = state.DoubleTest.get

      d.zero = 0
      d.minusZero = 1 / Double.NegativeInfinity
      d.two = 2.0
      d.pi = Math.PI
      d.NaN = Double.NaN

      state.write(p)
    }

    val state = SkillState.read(p)
    val f = state.FloatTest.get

    assert(f.zero === 0)
    assert(f.minusZero === 1 / Float.NegativeInfinity)
    assert(f.two === 2.0f)
    assert(f.pi === Math.PI.toFloat)
    assert(f.NaN != f.NaN)

    val d = state.DoubleTest.get

    assert(d.zero === 0)
    assert(d.minusZero === 1 / Double.NegativeInfinity)
    assert(d.two === 2.0)
    assert(d.pi === Math.PI)
    assert(d.NaN != d.NaN)
  }

}