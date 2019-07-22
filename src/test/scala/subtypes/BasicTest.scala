package subtypes

import common.CommonTest
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import ogss.common.scala.api.Write
import ogss.common.scala.api.Create

@RunWith(classOf[JUnitRunner])
class BasicTest extends CommonTest {

  test("create and write") {
    val p = tmpFile("createAndWrite")

    val state = OGFile.open(p, Create, Write)
    val c = state.C.make
    c.a = c
    c.c = c
    state.close
  }

  test("insert a and b") {
    val σ = OGFile.open(tmpFile("insertAB"), Create, Write)

    σ.A.build.a(σ.B.make).make

    val b = σ.B.head
    b.a = b
    b.b = b

    σ.close
  }
}