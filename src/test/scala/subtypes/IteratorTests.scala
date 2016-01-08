package subtypes

import common.CommonTest
import subtypes.api._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import de.ust.skill.common.scala.api.Write
import de.ust.skill.common.scala.api.Create
import de.ust.skill.common.scala.api.ReadOnly
import de.ust.skill.common.scala.api.Read
import de.ust.skill.common.scala.internal.TypeHierarchyIterator
import de.ust.skill.common.scala.internal.StoragePool
import de.ust.skill.common.scala.api.SkillObject

@RunWith(classOf[JUnitRunner])
class IteratorTest extends CommonTest {
  @inline def read = SkillFile.open("src/test/resources/localBasePoolOffset.sf", Read, ReadOnly)

  test("type hierarchy iterator") {
    val σ = read
    for (t ← σ)
      println(new TypeHierarchyIterator(t.asInstanceOf[StoragePool[SkillObject, SkillObject]]).map(_.name).mkString)
  }

  test("dynamic data iterator") {
    val state = read
    val c = state.C.make(null, null)
    c.a = c
    c.c = c
    state.close
  }

  test("static data iterator") {
    val σ = read

    // A
    locally {
      val ts = "aaabbbbbdddcc"
      assert(ts === σ.A.allInTypeOrder.map(_.getTypeName.charAt(0)).mkString)
    }

    // B
    locally {
      val ts = "bbbbbddd"
      assert(ts === σ.B.allInTypeOrder.map(_.getTypeName.charAt(0)).mkString)
    }

    // C
    locally {
      val ts = "cc"
      assert(ts === σ.C.allInTypeOrder.map(_.getTypeName.charAt(0)).mkString)
    }

    // D
    locally {
      val ts = "ddd"
      assert(ts === σ.D.allInTypeOrder.map(_.getTypeName.charAt(0)).mkString)
    }
  }
}