package date
import org.junit.Assert
import java.util.Random
import common.CommonTest
import date.api.SkillFile
import org.junit.runner.RunWith

class DatesMakerTest extends CommonTest {

  def read(s: String) = SkillFile.open("src/test/resources/"+s)

  def compareStates(sf: SkillFile, sf2: SkillFile) {
    var i1 = sf.Date.all
    var i2 = sf2.Date.all

    assert(i1.map(_.date).sameElements(i2.map(_.date)), "the argument states differ somehow")
  }

  test("write and read dates") {
    val state = read("date-example.sf")

    val out = tmpFile("test")
    state.changePath(out)
    state.close

    val sf2 = SkillFile.open(out)

    compareStates(SkillFile.open(out), sf2)
  }

  test("add a date") {
    val state = read("date-example.sf")

    state.Date(-15L)

    assert(state.Date.all.exists(_.date == -15L), "the added date does not exist!")
  }

  test("read, add, modify and write some dates") {
    val sf = read("date-example.sf")
    RandomDatesMaker.addLinearDates(sf, 98)
    for (d ← sf.Date.all)
      d.date = 0

    val out = tmpFile("oneHundredInts.sf")
    sf.changePath(out)
    sf.close

    val sf2 = SkillFile.open(out)

    compareStates(SkillFile.open(out), sf2)
    sf2.Date.all.foreach({ d ⇒ assert(d.date == 0) })
  }

  test("write and read some linear dates") {
    val sf = read("date-example.sf")
    Assert.assertNotNull(sf)
    RandomDatesMaker.addLinearDates(sf, 100)
    Assert.assertNotNull(sf)

    val out = tmpFile("someLinearDates.sf")
    sf.changePath(out)
    sf.close

    val sf2 = SkillFile.open(out)

    compareStates(SkillFile.open(out), sf2)
  }

  test("write and read some random dates") {
    val sf = read("date-example.sf")
    Assert.assertNotNull(sf)
    RandomDatesMaker.addDates(sf, 100)
    Assert.assertNotNull(sf)
    val out = tmpFile("someDates.sf")
    sf.changePath(out)
    sf.close

    val sf2 = SkillFile.open(out)

    compareStates(sf, sf2);
  }

  test("write and read a million random dates") {
    val sf = read("date-example.sf")
    Assert.assertNotNull(sf)
    RandomDatesMaker.addDates(sf, (1e6 - 2).toInt)
    Assert.assertNotNull(sf)

    val out = tmpFile("testOutWrite1MDatesNormal.sf")
    sf.changePath(out)
    sf.close

    compareStates(sf, SkillFile.open(out));
  }

  test("write and read a million small random dates") {
    val sf = read("date-example.sf")
    Assert.assertNotNull(sf)
    RandomDatesMaker.addDatesGaussian(sf, (1e6 - 2).toInt)
    Assert.assertNotNull(sf)

    val out = tmpFile("testOutWrite1MDatesGaussian.sf")
    sf.changePath(out)
    sf.close

    compareStates(sf, SkillFile.open(out));
  }

}

/**
 * Fills a serializable state with random dates.
 */
object RandomDatesMaker {

  /**
   * adds count new dates with linear content to sf
   */
  def addLinearDates(sf: SkillFile, count: Long) {
    for (i ← 0L until count)
      sf.Date(i)
  }

  /**
   * adds count new dates with random content to sf
   */
  def addDates(sf: SkillFile, count: Int) {
    var r = new Random()
    for (i ← 0 until count)
      sf.Date(r.nextLong())
  }

  /**
   * adds count new dates with random content to sf.
   *
   * uses a gaussian distribution, but only positive numbers
   */
  def addDatesGaussian(sf: SkillFile, count: Int) {
    var r = new Random()
    for (i ← 0 until count)
      sf.Date((r.nextGaussian().abs * 100).toLong)

  }
}