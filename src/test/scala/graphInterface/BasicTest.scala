
package graphInterface

import java.nio.file.Path

import org.junit.Assert

import common.CommonTest
import scala.collection.mutable.HashSet
import ogss.common.scala.api.Write
import ogss.common.scala.api.Create

/**
 * Tests interface API.
 */
class BasicTest extends CommonTest {
  test("create simple graph") {
    val path = tmpFile("graph");
    val sf = OGFile.open(path, Create, Write)

    val n = sf.Node.build.color("red").edges(new HashSet()).make

    val c = sf.ColorHolder.build.anAbstractNode(n).anAnnotation(n).make

    assert("red" === c.anAnnotation.color)

    assert(1 === sf.Marker.map(_.prettyString(sf)).count(_ ⇒ true))

    sf.close
  }

  test("access headless interface") {
    val path = tmpFile("graph");
    val sf = OGFile.open(path, Create, Write)

    val n = sf.Node.build.color("red").edges(new HashSet()).make

    val c = sf.ColorHolder.build.anAbstractNode(n).anAnnotation(n).make

    sf.Marker.map(_.prettyString(sf)).foreach(println)

    sf.close
  }

}
