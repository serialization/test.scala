
package graphInterface

import java.nio.file.Path

import org.junit.Assert

import de.ust.skill.common.scala.api.Access
import de.ust.skill.common.scala.api.Create
import de.ust.skill.common.scala.api.SkillException
import de.ust.skill.common.scala.api.Read
import de.ust.skill.common.scala.api.ReadOnly
import de.ust.skill.common.scala.api.Write

import graphInterface.api.SkillFile
import common.CommonTest
import scala.collection.mutable.HashSet

/**
 * Tests interface API.
 */
class BasicTest extends CommonTest {
  test("create simple graph") {
    val path = tmpFile("graph");
    val sf = SkillFile.open(path, Create, Write)

    val n = sf.Node.make(color = "red", edges = new HashSet())

    val c = sf.ColorHolder.make(n, n)

    assert("red" === c.anAnnotation.color)

    assert(1 === sf.Marker.all.map(_.prettyString).foldLeft(0) { case (i, v) ⇒ i + 1 })

    sf.close
  }

  test("access headless interface") {
    val path = tmpFile("graph");
    val sf = SkillFile.open(path, Create, Write)

    val n = sf.Node.make(color = "red", edges = new HashSet())

    val c = sf.ColorHolder.make(n, n)

    sf.Marker.map(_.prettyString).foreach(println)

    sf.close
  }

}
