package number

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common.CommonTest
import java.io.File
import number.api._

@RunWith(classOf[JUnitRunner])
class AppendTest extends CommonTest {

  test("write, append, check") {
    val path = tmpFile("number.write.append.check")

    locally {
      val σ = SkillFile.open(path, Create, Write)
      σ.Number(1)
      σ.Number(2)
      σ.Number(3)
      σ.close
    }

    locally {
      val σ = SkillFile.open(path, Read, Append)
      σ.Number(1)
      σ.Number(2)
      σ.Number(3)
      σ.close
    }

    assert("123123" === SkillFile.open(path).Number.all.map(_.number).mkString(""))
    assert(sha256(path) == sha256("number.writeAppendCheckTest.sf"), "hash did not match")
  }
}