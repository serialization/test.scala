package number

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common.CommonTest
import java.io.File
import number.api.SkillState

@RunWith(classOf[JUnitRunner])
class WriteTest extends CommonTest {

  test("write 10001 numbers") {
    val file = File.createTempFile("writetest", ".sf")
    val path = file.toPath()

    val σ = SkillState.create
    (-5000 to 5000) foreach (σ.addNumber(_))
    σ.write(path)

    assert(SkillState.read(path).getNumbers.map(_.number).toList.sameElements((-5000 to 5000).toList))

    file.delete
  }
}