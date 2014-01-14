package toolchains.node.viewer.internal

import scala.collection.Seq
import org.scalatest.Engine
import org.scalatest.events.Formatter
import scala.collection.immutable.Set
import scala.reflect.Manifest
import scala.runtime.BoxedUnit
import org.junit.runner.RunWith
import common.CommonTest
import scala.sys.Prop.Creator
import org.scalatest.junit.JUnitRunner

/**
 * This test looks at the internal representation of the viewer's state
 * @author Timm Felden
 */
@RunWith(classOf[JUnitRunner])
class InternalCoreTest extends CommonTest {
  import toolchains.node.tool1.api.{ SkillState ⇒ Creator }
  import toolchains.node.tool2.api.{ SkillState ⇒ ColorTool }
  import toolchains.node.tool3.api.{ SkillState ⇒ DescriptionTool }

  test("internal create,write,append,view") {
    val path = tmpFile("nodeExample.internal")

    locally {
      val σ = Creator.create
      σ.Node(23)
      σ.Node(42)
      σ.write(path)
    }

    locally {
      import toolchains.node.tool2.Node

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

    locally {
      import toolchains.node.tool3.Node

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

    locally {
      val σ = SerializableState.read(path)
      σ.dumpDebugInfo
    }
  }
}