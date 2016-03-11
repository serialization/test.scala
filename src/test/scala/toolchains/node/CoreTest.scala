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
import de.ust.skill.common.scala.api.Append

/**
 * The desired test makes use of core functionality, but it is not a unit test, but checks several features at once.
 *
 * @author Timm Felden
 */
@RunWith(classOf[JUnitRunner])
class CoreTest extends CommonTest {
  import tool1.api.{ SkillFile ⇒ Creator }
  import tool2.api.{ SkillFile ⇒ ColorTool }
  import tool3.api.{ SkillFile ⇒ DescriptionTool }
  import viewer.api.{ SkillFile ⇒ Viewer }

  def invokeCreator(path : Path) {
    val σ = Creator.create(path)
    σ.Node.make(23)
    σ.Node.make(42)
    σ.close
  }

  def invokeColorTool(path : Path) {
    import tool2.Node

    val σ = ColorTool.read(path, Append)
    σ.Node.all.foreach {
      case n @ Node(_, 23) ⇒ n.color = "red"
      case n @ Node(_, 42) ⇒ n.color = "black"
      case n               ⇒ n.color = "grey"
    }
    σ.close
  }

  def invokeDescriptionTool(path : Path) {
    import tool3.Node

    val σ = DescriptionTool.read(path, Append)
    σ.Node.all.foreach {
      case n @ Node(_, 23) ⇒ n.description = "Some odd number."
      case n @ Node(_, 42) ⇒ n.description = "The answer."
      case n               ⇒ n.description = "Boring!"
    }
    σ.close
  }

  test("create and write nodes with tool 1")(invokeCreator(tmpFile("nodeExample.create")))

  test("colorize nodes") {
    val path = tmpFile("toolchain.core.colorize.nodes")
    import tool2.Node

    invokeCreator(path)
    invokeColorTool(path)

    val σ = ColorTool.read(path)
    assert(σ.Node.all.forall {
      case n @ Node("red", 23)   ⇒ true
      case n @ Node("black", 42) ⇒ true
      case n                     ⇒ false
    })
  }

  test("create and write nodes with tool 1, append colors and descriptions -- with manual string updates") {
    val path = tmpFile("nodeExample.with.strings")

    invokeCreator(path)
    invokeColorTool(path)
    invokeDescriptionTool(path)

    locally {
      val σ = Viewer.read(path)
      assert(σ.Node.all.size === 2)
    }
  }

  ignore("two toolchain cycles -- append") {

    val path = tmpFile("toolchain.two.cycles.append")

    invokeCreator(path)
    invokeColorTool(path)
    invokeDescriptionTool(path)

    locally {
      val σ = Creator.read(path, Append)
      σ.Node.make(23)
      σ.Node.make(42)

      fail("close will crash, because there is currently no notion of partial fields")
      σ.close
    }
  }

  test("write to different files") {
    val path1 = tmpFile("commutativity.path1.")
    val path2 = tmpFile("commutativity.path2.")

    val σ = Creator.create(path1)
    σ.Node.make(23)
    σ.Node.make(42)
    σ.flush()
    σ.changePath(path2)
    σ.close

    assert(Viewer.read(path1).Node.size === 2, "first lacks instances")
    assert(Viewer.read(path2).Node.size === 2, "second lacks instances")
    assert(sha256(path1) == sha256(path2), "files should be equal")
  }

