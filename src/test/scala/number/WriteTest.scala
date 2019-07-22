package number

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common.CommonTest
import java.io.File
import ogss.common.scala.api.Write
import ogss.common.scala.api.Read
import ogss.common.scala.api.ReadOnly
import ogss.common.scala.api.Create

@RunWith(classOf[JUnitRunner])
class WriteTest extends CommonTest {

  test("write 1.2M numbers") {
    val startTime = System.nanoTime

    val limit : Int = 6e5.toInt
    val file = File.createTempFile("writetest", ".sf")
    val path = file.toPath()

    val σ = OGFile.open(path, Create, Write)
    for (i ← -limit until limit)
      σ.Number.build.number(i).make

    σ.close

    val d = OGFile.open(path, Read, ReadOnly).Number.iterator
    var cond = true
    for (i ← -limit until limit)
      cond &&= (i == d.next.number)
    assert(cond, "match failed")

    //    file.delete
  }
}