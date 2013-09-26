package date.internal

import java.io.File
import org.junit.Assert
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit
import java.util.Random

class DatesMakerTest extends AssertionsForJUnit {
  private implicit def nameToPath(s: String) = new File("src/test/resources/"+s).toPath

  def compareStates(σ: SerializableState, σ2: SerializableState) {
    var i1 = σ.getDates
    var i2 = σ2.getDates

    assert(i1.map(_.getDate).sameElements(i2.map(_.getDate)), "the argument states differ somehow")
  }

  @Test def writeReadDates: Unit = {
    val state = SerializableState.read("test.sf")

    val out = File.createTempFile("test", ".sf").toPath()
    state.write(out)

    val σ2 = SerializableState.read(out)

    compareStates(SerializableState.read(out), σ2)
  }

  @Test def addDate: Unit = {
    val state = SerializableState.read("test.sf")

    val out = File.createTempFile("test", ".sf").toPath()

    state.addDate(-15)

    assert(!state.getDates.filter(_.getDate == -15L).isEmpty, "the added date does not exist!")
  }

  @Test def addModifyWriteDates: Unit = {
    val state = SerializableState.read(new File("testdata/test.sf").toPath)
    RandomDatesMaker.addLinearDates(state, 98)
    state.getDates.foreach(_.setDate(0))

    val out = new File("oneHundredInts.sf").toPath()
    state.write(out)

    val σ2 = SerializableState.read(out)

    compareStates(SerializableState.read(out), σ2)
    σ2.getDates.foreach({ d ⇒ assert(d.getDate == 0) })
  }

  @Test def addSomeLinearDates: Unit = {
    val σ = SerializableState.read(new File("testdata/test.sf").toPath)
    Assert.assertNotNull(σ)
    RandomDatesMaker.addLinearDates(σ, 100)
    Assert.assertNotNull(σ)

    val out = new File("someLinearDates.sf").toPath
    σ.write(out)

    val σ2 = SerializableState.read(out)

    compareStates(SerializableState.read(out), σ2)
  }

  @Test def addSomeDates: Unit = {
    val σ = SerializableState.read(new File("testdata/test.sf").toPath)
    Assert.assertNotNull(σ)
    RandomDatesMaker.addDates(σ, 100)
    Assert.assertNotNull(σ)
    σ.write(new File("someDates.sf").toPath)

    val σ2 = SerializableState.read(new File("someDates.sf").toPath)

    compareStates(σ, σ2);
  }

  @Test def write1MDatesNormal: Unit = {
    val σ = SerializableState.read(new File("testdata/test.sf").toPath)
    Assert.assertNotNull(σ)
    RandomDatesMaker.addDates(σ, (1e6 - 2).toInt)
    Assert.assertNotNull(σ)

    val out = new File("testOutWrite1MDatesNormal.sf").toPath()
    σ.write(out)

    compareStates(σ, SerializableState.read(out));
  }

  @Test def write1MDatesGaussian: Unit = {
    val σ = SerializableState.read(new File("testdata/test.sf").toPath)
    Assert.assertNotNull(σ)
    RandomDatesMaker.addDatesGaussian(σ, (1e6 - 2).toInt)
    Assert.assertNotNull(σ)

    val out = new File("testOutWrite1MDatesGaussian.sf").toPath()
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