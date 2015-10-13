package common

import java.io.File
import scala.language.implicitConversions
import org.scalatest.junit.AssertionsForJUnit
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import java.security.MessageDigest
import java.nio.file.Path
import java.nio.file.Files

@RunWith(classOf[JUnitRunner])
class CommonTest extends FunSuite {
  @inline final def tmpFile(s : String) = {
    val r = File.createTempFile(s, ".sf")
    r.deleteOnExit
    r.toPath
  }

  final def sha256(name : String) : String = sha256(new File("src/test/resources/"+name).toPath)
  @inline final def sha256(path : Path) : String = {
    val bytes = Files.readAllBytes(path)
    MessageDigest.getInstance("SHA-256").digest(bytes).map("%02X".format(_)).mkString
  }

  final def ∀[T](is : Iterable[T])(p : T ⇒ Boolean) = is.forall(p)
}