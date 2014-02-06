package number

import common.CommonTest
import number.api.SkillState
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import scala.util.Random

/**
 * This test is used to produce results for the WSR'14 paper.
 *
 * run with: -XX:MaxHeapFreeRatio=70 -Xmx4G -Xms4G
 * @author Timm Felden
 */
@RunWith(classOf[JUnitRunner])
class WSR14Test extends CommonTest {

  val counts = Array(10, 1000, 100000)

  def averageOut(test: ⇒ Unit) {
    val t = System.nanoTime
    for (count ← 0 until 100)
      test

    print((System.nanoTime - t).toDouble * 1e-8)
  }

  test("linear") {
    val f = tmpFile("wsr");

    @inline def t(n: Int) {
      val σ = SkillState.create;
      for (i ← 0 to n)
        σ.Number(i)
      σ.write(f)
    }

    // produces nice latex output
    // run twice to get rid of JIT effects
    for (jit ← 0 to 1) {
      print("create")
      for (n ← counts) {
        print(" & ")
        averageOut(t(n))
      }
      print("\\\\\n")
    }
  }

  test("randomize") {
    val f = tmpFile("wsr");

    def make(n: Int) {
      val σ = SkillState.create;
      for (i ← 0 to n)
        σ.Number(0)
      σ.write(f)
    }

    def t(n: Int) {
      val σ = SkillState.read(f);
      for (n ← σ.Number.all)
        n.number = Random.nextLong
      σ.write(f)
    }

    // produces nice latex output
    // run twice to get rid of JIT effects
    for (jit ← 0 to 1) {
      print("randomize")
      for (n ← counts) {
        print(" & ")
        make(n)
        averageOut(t(n))
      }
      print("\\\\\n")
    }
  }

  test("sort") {
    val f = tmpFile("wsr_random");
    val out = tmpFile("wsr_sorted");
    @inline def make(n: Int) {
      val σ = SkillState.create;
      for (i ← 0 to n)
        σ.Number(Random.nextLong)
      σ.write(f)
    }

    @inline def t(n: Int) {
      val σ = SkillState.read(f);
      σ.Number.all.toBuffer.sortBy[Long](_.number)
      σ.write(out)
    }

    // produces nice latex output
    // run twice to get rid of JIT effects
    for (jit ← 0 to 1) {
      print("sort")
      for (n ← counts) {
        print(" & ")
        make(n)
        averageOut(t(n))
      }
      print("\\\\\n")
    }
  }
}