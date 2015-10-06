package floatTest

import common.CommonTest
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.text.DecimalFormat
import java.math.RoundingMode
import floats.api.SkillFile
import de.ust.skill.common.scala.api.Write
import de.ust.skill.common.scala.api.Read
import de.ust.skill.common.scala.api.Create
import de.ust.skill.common.scala.api.ReadOnly

@RunWith(classOf[JUnitRunner])
class BasicTest extends CommonTest {

  test("create, write, check") {
    val p = tmpFile("float.check")

    locally {
      val state = SkillFile.open(p, Create, Write)
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

      state.close
    }

    val state = SkillFile.open(p, Read, ReadOnly)
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