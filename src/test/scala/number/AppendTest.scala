package number

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common.CommonTest
import java.io.File
import number.api._
import de.ust.skill.common.scala.api.Write
import de.ust.skill.common.scala.api.Read
import de.ust.skill.common.scala.api.Append
import de.ust.skill.common.scala.api.Create
import de.ust.skill.common.scala.api.ReadOnly

@RunWith(classOf[JUnitRunner])
class AppendTest extends CommonTest {

  test("write, append, check") {
    val path = tmpFile("number.write.append.check")

    locally {
      val σ = SkillFile.open(path, Create, Write)
      σ.Number.make(1)
      σ.Number.make(2)
      σ.Number.make(3)
      σ.close
    }

    locally {
      val σ = SkillFile.open(path, Read, Append)
      σ.Number.make(1)
      σ.Number.make(2)
      σ.Number.make(3)
      σ.close
    }

    assert("123123" === SkillFile.open(path, Read, ReadOnly).Number.all.map(_.number).mkString(""))
    assert(sha256(path) == sha256("number.writeAppendCheckTest.sf"), "hash did not match")
  }
}