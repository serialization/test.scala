package date
import org.junit.runner.RunWith
import common.CommonTest
import date.api.SkillState
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AppendTest extends CommonTest {

  test("Tr13 §6.2.3 based date example") {
    val path = tmpFile("writetest")

    val σ = SkillState.create
    σ.addDate(1)
    σ.addDate(-1)
    σ.write(path)

    assert(sha256(path) === sha256("date-example.sf"))
    assert(SkillState.read(path).getDates.map(_.getDate).toList.sameElements(List(1, -1)))
  }

  test("write 100k dates; append 9x100k; write 1m dates and check them all") {
    val limit = 1e5.toInt
    val path = tmpFile("append")

    // write
    locally {
      val σ = SkillState.create
      for (i ← 0 until limit)
        σ.addDate(i)

      σ.write(path)
    }

    // append
    for (i ← 1 until 10) {
      val σ = SkillState.read(path)
      for (v ← i * limit until limit + i * limit)
        σ.addDate(v)

      σ.append
    }

    // read & check & write
    val writePath = tmpFile("write")
    locally {
      val state = SkillState.read(path)
      val d = state.getDates
      assert(state.getDates.size === 10 * limit, s"we somehow lost ${10 * limit - state.getDates.size} dates")

      var cond = true
      for (i ← 0 until 10 * limit)
        cond &&= (i == d.next.getDate)
      assert(cond, "match failed")

      state.write(writePath)
    }

    // check append against write
    locally {
      val s1 = SkillState.read(path)
      val s2 = SkillState.read(writePath)

      val i1 = s1.getDates
      val i2 = s2.getDates

      while (i1.hasNext) {
        assert(i1.next.getDate === i2.next.getDate)
      }

      assert(i1.hasNext === i2.hasNext, "state1 had less elements!")
    }
  }

  ignore("write 100k dates; append 9x100k; write 1m dates and check them all -- in a single state") {
    // TODO implementation required
  }
}
