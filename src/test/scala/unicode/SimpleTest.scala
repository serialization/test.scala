package unicode

import common.CommonTest
import unicode.api._
import java.io.File
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SimpleTest extends CommonTest {

  test("create unicode example") {
    val path = tmpFile("unicode.writetest")

    val σ = SkillFile.open(path, Create, Write)
    σ.Unicode(one = "1", two = "ö", three = "☢")
    σ.close

    assert(sha256(path) === sha256(new File("src/test/resources/unicode-reference.sf").toPath))
  }

  test("check unicode example") {
    val path = new File("src/test/resources/unicode-reference.sf").toPath()

    val σ = SkillFile.open(path)
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
