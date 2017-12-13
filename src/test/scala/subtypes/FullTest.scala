package subtypes

import common.CommonTest
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import subtypes.api.SkillFile
import de.ust.skill.common.scala.api.FieldDeclaration
import de.ust.skill.common.scala.api.Read
import de.ust.skill.common.scala.api.ReadOnly
import de.ust.skill.common.scala.api.Write
import de.ust.skill.common.scala.api.Create
import de.ust.skill.common.scala.api.Append

@RunWith(classOf[JUnitRunner])
class FullTest extends CommonTest {
  @inline def read(s : String) = SkillFile.open("src/test/resources/" + s)

  /**
   * not fully implemented
   */
  test("delete and write") {
    val sf = read("localBasePoolOffset.sf")
    for (d ← sf.D.all)
      sf.delete(d)

    val path = tmpFile("delete")
    sf.changePath(path)
    sf.close

    assert(0 === SkillFile.open(path, Read, ReadOnly).D.size, "there should be no D, because we deleted them all!")
  }

  test("delete and write -- hard") {
    val sf = read("localBasePoolOffset.sf")

    // remember size
    val sizes = sf.map(p ⇒ p.name -> p.size).toMap

    for (x ← sf.B if !x.isInstanceOf[D])
      sf.delete(x)

    for (x ← sf.C)
      sf.delete(x)

    val path = tmpFile("delete")
    sf.changePath(path)
    sf.close

    val σ = SkillFile.open(path, Read, ReadOnly)
    assert(sizes("a") - sizes("c") - (sizes("b") - sizes("d")) === σ.A.size)
    assert(sizes("d") === σ.B.size)
    assert(0 === σ.C.size)
    assert(sizes("d") === σ.D.size)
  }

  test("delete -- marked") {
    val σ = read("localBasePoolOffset.sf")
    for (d ← σ.D.all)
      σ.delete(d)

    assert(σ.D.all.forall(_.markedForDeletion), "some D is not marked for deletion?!")
  }

  test("reflective count ages") {
    val sf = SkillFile.open("age16.sf")
    for (p ← sf; if p.name == "age") {
      val f = p.allFields.find(_.name == "age").get.asInstanceOf[FieldDeclaration[Long]];

      assert(53725 === p.all.count(_.get(f) == 0))
    }
  }

  test("change tolerant append") {
    val target = tmpFile("ctAppend")
    // create a partial file
    locally {
      val sf = unknown.api.SkillFile.open(target, Create, Write)
      sf.C.make(a = sf.A.make(null))
      sf.close
    }
    // append the new type system
    locally {
      SkillFile.open(target, Read, Append).close
    }
    // append a new instance
    locally {
      val sf = SkillFile.open(target, Read, Append)
      sf.D.make(a = sf.C.head)
      sf.close
    }
    // read the whole thing again and check content
    locally {
      val sf = SkillFile.open(target, Read, ReadOnly)
      assert(sf.A.head.a === null)
      assert(sf.C.head.a === sf.A.head)
      assert(sf.C.head.c === null)
      assert(sf.D.head.a === sf.C.head)
      assert(sf.D.head.d === null)
    }
  }
}