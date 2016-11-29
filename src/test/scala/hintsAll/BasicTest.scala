package hintsAll

import scala.collection.mutable.HashSet

import common.CommonTest
import de.ust.skill.common.scala.api.Create
import de.ust.skill.common.scala.api.Write
import hintsAll.api.SkillFile

/**
 * Tests interface API.
 */
class BasicTest extends CommonTest {
  test("create a node with distributed fields and access a field") {
    val path = tmpFile("hints");
    val sf = SkillFile.open(path, Create, Write)

    val n = sf.User.make(30, "ich", "ich nicht")
    println(n.age)

    sf.close
  }

}
