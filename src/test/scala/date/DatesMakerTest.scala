package date
import org.junit.Assert
import java.util.Random
import common.CommonTest
import date.api.SkillState
import org.junit.runner.RunWith

class DatesMakerTest extends CommonTest {

  def read(s: String) = SkillState.read("src/test/resources/"+s)

  def compareStates(σ: SkillState, σ2: SkillState) {
    var i1 = σ.Date.all
    var i2 = σ2.Date.all

    assert(i1.map(_.date).sameElements(i2.map(_.date)), "the argument states differ somehow")
  }

  test("write and read dates") {
    val state = read("date-example.sf")

    val out = tmpFile("test")
    state.write(out)

    val σ2 = SkillState.read(out)

    compareStates(SkillState.read(out), σ2)
  }

  test("add a date") {
    val state = read("date-example.sf")

    val out = tmpFile("test")

    state.Date(-15L)

    assert(!state.Date.all.filter(_.date == -15L).isEmpty, "the added date does not exist!")
  }

  test("read, add, modify and write some dates") {
    val state = read("date-example.sf")
    RandomDatesMaker.addLinearDates(state, 98)
    for (d ← state.Date.all)
      d.date = 0

    val out = tmpFile("oneHundredInts.sf")
    state.write(out)

    val σ2 = SkillState.read(out)

    compareStates(SkillState.read(out), σ2)
    σ2.Date.all.foreach({ d ⇒ assert(d.date == 0) })
  }

  test("write and read some linear dates") {
    val σ = read("date-example.sf")
    Assert.assertNotNull(σ)
    RandomDatesMaker.addLinearDates(σ, 100)
    Assert.assertNotNull(σ)

    val out = tmpFile("someLinearDates.sf")
    σ.write(out)

    val σ2 = SkillState.read(out)

    compareStates(SkillState.read(out), σ2)
  }

  test("write and read some random dates") {
    val σ = read("date-example.sf")
    Assert.assertNotNull(σ)
    RandomDatesMaker.addDates(σ, 100)
    Assert.assertNotNull(σ)
    val out = tmpFile("someDates.sf")
    σ.write(out)

    val σ2 = SkillState.read(out)

    compareStates(σ, σ2);
  }

  test("write and read a million random dates") {
    val σ = read("date-example.sf")
    Assert.assertNotNull(σ)
    RandomDatesMaker.addDates(σ, (1e6 - 2).toInt)
    Assert.assertNotNull(σ)

    val out = tmpFile("testOutWrite1MDatesNormal.sf")
    σ.write(out)

    compareStates(σ, SkillState.read(out));
  }

  test("write and read a million small random dates") {
    val σ = read("date-example.sf")
    Assert.assertNotNull(σ)
    RandomDatesMaker.addDatesGaussian(σ, (1e6 - 2).toInt)
    Assert.assertNotNull(σ)

    val out = tmpFile("testOutWrite1MDatesGaussian.sf")
    σ.write(out)

    compareStates(σ, SkillState.read(out));
  }

}

/**
 * Fills a serializable state with random dates.
 */
object RandomDatesMaker {

  /**
   * adds count new dates with linear content to σ
   */
  def addLinearDates(σ: SkillState, count: Long) {
    for (i ← 0L until count)
      σ.Date(i)
  }

  /**
   * adds count new dates with random content to σ
   */
  def addDates(σ: SkillState, count: Int) {
    var r = new Random()
    for (i ← 0 until count)
      σ.Date(r.nextLong())
  }

  /**
   * adds count new dates with random content to σ.
   *
   * uses a gaussian distribution, but only positive numbers
   */
  def addDatesGaussian(σ: SkillState, count: Int) {
    var r = new Random()
    for (i ← 0 until count)
      σ.Date((r.nextGaussian().abs * 100).toLong)

  }
}