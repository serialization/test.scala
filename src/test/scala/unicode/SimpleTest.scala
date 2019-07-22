package unicode

import java.io.File

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import common.CommonTest
import ogss.common.scala.api.Create
import ogss.common.scala.api.Read
import ogss.common.scala.api.ReadOnly
import ogss.common.scala.api.Write

@RunWith(classOf[JUnitRunner])
class SimpleTest extends CommonTest {

  test("create unicode example") {
    val path = tmpFile("unicode.writetest")

    val σ = OGFile.open(path, Create, Write)
    σ.Unicode.build.one("1").two("ö").three("☢").make
    σ.close

    assert(sha256(path) === sha256(new File("src/test/resources/unicode-reference.sf").toPath))
  }

  test("check unicode example") {
    val path = new File("src/test/resources/unicode-reference.sf").toPath()

    val σ = OGFile.open(path, Read, ReadOnly)
    val u = σ.Unicode.head
    assert(σ.Unicode.size === 1)
    assert(u.one.length === 1, u.one)
    assert(u.one.getBytes.length === 1, u.one)
    assert(u.two.length === 1, u.two)
    assert(u.two.getBytes.length === 2, u.two)
    assert(u.three.length === 1, u.three)
    assert(u.three.getBytes.length === 3, u.three)
  }
}
