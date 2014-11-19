package toolchains.node

import java.nio.file.Path
import scala.reflect.Manifest
import org.junit.runner.RunWith
import org.scalatest.Engine
import org.scalatest.events.Formatter
import org.scalatest.junit.JUnitRunner
import common.CommonTest
import java.nio.file.Files
import org.scalatest.FunSuite
import java.io.File
import java.security.MessageDigest

/**
 * The desired test makes use of core functionality, but it is not a unit test, but checks several features at once.
 *
 * @author Timm Felden
 */
@RunWith(classOf[JUnitRunner])
class CoreTest extends FunSuite {
  import tool1.api.{ SkillFile ⇒ Creator }
  import tool2.api.{ SkillFile ⇒ ColorTool }
  import tool3.api.{ SkillFile ⇒ DescriptionTool }
  import viewer.api.{ SkillFile ⇒ Viewer }

  @inline final def tmpFile(s : String) = {
    val r = File.createTempFile(s, ".sf")
    //    r.deleteOnExit
    r.toPath
  }

  final def sha256(name : String) : String = sha256(new File("src/test/resources/"+name).toPath)
  @inline final def sha256(path : Path) : String = {
    val bytes = Files.readAllBytes(path)
    MessageDigest.getInstance("SHA-256").digest(bytes).map("%02X".format(_)).mkString
  }

  test("fix this file!")(fail())

