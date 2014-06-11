package unicode

import common.CommonTest
import unicode.api.SkillState
import java.io.File
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SimpleTest  extends CommonTest {
  import SkillState._

  test("create unicode example") {
    val path = new File("/home/feldentm/Desktop/unicode-reference.sf").toPath // tmpFile("unicode.writetest")

    val σ = create
    σ.Unicode("1", "ö", "☢")
    σ.write(path)

    assert(sha256(path) === sha256(new File("src/test/resources/unicode-reference.sf").toPath))
  }

  test("check unicode example") {
    val path = new File("src/test/resources/unicode-reference.sf").toPath()

    val σ = read(path)
    val u = σ.Unicode.all.next
    assert(σ.Unicode.size === 1)
    assert(u.one.length === 1, u.one)
    assert(u.one.getBytes.length === 1, u.one)
    assert(u.two.length === 1, u.two)
    assert(u.two.getBytes.length === 2, u.two)
    assert(u.three.length === 1, u.three)
    assert(u.three.getBytes.length === 3, u.three)
  }
}
