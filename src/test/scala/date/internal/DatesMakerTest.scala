package date.internal

import java.io.File
import org.junit.Assert
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit
import java.util.Random
import common.CommonTest
import java.nio.file.Files

class DatesMakerTest extends CommonTest {

  def compareStates(σ: SerializableState, σ2: SerializableState) {
    var i1 = σ.getDates
    var i2 = σ2.getDates

    assert(i1.map(_.getDate).sameElements(i2.map(_.getDate)), "the argument states differ somehow")
  }

  test("write and read dates") {
    val state = SerializableState.read("date-example.sf")

    val out = tmpFile("test")
    state.write(out)

    val σ2 = SerializableState.read(out)

    compareStates(SerializableState.read(out), σ2)
  }

  test("add a date") {
    val state = SerializableState.read("date-example.sf")

    val out = tmpFile("test")

    state.addDate(-15)

    assert(!state.getDates.filter(_.getDate == -15L).isEmpty, "the added date does not exist!")
  }

  test("read, add, modify and write some dates") {
    val state = SerializableState.read("date-example.sf")
    RandomDatesMaker.addLinearDates(state, 98)
    state.getDates.foreach(_.setDate(0))

    val out = tmpFile("oneHundredInts.sf")
    state.write(out)

    val σ2 = SerializableState.read(out)

    compareStates(SerializableState.read(out), σ2)
    σ2.getDates.foreach({ d ⇒ assert(d.getDate == 0) })
  }

  test("write and read some linear dates"){
    val σ = SerializableState.read("date-example.sf")
    Assert.assertNotNull(σ)
    RandomDatesMaker.addLinearDates(σ, 100)
    Assert.assertNotNull(σ)

    val out = tmpFile("someLinearDates.sf")
    σ.write(out)

    val σ2 = SerializableState.read(out)

    compareStates(SerializableState.read(out), σ2)
  }

  test("write and read some random dates"){
    val σ = SerializableState.read("date-example.sf")
    Assert.assertNotNull(σ)
    RandomDatesMaker.addDates(σ, 100)
    Assert.assertNotNull(σ)
    val out = tmpFile("someDates.sf")
    σ.write(out)

    val σ2 = SerializableState.read(out)

    compareStates(σ, σ2);
  }

  test("write and read a million random dates"){
    val σ = SerializableState.read("date-example.sf")
    Assert.assertNotNull(σ)
    RandomDatesMaker.addDates(σ, (1e6 - 2).toInt)
    Assert.assertNotNull(σ)

    val out = tmpFile("testOutWrite1MDatesNormal.sf")
    σ.write(out)

    compareStates(σ, SerializableState.read(out));
  }

  test("write and read a million small random dates"){
    val σ = SerializableState.read("date-example.sf")
    Assert.assertNotNull(σ)
    RandomDatesMaker.addDatesGaussian(σ, (1e6 - 2).toInt)
    Assert.assertNotNull(σ)

    val out = tmpFile("testOutWrite1MDatesGaussian.sf")
    σ.write(out)

    compareStates(σ, SerializableState.read(out));
  }

}

/**
 * Fills a serializable state with random dates.
 */
object RandomDatesMaker {

  /**
   * adds count new dates with linear content to σ
   */
  def addLinearDates(σ: SerializableState, count: Int) {
    for (i ← 0 until count)
      σ.addDate(i)
  }

  /**
   * adds count new dates with random content to σ
   */
  def addDates(σ: SerializableState, count: Int) {
    var r = new Random()
    for (i ← 0 until count)
      σ.addDate(r.nextLong())
  }

  /**
   * adds count new dates with random content to σ.
   *
   * uses a gaussian distribution, but only positive numbers
   */
  def addDatesGaussian(σ: SerializableState, count: Int) {
    var r = new Random()
    for (i ← 0 until count)
      σ.addDate((r.nextGaussian().abs * 100).toLong)

  }
}