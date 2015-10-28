package subtypes

import common.CommonTest
import subtypes.api.SkillFile
import java.io.File
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import de.ust.skill.common.scala.api.Append
import de.ust.skill.common.scala.api.Create
import de.ust.skill.common.scala.api.Read
import de.ust.skill.common.scala.api.ReadOnly

/**
 * @author Timm Felden
 * @note this test is no longer internal, as subtypes is compiled in a mode that reveals the SKilL IDs in a read mode
 */
@RunWith(classOf[JUnitRunner])
class InternalTest extends CommonTest {
  @inline def read(s : String) = SkillFile.open("src/test/resources/"+s)

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
    for (a ← state.A.all)
      assert(a.a === a)
    for (b ← state.B.all)
      assert(b.b === b)
    for (c ← state.C.all)
      assert(c.c === c)
    for (d ← state.D.all)
      assert(d.d === d)
  }

  test("subtypes create") {
    val path = tmpFile("lbpsi.create")

    val sf = SkillFile.open(path, Create, Append)

    val blocks = Seq("aabbbc", "bbdd", "acd")
    for (b ← blocks) {
      for (c ← b) {
        c match {
          case 'a' ⇒
            val i = sf.A.make(null);
            i.a = i;
          case 'b' ⇒
            val i = sf.B.make(null, null);
            i.a = i;
            i.b = i;
          case 'c' ⇒
            val i = sf.C.make(null, null);
            i.a = i;
            i.c = i;
          case 'd' ⇒
            val i = sf.D.make(null, null, null);
            i.a = i;
            i.b = i;
            i.d = i;
        }
      }
      sf.flush;
    }

    assert("aabbbcbbddadc" === SkillFile.open(sf.path, Read, ReadOnly).A.map(_.getTypeName).mkString)
  }

  test("subtypes write") {
    val path = tmpFile("lbpsi.write")

    val state = read("localBasePoolOffset.sf")

    // check self references
    for ((instance, index) ← state.A.all.zipWithIndex) {
      assert(instance.a.getSkillID === index + 1L, "index missmatch")
      assert(instance.a === instance, "self reference corrupted")
    }

    state.changePath(path)
    state.flush

    // check self references again (write might not have restored them)
    for ((instance, index) ← state.A.all.zipWithIndex) {
      assert(instance.a.getSkillID === index + 1L, "index missmatch after write")
      assert(instance.a === instance, "self reference corrupted after write")
    }

    val state2 = SkillFile.open(path, Read, ReadOnly)

    // check type of deserialized instances
    assert(state.A.allInTypeOrder.map(_.getClass.getSimpleName).sameElements(state2.A.allInTypeOrder.map(_.getClass.getSimpleName)))
  }
}