  //
  //  def invokeCreator(path : Path) {
  //    val σ = Creator.create
  //    σ.Node(23)
  //    σ.Node(42)
  //    σ.write(path)
  //  }
  //
  //  def invokeColorTool(path : Path) {
  //    import tool2.Node
  //
  //    val σ = ColorTool.read(path)
  //    σ.Node.all.foreach {
  //      case n @ Node(_, 23) ⇒ n.color = "red"
  //      case n @ Node(_, 42) ⇒ n.color = "black"
  //      case n               ⇒ n.color = "grey"
  //    }
  //    σ.append
  //  }
  //
  //  def invokeDescriptionTool(path : Path) {
  //    import tool3.Node
  //
  //    val σ = DescriptionTool.read(path)
  //    σ.Node.all.foreach {
  //      case n @ Node(_, 23) ⇒ n.description = "Some odd number."
  //      case n @ Node(_, 42) ⇒ n.description = "The answer."
  //      case n               ⇒ n.description = "Boring!"
  //    }
  //    σ.append
  //  }
  //
  //  test("create and write nodes with tool 1")(invokeCreator(tmpFile("nodeExample.create")))
  //
  //  test("colorize nodes") {
  //    val path = tmpFile("toolchain.core.colorize.nodes")
  //    import tool2.Node
  //
  //    invokeCreator(path)
  //    invokeColorTool(path)
  //
  //    val σ = ColorTool.read(path)
  //    assert(σ.Node.all.forall {
  //      case n @ Node("red", 23)   ⇒ true
  //      case n @ Node("black", 42) ⇒ true
  //      case n                     ⇒ false
  //    })
  //  }
  //
  //  test("create and write nodes with tool 1, append colors and descriptions -- with manual string updates") {
  //    val path = tmpFile("nodeExample.with.strings")
  //
  //    invokeCreator(path)
  //    invokeColorTool(path)
  //    invokeDescriptionTool(path)
  //
  //    locally {
  //      val σ = Viewer.read(path)
  //      assert(σ.Node.all.size === 2)
  //    }
  //  }
  //
  //  // TODO this test will have different semantics in TR14 (⇒partial fields)
  //  ignore("two toolchain cycles -- append") {
  //    val path = tmpFile("toolchain.two.cycles.append")
  //
  //    invokeCreator(path)
  //    invokeColorTool(path)
  //    invokeDescriptionTool(path)
  //
  //    locally {
  //      val σ = Creator.read(path)
  //      σ.Node(23)
  //      σ.Node(42)
  //      // not allowed: some fields are missing
  //      val e = intercept[AssertionError] {
  //        σ.append(path)
  //      }
  //      assert(e.getMessage() === "assertion failed: adding instances with an unknown field is currently not supported")
  //    }
  //  }
  //
  //  // TODO this test is broken indeed:)
  //  test("write to different files") {
  //    val path1 = tmpFile("commutativity.path1.")
  //    val path2 = tmpFile("commutativity.path2.")
  //
  //    val σ = Creator.create
  //    σ.Node(23)
  //    σ.Node(42)
  //    σ.write(path1)
  //    σ.write(path2)
  //
  //    assert(Viewer.read(path1).Node.size === 2, "first lacks instances")
  //    assert(Viewer.read(path2).Node.size === 2, "second lacks instances")
  //    assert(sha256(path1) == sha256(path2), "files should be equal")
  //  }
  //
  //  // TODO this test should start working again, after the test above has been fixed
  //  test("toolchain commutativity -- append") {
  //    val path1 = tmpFile("commutativity.path1.")
  //    val path2 = tmpFile("commutativity.path2.")
  //
  //    locally {
  //      val σ = Creator.create
  //      σ.Node(23)
  //      σ.Node(42)
  //      σ.write(path1)
  //      σ.write(path2)
  //    }
  //
  //    // path1
  //    invokeColorTool(path1)
  //    invokeDescriptionTool(path1)
  //
  //    // path2
  //    invokeDescriptionTool(path2)
  //    invokeColorTool(path2)
  //
  //    // check: files are unequal, contents is equal
  //    assert(sha256(path1) != sha256(path2), "files should not be equal, as appends happened in a different order")
  //    locally {
  //      val σ1 = Viewer.read(path1)
  //      val σ2 = Viewer.read(path2)
  //      assert(σ1.Node.size === σ2.Node.size, s"a path lost nodes: ${σ1.Node.size} != ${σ2.Node.size}")
  //      assert(σ1.Node.size === 2, "no node was lost")
  //      assert(σ1.Node.all.map(_.ID).sameElements(σ2.Node.all.map(_.ID)), "same ID")
  //      assert(σ1.Node.all.map(_.color).sameElements(σ2.Node.all.map(_.color)), "same colors")
  //      assert(σ1.Node.all.map(_.description).sameElements(σ2.Node.all.map(_.description)), "same description")
  //    }
  //  }
  //
  //  test("two toolchain cycles -- write") {
  //    val path = tmpFile("nodeExample.write.projection")
  //
  //    invokeCreator(path)
  //    invokeColorTool(path)
  //    invokeDescriptionTool(path)
  //
  //    locally {
  //      val σ = Creator.read(path)
  //      σ.Node(-1)
  //      σ.Node(2)
  //      σ.write(path)
  //    }
  //
  //    // the last write projected colors and descriptions away
  //    locally {
  //      val σ = Viewer.read(path)
  //      assert(σ.Node.size === 4)
  //      assert(σ.Node.all.forall(_.color == null))
  //      assert(σ.Node.all.forall(_.description == null))
  //    }
  //  }
  //
  //  test("append field to an empty pool -- toolchain two cycles, drop first create") {
  //    val path = tmpFile("nodeExample.no.create.write")
  //
  //    // no create here
  //    invokeColorTool(path)
  //    invokeDescriptionTool(path)
  //
  //    locally {
  //      // this might cause a problem
  //      val σ = Creator.read(path)
  //      σ.Node(-1)
  //      σ.Node(2)
  //      σ.write(path)
  //    }
  //
  //    // the last write projected colors and descriptions away
  //    locally {
  //      val σ = Viewer.read(path)
  //      assert(σ.Node.size === 2)
  //      assert(σ.Node.all.forall(_.color == null))
  //      assert(σ.Node.all.forall(_.description == null))
  //    }
  //  }
  //
  //  //@note: the behavior of this test changed, as empty pools have to be written
  //  test("append field to an empty pool -- no instances, all append") {
  //    val path = tmpFile("nodeExample.no.create.append")
  //
  //    // no instances -- no harm
  //    locally {
  //      // this might cause a problem
  //      val σ = Creator.read(path)
  //      σ.write(path)
  //    }
  //    invokeColorTool(path)
  //    invokeDescriptionTool(path)
  //
  //    locally {
  //      // this might cause a problem
  //      val σ = Creator.read(path)
  //      σ.Node(-1)
  //      σ.Node(2)
  //      σ.append(path)
  //    }
  //
  //    // the last write projected colors and descriptions away
  //    locally {
  //      val σ = Viewer.read(path)
  //      assert(σ.Node.size === 2)
  //    }
  //  }
  //
  //  /**
  //   * ensure that strings, which are not used, are not added
  //   */
  //  test("create and write nodes with tool 1, append colors and descriptions") {
  //    val path = tmpFile("nodeExample.create.write.append")
  //
  //    invokeCreator(path)
  //
  //    locally {
  //      import tool2.Node
  //
  //      val σ = ColorTool.read(path)
  //      assert(σ.Node.size === 2)
  //      σ.Node.all.foreach {
  //        case n @ Node(_, 23) ⇒ n.color = "red"
  //        case n @ Node(_, 42) ⇒ n.color = "black"
  //        case n               ⇒ n.color = "grey"
  //      }
  //      σ.append
  //    }
  //
  //    locally {
  //      import tool3.Node
  //
  //      val σ = DescriptionTool.read(path)
  //      assert(σ.Node.size === 2)
  //      σ.Node.all.foreach {
  //        case n @ Node(_, 23) ⇒ n.description = "Some odd number."
  //        case n @ Node(_, 42) ⇒ n.description = "The answer."
  //        case n               ⇒ n.description = "Boring!"
  //      }
  //      σ.append
  //    }
  //
  //    locally {
  //      val σ = Viewer.read(path)
  //      assert(σ.Node.size === 2)
  //      for (s ← σ.String.all) s match {
  //        case "grey"    ⇒ fail(s"$s should not be in here")
  //        case "Boring!" ⇒ fail(s"$s should not be in here")
  //        case _         ⇒ //fine
  //      }
  //    }
  //  }
}