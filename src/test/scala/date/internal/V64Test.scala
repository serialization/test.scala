package date.internal

import org.junit.Assert
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit

import date.internal.streams.OutBuffer

class V64Test extends AssertionsForJUnit {

  def v64(v: Long): Array[Byte] = {
    //@note use both v64 implementations, just in case
    val out = new OutBuffer()
    SerializationFunctions.v64(v, out)
    val r = new Array[Byte](out.size.toInt)
    SerializationFunctions.v64(v, r, 0)
    r
  }

  def v64(it: Iterator[Byte]): Long = {
    var count: Long = 0
    var rval: Long = 0
    var r: Long = it.next
    while (count < 8 && 0 != (r & 0x80)) {
      rval |= (r & 0x7f) << (7 * count);

      count += 1;
      r = it.next
    }
    rval = (rval | (count match {
      case 8 ⇒ r
      case _ ⇒ (r & 0x7f)
    }) << (7 * count));
    rval
  }

  def check(v: Long) {
    Assert.assertEquals("intermediate: "+v64(v).toList.mkString(","), v, v64(v64(v).iterator))
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