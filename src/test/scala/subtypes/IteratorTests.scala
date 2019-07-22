package subtypes

import common.CommonTest
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import ogss.common.scala.api.Read
import ogss.common.scala.api.ReadOnly
import ogss.common.scala.internal.TypeHierarchyIterator
import ogss.common.scala.internal.Pool
import ogss.common.scala.internal.Obj

@RunWith(classOf[JUnitRunner])
class IteratorTest extends CommonTest {
  @inline def read = OGFile.open("../../src/test/resources/binarygen/[[empty]]/accept/poly.sg", Read, ReadOnly)

  test("type hierarchy iterator") {
    val σ = read
    for (t ← σ.allTypes)
      println(new TypeHierarchyIterator(t.asInstanceOf[Pool[_ <: Obj]]).map(_.name).mkString)
  }

  test("dynamic data iterator") {
    val σ = read

    // A
    locally {
      val ts = "abdc"
      assert(ts === σ.A.inTypeOrder.map(σ.pool(_).name.toLowerCase.charAt(0)).mkString)
    }

    // B
    locally {
      val ts = "bd"
      assert(ts === σ.B.inTypeOrder.map(σ.pool(_).name.toLowerCase.charAt(0)).mkString)
    }

    // C
    locally {
      val ts = "c"
      assert(ts === σ.C.inTypeOrder.map(σ.pool(_).name.toLowerCase.charAt(0)).mkString)
    }

    // D
    locally {
      val ts = "d"
      assert(ts === σ.D.inTypeOrder.map(σ.pool(_).name.toLowerCase.charAt(0)).mkString)
    }
  }

  test("static data iterator") {
    val σ = read

    // A
    locally {
      val ts = "a"
      assert(ts === σ.A.staticInstances.map(σ.pool(_).name.toLowerCase.charAt(0)).mkString)
    }

    // B
    locally {
      val ts = "b"
      assert(ts === σ.B.staticInstances.map(σ.pool(_).name.toLowerCase.charAt(0)).mkString)
    }

    // C
    locally {
      val ts = "c"
      assert(ts === σ.C.staticInstances.map(σ.pool(_).name.toLowerCase.charAt(0)).mkString)
    }

    // D
    locally {
      val ts = "d"
      assert(ts === σ.D.staticInstances.map(σ.pool(_).name.toLowerCase.charAt(0)).mkString)
    }
  }
}