package subtypes

import common.CommonTest
import java.io.File
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import ogss.common.scala.api.Write
import ogss.common.scala.api.Read
import ogss.common.scala.api.ReadOnly
import ogss.common.scala.api.Create

/**
 * @author Timm Felden
 * @note this test is no longer internal, as subtypes is compiled in a mode that reveals the SKilL IDs in a read mode
 */
@RunWith(classOf[JUnitRunner])
class InternalTest extends CommonTest {
  @inline def read(s : String) = OGFile.open("src/test/resources/" + s)

  test("subtypes read foreign") {
    val state = read("annotationTest.sf")
    assert(null != state)
  }

  test("simple read §6.3.2") {
    assert(null != read("localBasePoolOffset.sf"))
  }

  test("subtypes read; see §6.3.2") {
    val state = read("localBasePoolOffset.sf")
    val types = "aabbbcbbddadc"

    // check types
    val actualTypes = state.A.map(_.getClass.getSimpleName.toLowerCase).mkString("");
    assert(actualTypes === types)

    // check fields (all fields are self-references)
    for (a ← state.A)
      assert(a.a === a)
    for (b ← state.B)
      assert(b.b === b)
    for (c ← state.C)
      assert(c.c === c)
    for (d ← state.D)
      assert(d.d === d)
  }

  test("subtypes create") {
    val path = tmpFile("lbpsi.create")

    val sf = OGFile.open(path, Create, Write)

    val blocks = Seq("aabbbc", "bbdd", "acd")
    for (b ← blocks) {
      for (c ← b) {
        c match {
          case 'a' ⇒
            val i = sf.A.make
            i.a = i;
          case 'b' ⇒
            val i = sf.B.make
            i.a = i;
            i.b = i;
          case 'c' ⇒
            val i = sf.C.make
            i.a = i;
            i.c = i;
          case 'd' ⇒
            val i = sf.D.make
            i.a = i;
            i.b = i;
            i.d = i;
        }
      }
      sf.flush
    }

    assert("aabbbcbbddadc" === OGFile.open(sf.currentPath, Read, ReadOnly).A.map(sf.pool(_).name.toLowerCase).mkString)
  }

  test("subtypes write") {
    val path = tmpFile("lbpsi.write")

    val state = read("localBasePoolOffset.sf")

    // check self references
    for ((instance, index) ← state.A.zipWithIndex) {
      assert(instance.a.ID === index + 1, "index missmatch")
      assert(instance.a === instance, "self reference corrupted")
    }

    state.changePath(path)
    state.flush

    // check self references again (write might not have restored them)
    for ((instance, index) ← state.A.zipWithIndex) {
      assert(instance.a.ID === index + 1, "index missmatch after write")
      assert(instance.a === instance, "self reference corrupted after write")
    }

    val state2 = OGFile.open(path, Read, ReadOnly)

    // check type of deserialized instances
    assert(state.A.inTypeOrder.map(_.getClass.getSimpleName).sameElements(state2.A.inTypeOrder.map(_.getClass.getSimpleName)))
  }
}