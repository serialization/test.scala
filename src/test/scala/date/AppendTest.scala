package date
import org.junit.runner.RunWith
import common.CommonTest
import date.api._
import org.scalatest.junit.JUnitRunner
import java.io.File

@RunWith(classOf[JUnitRunner])
class AppendTest extends CommonTest {
  import SkillFile._

  test("Tr13 §6.2.3 based date example") {
    val path = tmpFile("writetest")

    val σ = open(path, Create, Write)
    σ.Date(1)
    σ.Date(-1)
    σ.close

    assert(sha256(path) === sha256(new File("src/test/resources/date-example.sf").toPath))
    assert(SkillFile.open(path).Date.all.map(_.date).toList.sameElements(List(1, -1)))
  }

  test("Tr13 §6.2.3 append example") {
    val path = tmpFile("append.test")

    val σ = open("src/test/resources/date-example.sf", Read, Append)
    σ.Date(2)
    σ.Date(3)
    σ.changePath(path)
    σ.close

    assert(sha256(path) === sha256(new File("src/test/resources/date-example-append.sf").toPath))
    assert(SkillFile.open(path).Date.all.map(_.date).toList.sameElements(List(1, -1, 2, 3)))
  }

  test("write 100k dates; append 9x100k; write 1m dates and check them all -- multiple states") {
    val limit = 1e5.toInt
    val path = tmpFile("append")

    // write
    locally {
      val σ = open(path, Create, Write)
      for (i ← 0 until limit)
        σ.Date(i)

      σ.close
    }

    // append
    for (i ← 1 until 10) {
      val σ = SkillFile.open(path, Read, Append)
      for (v ← i * limit until limit + i * limit)
        σ.Date(v)

      σ.close
    }

    // read & check & write
    val writePath = tmpFile("write")
    locally {
      val state = SkillFile.open(path, Create, Write)
      val d = state.Date.all
      assert(state.Date.size === 10 * limit, s"we somehow lost ${10 * limit - state.Date.size} dates")

      var cond = true
      for (i ← 0 until 10 * limit)
        cond &&= (i == d.next.date)
      assert(cond, "match failed")

      state.close
    }

    // check append against write
    locally {
      val s1 = SkillFile.open(path)
      val s2 = open(writePath)

      val i1 = s1.Date.all
      val i2 = s2.Date.all

      while (i1.hasNext) {
        assert(i1.next.date === i2.next.date)
      }

      assert(i1.hasNext === i2.hasNext, "state1 had less elements!")
    }
  }

  ignore("write 100k dates; append 9x100k; write 1m dates and check them all -- in two states") {
    val limit = 1e5.toInt
    val path = tmpFile("append")

    // write
    val σ = open(path, Create, Write)
    for (i ← 0 until limit)
      σ.Date(i)

    σ.flush
    // TODO σ.modeSwitch(Append)!!

    // append
    for (i ← 1 until 10) {
      for (v ← i * limit until limit + i * limit)
        σ.Date(v)

      // TODO [[σ.append]]
    }

    // read & check & write
    val writePath = tmpFile("write")
    locally {
      val state = open(path, Create, Write)
      val d = state.Date.all
      assert(state.Date.size === 10 * limit, s"we somehow lost ${10 * limit - state.Date.size} dates")

      var cond = true
      for (i ← 0 until 10 * limit)
        cond &&= (i == d.next.date)
      assert(cond, "match failed")

      state.close
    }

    // check append against write
    locally {
      val s1 = open(path)
      val s2 = open(writePath)

      val i1 = s1.Date.all
      val i2 = s2.Date.all

      while (i1.hasNext) {
        assert(i1.next.date === i2.next.date)
      }

      assert(i1.hasNext === i2.hasNext, "state1 had less elements!")
    }
  }

  ignore("write 100k dates; append 9x100k; write 1m dates and check them all -- in a single state") {
    val limit = 1e5.toInt
    val path = tmpFile("append")

    // write
    val σ = open(path, Create, Write)
    for (i ← 0 until limit)
      σ.Date(i)

    σ.flush
    // TODO modeswitch!

    // append
    for (i ← 1 until 10) {
      for (v ← i * limit until limit + i * limit)
        σ.Date(v)

      // TODO [[σ.append]]
    }

    // read & check & write
    val writePath = tmpFile("write")
    locally {
      val state = σ
      val d = state.Date.all
      assert(state.Date.size === 10 * limit, s"we somehow lost ${10 * limit - state.Date.size} dates")

      var cond = true
      for (i ← 0 until 10 * limit)
        cond &&= (i == d.next.date)
      assert(cond, "match failed")

      // TODO [[state.write(writePath)]]
    }

    // check append against write
    locally {
      val s1 = open(path)
      val s2 = open(writePath)

      val i1 = s1.Date.all
      val i2 = s2.Date.all

      while (i1.hasNext) {
        assert(i1.next.date === i2.next.date)
      }

      assert(i1.hasNext === i2.hasNext, "state1 had less elements!")
    }
  }
  
  test("write, append, check") {
    val path = tmpFile("date.write.append.check")

    locally {
      val σ = open(path, Create, Write)
      σ.Date(1)
      σ.Date(2)
      σ.Date(3)
      σ.close
    }

    locally {
      val σ = SkillFile.open(path, Read, Append)
      σ.Date(1)
      σ.Date(2)
      σ.Date(3)
      σ.close
    }

    assert("123123" === SkillFile.open(path).Date.all.map(_.date).mkString(""))
  }
}
