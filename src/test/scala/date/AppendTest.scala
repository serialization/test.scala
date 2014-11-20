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

    val sf = open(path, Create, Write)
    sf.Date(1)
    sf.Date(-1)
    sf.close

    assert(sha256(path) === sha256(new File("src/test/resources/date-example.sf").toPath))
    assert(SkillFile.open(path).Date.all.map(_.date).toList.sameElements(List(1, -1)))
  }

  test("Tr13 §6.2.3 append example") {
    val path = tmpFile("append.test")

    val sf = open("src/test/resources/date-example.sf", Read, Append)
    sf.Date(2)
    sf.Date(3)
    sf.changePath(path)
    sf.close

    assert(sha256(path) === sha256(new File("src/test/resources/date-example-append.sf").toPath))
    assert(SkillFile.open(path, Read).Date.all.map(_.date).toList.sameElements(List(1, -1, 2, 3)))
  }

  test("write 100k dates; append 9x100k; write 1m dates and check them all -- multiple states") {
    val limit = 1e5.toInt
    val path = tmpFile("append")

    // write
    locally {
      val sf = open(path, Create, Write)
      for (i ← 0 until limit)
        sf.Date(i)

      sf.close
    }

    // append
    for (i ← 1 until 10) {
      val sf = SkillFile.open(path, Read, Append)
      for (v ← i * limit until limit + i * limit)
        sf.Date(v)

      sf.close
    }

    // read & check & write
    val writePath = tmpFile("write")
    locally {
      val state = SkillFile.open(path, Read, Write)
      val d = state.Date.all
      assert(state.Date.size === 10 * limit, s"we somehow lost ${10 * limit - state.Date.size} dates")

      var cond = true
      for (i ← 0 until 10 * limit)
        cond &&= (i == d.next.date)
      assert(cond, "match failed")

      state.changePath(writePath)
      state.close
    }

    // check append against write
    locally {
      val s1 = SkillFile.open(path, Read)
      val s2 = SkillFile.open(writePath, Read)

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
    val sf = open(path, Create, Write)
    for (i ← 0 until limit)
      sf.Date(i)

    sf.flush
    // TODO sf.modeSwitch(Append)!!

    // append
    for (i ← 1 until 10) {
      for (v ← i * limit until limit + i * limit)
        sf.Date(v)

      // TODO [[sf.append]]
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
    val sf = open(path, Create, Append)
    for (i ← 0 until limit)
      sf.Date(i)

    sf.flush

    // append
    for (i ← 1 until 10) {
      for (v ← i * limit until limit + i * limit)
        sf.Date(v)

      sf.flush
    }

    // read & check & write
    val writePath = tmpFile("write")
    locally {
      val state = SkillFile.open(path, Read, Write)
      val d = state.Date.all
      assert(state.Date.size === 10 * limit, s"we somehow lost ${10 * limit - state.Date.size} dates")

      var cond = true
      for (i ← 0 until 10 * limit)
        cond &&= (i == d.next.date)
      assert(cond, "match failed")

      sf.changePath(writePath)
      sf.close
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
      val sf = open(path, Create, Write)
      sf.Date(1)
      sf.Date(2)
      sf.Date(3)
      sf.close
    }

    locally {
      val sf = SkillFile.open(path, Read, Append)
      sf.Date(1)
      sf.Date(2)
      sf.Date(3)
      sf.close
    }

    assert("123123" === SkillFile.open(path).Date.all.map(_.date).mkString(""))
  }
}
