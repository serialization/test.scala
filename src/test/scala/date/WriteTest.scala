package date

import java.io.File

import scala.collection.mutable.ArrayBuffer

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import common.CommonTest
import date.api.SkillState

@RunWith(classOf[JUnitRunner])
class WriteTest extends CommonTest {

  test("foo") {
    val low = -8e6.toInt
    val high = 8e6.toInt

    val dates = new ArrayBuffer[date.Date]
    for (i ← low until high)
      dates.append(new date.internal.types.Date(i))

    var time = System.nanoTime()
    @inline def updateTime = {
      println((System.nanoTime - time) * 1e-9)
      time = System.nanoTime
    }

    var min = Long.MaxValue
    for (i ← dates.size - 1 to 0 by -1)
      min = Math.min(dates(i).getDate, min)

    updateTime

    for (d ← dates)
      d.date -= min

    updateTime
  }

  test("§6.6 date example") {
    val file = File.createTempFile("writetest", ".sf")
    val path = file.toPath

    val σ = SkillState.create
    σ.addDate(1)
    σ.addDate(-1)
    σ.write(path)

    assert(sha256(path) === sha256("date-example.sf"))
    assert(SkillState.read(path).getDates.map(_.date).toList.sameElements(List(1, -1)))

    file.delete
  }

  test("write 10MB dates") {
    val low = -8e5.toInt
    val high = 8e5.toInt
    val file = File.createTempFile("writetest", ".sf")
    val path = file.toPath()

    val σ = SkillState.create
    for (i ← low until high)
      σ.addDate(i)

    val w = System.nanoTime().toDouble;
    σ.write(path)
    println("w:", (System.nanoTime() - w) * 1e-9)

    val r = System.nanoTime().toDouble;
    val d = SkillState.read(path).getDates
    var cond = true
    for (i ← low until high)
      cond &&= (i == d.next.getDate)
    assert(cond, "match failed")
    println("r:", (System.nanoTime() - r) * 1e-9)

    //    file.delete
  }

  test("normalize 100MB v64") {
    var time = System.nanoTime()
    @inline def updateTime = {
      println((System.nanoTime - time) * 1e-9)
      time = System.nanoTime
    }

    val σ = SkillState.read("normalizedInput.sf")

    updateTime
    var min = Long.MaxValue
    for (d ← σ.getDates)
      min = Math.min(d.getDate, min)

    updateTime

    for (d ← σ.getDates)
      d.date -= min

    updateTime

    val file = File.createTempFile("normalized", ".sf")
    σ.write(file.toPath);

    updateTime
  }
}
