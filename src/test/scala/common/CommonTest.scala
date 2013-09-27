package common

import java.io.File
import scala.language.implicitConversions
import org.scalatest.junit.AssertionsForJUnit
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CommonTest extends FunSuite {
  protected implicit def nameToPath(s: String) = new File("src/test/resources/"+s).toPath

}