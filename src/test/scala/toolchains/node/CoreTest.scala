package toolchains.node

import scala.reflect.Manifest
import org.junit.runner.RunWith
import org.scalatest.Engine
import org.scalatest.events.Formatter
import org.scalatest.junit.JUnitRunner
import common.CommonTest
import java.nio.file.Path

/**
 * The desired test makes use of core functionality, but it is not a unit test, but checks several features at once.
 *
 * @author Timm Felden
 */
@RunWith(classOf[JUnitRunner])
class CoreTest extends CommonTest {
  import tool1.api.{ SkillState ⇒ Creator }
  import tool2.api.{ SkillState ⇒ ColorTool }
  import tool3.api.{ SkillState ⇒ DescriptionTool }
  import viewer.api.{ SkillState ⇒ Viewer }

  def invokeCreator(path: Path) {
    val σ = Creator.create
    σ.Node(23)
    σ.Node(42)
    σ.write(path)
  }

  def invokeColorTool(path: Path) {
    import tool2.Node

    val σ = ColorTool.read(path)
    σ.String.add("red")
    σ.String.add("black")
    σ.String.add("grey")
    σ.Node.all.foreach {
      case n @ Node(23, _) ⇒ n.color = "red"
      case n @ Node(42, _) ⇒ n.color = "black"
      case n               ⇒ n.color = "grey"
    }
    σ.append
  }

  def invokeDescriptionTool(path: Path) {
    import tool3.Node

    val σ = DescriptionTool.read(path)
    σ.String.add("Some odd number.")
    σ.String.add("The answer.")
    σ.String.add("Boring!")
    σ.Node.all.foreach {
      case n @ Node(23, _) ⇒ n.description = "Some odd number."
      case n @ Node(42, _) ⇒ n.description = "The answer."
      case n               ⇒ n.description = "Boring!"
    }
    σ.append
  }

  test("create and write nodes with tool 1")(invokeCreator(tmpFile("nodeExample.create")))

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

  test("two toolchain cycles -- append") {
    val path = tmpFile("nodeExample.with.strings")

    invokeCreator(path)
    invokeColorTool(path)
    invokeDescriptionTool(path)

    locally {
      val σ = Creator.read(path)
      σ.Node(23)
      σ.Node(42)
      // not allowed: some fields are missing
      val e = intercept[AssertionError] {
        σ.append(path)
      }
      assert(e.getMessage() === "assertion failed: adding instances with an unknown field is currently not supported")
    }
  }

  test("toolchain commutativity -- append") {
    val path1 = tmpFile("commutativity.path1.")
    val path2 = tmpFile("commutativity.path2.")

    locally {
      val σ = Creator.create
      σ.Node(23)
      σ.Node(42)
      σ.write(path1)
      σ.write(path2)
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
      assert(σ1.Node.all.size === 2)
    }
  }

  test("two toolchain cycles -- write") {
    val path = tmpFile("nodeExample.with.strings")

    invokeCreator(path)
    invokeColorTool(path)
    invokeDescriptionTool(path)

    locally {
      val σ = Creator.read(path)
      σ.Node(-1)
      σ.Node(2)
      σ.write(path)
    }

    // the last write projected colors and descriptions away
    locally {
      val σ = Viewer.read(path)
      assert(σ.Node.all.forall(_.color == null))
      assert(σ.Node.all.forall(_.description == null))
    }
  }

  /**
   * ignoder, because it assumes that strings are collected automagically
   */
  ignore("create and write nodes with tool 1, append colors and descriptions") {
    val path = tmpFile("nodeExample.create.write.append")

    invokeCreator(path)

    locally {
      import tool2.Node

      val σ = ColorTool.read(path)
      σ.Node.all.foreach {
        case n @ Node(23, _) ⇒ n.color = "red"
        case n @ Node(42, _) ⇒ n.color = "black"
        case n               ⇒ n.color = "grey"
      }
      σ.append
    }

    locally {
      import tool3.Node

      val σ = DescriptionTool.read(path)
      σ.Node.all.foreach {
        case n @ Node(23, _) ⇒ n.description = "Some odd number."
        case n @ Node(42, _) ⇒ n.description = "The answer."
        case n               ⇒ n.description = "Boring!"
      }
      σ.append
    }

    locally {
      val σ = Viewer.read(path)
      assert(σ.Node.all.size === 2)
    }
  }
}