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
  @inline def read = OGFile.open("../../src/test/resources/binarygen/[[empty]]/accept/poly.sg", Read, ReadOnly)

  /**
   * not fully implemented
   */
  test("delete and write") {
    val sf = read
    for (d ← sf.D)
      sf.delete(d)

    val path = tmpFile("delete")
    sf.changePath(path)
    sf.close

    assert(0 === OGFile.open(path, Read, ReadOnly).D.size, "there should be no D, because we deleted them all!")
  }

  test("delete and write -- hard") {
    val sf = read

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
    assert(sizes("A") - sizes("C") - (sizes("B") - sizes("D")) === σ.A.size)
    assert(sizes("D") === σ.B.size)
    assert(0 === σ.C.size)
    assert(sizes("D") === σ.D.size)
  }

  test("delete -- marked") {
    val σ = read
    for (d ← σ.D)
      σ.delete(d)

    assert(σ.D.forall(_.isDeleted), "some D is not marked for deletion?!")
  }

  test("delete -- write twice") {
    val σ = read
    σ.changePath(tmpFile("writeTwice"))
    for (d ← σ.D)
      σ.delete(d)

    σ.flush
    σ.flush

    assert(σ.D.forall(_.isDeleted), "some D is not marked for deletion?!")
  }

  test("delete -- write compress twice") {
    val σ = read
    σ.changePath(tmpFile("writeTwice"))
    for (d ← σ.D)
      σ.delete(d)

    σ.flush
    σ.flush

    assert(σ.D.forall(_.isDeleted), "some D is not marked for deletion?!")
  }

  test("delete -- write compress") {
    val σ = read
    σ.changePath(tmpFile("writeTwice"))
    for (d ← σ.D)
      σ.delete(d)

    σ.flush
    σ.flush

    assert(σ.D.forall(_.isDeleted), "some D is not marked for deletion?!")
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