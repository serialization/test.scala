package number

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common.CommonTest
import java.io.File
import number.api.SkillState

@RunWith(classOf[JUnitRunner])
class WriteTest extends CommonTest {

  test("write 10MB numbers") {
    val startTime = System.nanoTime

    val limit: Int = 6e5.toInt
    val file = File.createTempFile("writetest", ".sf")
    val path = file.toPath()

    val σ = SkillState.create
    for (i ← -limit until limit)
      σ.addNumber(i)

    σ.write(path)

    val d = SkillState.read(path).getNumbers
    var cond = true
    for (i ← -limit until limit)
      cond &&= (i == d.next.getNumber)
    assert(cond, "match failed")

    assert(System.nanoTime - startTime < 10e9, s"test should run faster: ${(System.nanoTime - startTime).toDouble * 1e-9}")

//    file.delete
  }
}