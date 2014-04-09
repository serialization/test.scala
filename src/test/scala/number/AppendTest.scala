package number

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common.CommonTest
import java.io.File
import number.api.SkillState

@RunWith(classOf[JUnitRunner])
class AppendTest extends CommonTest {

  test("write, append, check") {
    val path = tmpFile("number.write.append.check")

    locally {
      val σ = SkillState.create
      σ.Number(1)
      σ.Number(2)
      σ.Number(3)
      σ.write(path)
    }

    locally {
      val σ = SkillState.read(path)
      σ.Number(1)
      σ.Number(2)
      σ.Number(3)
      σ.append(path)
    }

    assert("123123" === SkillState.read(path).Number.all.map(_.number).mkString(""))
    assert(sha256(path) == sha256("number.writeAppendCheckTest.sf"), "hash did not match")
  }
}