  test("toolchain commutativity -- append") {
    val path1 = tmpFile("commutativity.path1.")
    val path2 = tmpFile("commutativity.path2.")

    locally {
      val σ = Creator.create(path1)
      σ.Node.make(23)
      σ.Node.make(42)
      σ.flush()
      σ.changePath(path2)
      σ.close
    }

    // path1
    invokeColorTool(path1)
    invokeDescriptionTool(path1)

    // path2
    invokeDescriptionTool(path2)
    invokeColorTool(path2)

    // check: files are unequal, contents is equal
    assert(sha256(path1) != sha256(path2), "files should not be equal, as appends happened in a different order")
    locally {
      val σ1 = Viewer.read(path1)
      val σ2 = Viewer.read(path2)
      assert(σ1.Node.size === σ2.Node.size, s"a path lost nodes: ${σ1.Node.size} != ${σ2.Node.size}")
      assert(σ1.Node.size === 2, "no node was lost")
      assert(σ1.Node.all.map(_.ID).sameElements(σ2.Node.all.map(_.ID)), "same ID")
      assert(σ1.Node.all.map(_.color).sameElements(σ2.Node.all.map(_.color)), "same colors")
      assert(σ1.Node.all.map(_.description).sameElements(σ2.Node.all.map(_.description)), "same description")
    }
  }

  ignore("two toolchain cycles -- write") {
    val path = tmpFile("nodeExample.write.projection")

    invokeCreator(path)
    invokeColorTool(path)
    invokeDescriptionTool(path)

    locally {
      val σ = Creator.read(path)
      σ.Node.make(-1)
      σ.Node.make(2)
      σ.close
    }

    // the last write projected colors and descriptions away
    fail("this test is no longer working, because write will not project; requires implementation of partial fields")
    locally {
      val σ = Viewer.read(path)
      assert(σ.Node.size === 4)
      assert(σ.Node.all.forall(_.color == null))
      assert(σ.Node.all.forall(_.description == null))
    }
  }

  test("append field to an empty pool -- toolchain two cycles, drop first create") {
    val path = tmpFile("nodeExample.no.create.write")

    // no create here
    invokeColorTool(path)
    invokeDescriptionTool(path)

    locally {
      // this might cause a problem
      val σ = Creator.read(path)
      σ.Node.make(-1)
      σ.Node.make(2)
      σ.close
    }

    // the last write projected colors and descriptions away
    locally {
      val σ = Viewer.read(path)
      assert(σ.Node.size === 2)
      assert(σ.Node.all.forall(_.color == null))
      assert(σ.Node.all.forall(_.description == null))
    }
  }

  //@note: the behavior of this test changed, as empty pools have to be written
  test("append field to an empty pool -- no instances, all append") {
    val path = tmpFile("nodeExample.no.create.append")

    // no instances -- no harm
    locally {
      // this might cause a problem
      val σ = Creator.read(path)
      σ.close
    }
    invokeColorTool(path)
    invokeDescriptionTool(path)

    locally {
      // this might cause a problem
      val σ = Creator.read(path, Append)
      σ.Node.make(-1)
      σ.Node.make(2)
      σ.close
    }

    // the last write projected colors and descriptions away
    locally {
      val σ = Viewer.read(path)
      assert(σ.Node.size === 2)
    }
  }

  /**
   * ensure that strings, which are not used, are not added
   */
  test("create and write nodes with tool 1, append colors and descriptions") {
    val path = tmpFile("nodeExample.create.write.append")

    invokeCreator(path)

    locally {
      import tool2.Node

      val σ = ColorTool.read(path, Append)
      assert(σ.Node.size === 2)
      σ.Node.all.foreach {
        case n @ Node(_, 23) ⇒ n.color = "red"
        case n @ Node(_, 42) ⇒ n.color = "black"
        case n               ⇒ n.color = "grey"
      }
      σ.close
    }

    locally {
      import tool3.Node

      val σ = DescriptionTool.read(path, Append)
      assert(σ.Node.size === 2)
      σ.Node.foreach {
        case n @ Node(_, 23) ⇒ n.description = "Some odd number."
        case n @ Node(_, 42) ⇒ n.description = "The answer."
        case n               ⇒ n.description = "Boring!"
      }
      σ.close
    }

    locally {
      val σ = Viewer.read(path)
      assert(σ.Node.size === 2)
      for (s ← σ.String) s match {
        case "grey"    ⇒ fail(s"$s should not be in here")
        case "Boring!" ⇒ fail(s"$s should not be in here")
        case _         ⇒ //fine
      }
    }
  }
}
