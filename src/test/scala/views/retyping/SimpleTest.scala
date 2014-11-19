package views.retyping

import common.CommonTest
import java.io.File
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import views.retyping.api._

/**
 * @note these tests are basically a copy of unicode test and do not make any inherent sense; testing was done based on
 * the compiler
 * @author Timm Felden
 */
@RunWith(classOf[JUnitRunner])
class SimpleTest extends CommonTest {

  test("create some instances") {
    val path = tmpFile("views.writetest")

    val σ = SkillFile.open(path)
    σ.A(null)
    σ.close

    assert(sha256(path) === sha256(new File("src/test/resources/unicode-reference.sf").toPath))
  }

  test("check unicode example") {
    val path = new File("src/test/resources/unicode-reference.sf").toPath()

    val σ = SkillFile.open(path)
  }
}
