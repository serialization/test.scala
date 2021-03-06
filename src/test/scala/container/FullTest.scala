package container

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import org.scalatest.junit.JUnitRunner
import common.CommonTest
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import java.io.File
import ogss.common.scala.api.Write
import ogss.common.scala.api.Read
import ogss.common.scala.api.ReadOnly
import ogss.common.scala.api.Create
import ogss.common.scala.internal.Obj

@RunWith(classOf[JUnitRunner])
class FullTest extends CommonTest {

  @inline final def read(s : String) = {
    println(s)
    OGFile.open("src/test/resources/" + s)
  }
  @inline final def dump(state : OGFile) {
    for (t ← state.allTypes) {
      println(s"Pool[${t.name}${
        if (t.superType != null)
          " <: " + t.superType.name
        else
          ""
      }]")
      for ((i : Obj) ← t) {
        println(s"  $i = ${
          t.allFields.map {
            f ⇒ s"${f.name}: ${f.get(i)}"
          }.mkString("[", ", ", "]")
        }")
      }
      println()
    }
  }

  // compound types
  test("create container instances") {
    val p = tmpFile("container.create")

    locally {
      val state = OGFile.open(p, Create, Write)
      state.Container.build
        .arr(ArrayBuffer(0, 0, 0))
        .varr(ArrayBuffer(1, 2, 3))
        .l(ListBuffer())
        .s(Set().to)
        .f(HashMap("f" -> HashMap(0L -> 0L)))
        .someSet(Set().to)
        .make

      for (c ← state.Container)
        c.s = c.arr.toSet.to

      state.close
    }

    locally {
      val state = OGFile.open(p, Read, ReadOnly)
      val c = state.Container.iterator.next
      assert(c.arr.size === 3)
      assert(c.varr.sameElements(1 to 3))
      assert(c.l.isEmpty)
      assert(c.s.sameElements(0 to 0))
      assert(c.f("f")(c.s.head) == 0)
    }
  }
}