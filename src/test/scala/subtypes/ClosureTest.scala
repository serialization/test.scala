package subtypes

import common.CommonTest
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ClosureTest extends CommonTest {
  @inline def read(s : String = "localBasePoolOffset.sf") = OGFile.open("src/test/resources/" + s)

  /**
   * not fully implemented
   */
  test("closure by: error") {
    val sf = read()
    val sf2 = read()

    sf.closure

    sf.A.head.a = sf2.A.head

    ???
//    assert(null != intercept[ClosureException] {
//      sf.closure(ThrowException)
//    })
  }
}