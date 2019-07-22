package completeEnum

import common.CommonTest
import ogss.common.scala.api.Write
import ogss.common.scala.api.Read
import ogss.common.scala.api.ReadOnly
import ogss.common.scala.api.Create

/**
 * Tests the file reading capabilities.
 */
class BasicTest extends CommonTest {

  test("enums - create") {
    val sf = OGFile.open(tmpFile("enum.create"), Create, ReadOnly)
    assert(sf.Brot.exists(_.target == Brot.Roggen))
  }

  test("enums - create and close empty") {
    val sf = OGFile.open(tmpFile("enum.create"), Create, Write)
    sf.close
  }

  test("enums - create & flush") {
    val sf = OGFile.open(tmpFile("enum.create"), Create, Write)
    sf.flush
    
    assert(sf.Brot.exists(_.target == Brot.Roggen))
  }
}