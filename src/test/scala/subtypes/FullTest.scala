package subtypes

import common.CommonTest
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import ogss.common.scala.api.Write
import ogss.common.scala.api.Read
import ogss.common.scala.api.ReadOnly
import ogss.common.scala.api.Create
import ogss.common.scala.api.FieldAccess

@RunWith(classOf[JUnitRunner])
class FullTest extends CommonTest {
  @inline def read(s : String) = OGFile.open("src/test/resources/" + s)

  /**
   * not fully implemented
   */
  test("delete and write") {
    val sf = read("localBasePoolOffset.sf")
    for (d ← sf.D)
      sf.delete(d)

    val path = tmpFile("delete")
    sf.changePath(path)
    sf.close

    assert(0 === OGFile.open(path, Read, ReadOnly).D.size, "there should be no D, because we deleted them all!")
  }

  test("delete and write -- hard") {
    val sf = read("localBasePoolOffset.sf")

    // remember size
    val sizes = sf.allTypes.map(p ⇒ p.name -> p.size).toMap

    for (x ← sf.B if !x.isInstanceOf[D])
      sf.delete(x)

    for (x ← sf.C)
      sf.delete(x)

    val path = tmpFile("delete")
    sf.changePath(path)
    sf.close

    val σ = OGFile.open(path, Read, ReadOnly)
    assert(sizes("a") - sizes("c") - (sizes("b") - sizes("d")) === σ.A.size)
    assert(sizes("d") === σ.B.size)
    assert(0 === σ.C.size)
    assert(sizes("d") === σ.D.size)
  }

  test("delete -- marked") {
    val σ = read("localBasePoolOffset.sf")
    for (d ← σ.D)
      σ.delete(d)

    assert(σ.D.forall(_.isDeleted), "some D is not marked for deletion?!")
  }

  test("delete -- write twice") {
    val σ = read("localBasePoolOffset.sf")
    σ.changePath(tmpFile("writeTwice"))
    for (d ← σ.D)
      σ.delete(d)

    σ.flush
    σ.flush

    assert(σ.D.forall(_.isDeleted), "some D is not marked for deletion?!")
  }

  test("delete -- write compress twice") {
    val σ = read("localBasePoolOffset.sf")
    σ.changePath(tmpFile("writeTwice"))
    for (d ← σ.D)
      σ.delete(d)

    σ.flush
    σ.flush

    assert(σ.D.forall(_.isDeleted), "some D is not marked for deletion?!")
  }

  test("delete -- write compress") {
    val σ = read("localBasePoolOffset.sf")
    σ.changePath(tmpFile("writeTwice"))
    for (d ← σ.D)
      σ.delete(d)

    σ.flush
    σ.flush

    assert(σ.D.forall(_.isDeleted), "some D is not marked for deletion?!")
  }

  test("reflective count ages") {
    val sf = OGFile.open("age16.sf")
    for (p ← sf.allTypes; if p.name == "Age") {
      val f = p.allFields.find(_.name == "Age").get.asInstanceOf[FieldAccess[Long]];

      assert(53725 === p.count(f.get(_) == 0))
    }
  }

  test("change tolerant append") {
    val target = tmpFile("ctAppend")
    // create a partial file
    locally {
      val sf = unknown.OGFile.open(target, Create, Write)
      sf.C.build.a(sf.A.make).make
      sf.close
    }
    // append the new type system
    locally {
      OGFile.open(target, Read, Write).close
    }
    // append a new instance
    locally {
      val sf = OGFile.open(target, Read, Write)
      sf.D.build.a(sf.C.head).make
      sf.close
    }
    // read the whole thing again and check content
    locally {
      val sf = OGFile.open(target, Read, ReadOnly)
      assert(sf.A.head.a === null)
      assert(sf.C.head.a === sf.A.head)
      assert(sf.C.head.c === null)
      assert(sf.D.head.a === sf.C.head)
      assert(sf.D.head.d === null)
    }
  }
}