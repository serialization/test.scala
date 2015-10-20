package container

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import org.scalatest.junit.JUnitRunner
import common.CommonTest
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import java.io.File
import container.api.SkillFile
import de.ust.skill.common.scala.internal.SkillState
import de.ust.skill.common.scala.api.Write
import de.ust.skill.common.scala.api.Create
import de.ust.skill.common.scala.api.Read
import de.ust.skill.common.scala.api.ReadOnly

@RunWith(classOf[JUnitRunner])
class FullTest extends CommonTest {

  @inline final def read(s : String) = {
    println(s)
    SkillFile.open("src/test/resources/"+s)
  }
  @inline final def dump(state : SkillFile) {
    for (t ← state) {
      println(s"Pool[${t.name}${
        if (t.superName.isDefined)
          " <: "+t.superName.get
        else
          ""
      }]")
      for (i ← t.all) {
        println(s"  $i = ${
          t.allFields.map {
            f ⇒ s"${f.name}: ${i.get(f)}"
          }.mkString("[", ", ", "]")
        }")
      }
      println()
    }
  }

  // reflective read
  test("read reflective: nodes") { dump(read("node.sf")) }
  test("read reflective: two node blocks") { dump(read("twoNodeBlocks.sf")) }
  test("read reflective: colored nodes") { dump(read("coloredNodes.sf")) }
  test("read reflective: four colored nodes") { dump(read("fourColoredNodes.sf")) }
  test("read reflective: empty blocks") { dump(read("emptyBlocks.sf")) }
  test("read reflective: two types") { dump(read("twoTypes.sf")) }
  test("read reflective: trivial type definition") { dump(read("trivialType.sf")) }
  test("read reflective: subtypes") { dump(read("localBasePoolOffset.sf")) }
  test("read reflective: container") { dump(read("container.sf")) }
  test("read reflective: commutativity path 1") { dump(read("commutativityPath1.sf")) }
  test("read reflective: commutativity path 2") { dump(read("commutativityPath2.sf")) }

  // compound types
  test("create container instances") {
    val p = tmpFile("container.create")

    locally {
      val state = SkillFile.open(p, Create, Write)
      state.Container.make(
        arr = ArrayBuffer(0, 0, 0),
        varr = ArrayBuffer(1, 2, 3),
        l = ListBuffer(),
        s = Set().to,
        f = HashMap("f" -> HashMap(0L -> 0L)),
        someSet = Set().to
      )
      for (c ← state.Container.all)
        c.s = c.arr.toSet.to

      state.close
    }

    locally {
      val state = SkillFile.open(p, Read, ReadOnly)
      val c = state.Container.all.next
      assert(c.arr.size === 3)
      assert(c.varr.sameElements(1 to 3))
      assert(c.l.isEmpty)
      assert(c.s.sameElements(0 to 0))
      assert(c.f("f")(c.s.head) == 0)
    }
  }
}