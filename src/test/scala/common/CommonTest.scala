package common

import java.io.File

import scala.language.implicitConversions

import org.scalatest.junit.AssertionsForJUnit

class CommonTest extends AssertionsForJUnit {
  protected implicit def nameToPath(s: String) = new File("src/test/resources/"+s).toPath

}