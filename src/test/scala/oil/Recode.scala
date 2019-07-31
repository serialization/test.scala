
package oil

import common.CommonTest
import ogss.common.scala.api.Read
import ogss.common.scala.api.ReadOnly
import ogss.common.scala.api.Write

/**
 * Tests recoding of files without a tool specification.
 *
 * @author Timm Felden
 */
class RecodeTest extends CommonTest {
  @inline def read(s : String) = OGFile.open("../../" + s, Read, ReadOnly)

  test("read") {
    val sg = read("src/test/resources/binarygen/[[empty]]/accept/ogss.oil.sg")
    sg.check
    sg.close
  }

  test("recode") {
    val path = tmpFile("recode");
    locally {
      val sg = read("src/test/resources/binarygen/[[empty]]/accept/ogss.oil.sg")
      sg.check
      sg.changePath(path)
      sg.close
    }
    locally {
      val sg = OGFile.open(path, Read, ReadOnly)
      sg.check
      sg.close
    }
  }

  test("recode twice") {
    val path = tmpFile("recode");
    locally {
      val sg = read("src/test/resources/binarygen/[[empty]]/accept/ogss.oil.sg")
      sg.check
      sg.changePath(path)
      sg.close
    }
    locally {
      val sg = OGFile.open(path, Read, Write)
      sg.check
      sg.close
    }
    locally {
      val sg = OGFile.open(path, Read, ReadOnly)
      sg.check
      sg.close
    }
  }
}
