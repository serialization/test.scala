package enums

import common.CommonTest
import enums.api.SkillFile
import de.ust.skill.common.scala.api.Read
import de.ust.skill.common.scala.api.ReadOnly

/**
 * Tests the file reading capabilities.
 */
class BasicTest extends CommonTest {

  test("enums - create") {
    val sf = SkillFile.open(tmpFile("enum.create"), Read, ReadOnly)
    sf.foreach(println)
    sf.`Testenum:default`.get
    for (e ← sf.TestEnum.all) {
      val n = e.getClass.getName
      e.name = n.substring(n.lastIndexOf('$') + 1)
      println(e.prettyString)
    }
  }

  test("enums - create and close empty") {
    val sf = SkillFile.open(tmpFile("enum.create"), Read, ReadOnly)
    sf.close
  }

  test("enums - create & flush") {
    val sf = SkillFile.open(tmpFile("enum.create"), Read, ReadOnly)
    sf.foreach(println)
    sf.`Testenum:default`.get
    sf.flush
    for (e ← sf.TestEnum.all) {
      val n = e.getClass.getName
      e.name = n.substring(n.lastIndexOf('$'))
      println(e.prettyString)
    }
  }
}