package number

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common.CommonTest
import java.io.File
import number.api._

@RunWith(classOf[JUnitRunner])
class WriteTest extends CommonTest {

  test("write 1.2M numbers") {
    val startTime = System.nanoTime

    val limit: Int = 6e5.toInt
    val file = File.createTempFile("writetest", ".sf")
    val path = file.toPath()

    val σ = SkillFile.open(path, Create, Write)
    for (i ← -limit until limit)
      σ.Number(i)

    σ.close

    val d = SkillFile.open(path).Number.all
    var cond = true
    for (i ← -limit until limit)
      cond &&= (i == d.next.number)
    assert(cond, "match failed")

//    file.delete
  }
}