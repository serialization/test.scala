package date.internal

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import org.junit.Assert

class V64Test extends AssertionsForJUnit {

  import SerializableState.v64

  def check(v: Long) {
    Assert.assertEquals("intermediate: "+v64(v).toList.mkString(","), v, v64(v64(v)))
  }

  @Test def someCoding1 = check(1)
  @Test def someCoding2 = check(0)
  @Test def someCoding3 = check(-1)
  @Test def someCoding4 = check(31337)
  @Test def someCoding5 = check(127)
  @Test def someCoding6 = check(128)
  @Test def someCoding7 = check(-200)
  @Test def someCodingLong = check(391510817450516096L)
  @Test def someCodingLong2 = check(-391510817450516096L)
}