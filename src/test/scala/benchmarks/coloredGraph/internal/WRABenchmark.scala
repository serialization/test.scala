package benchmarks.coloredGraph.internal

import java.io.File
import java.nio.file.Files
import scala.Array.canBuildFrom
import scala.collection.mutable.HashMap
import scala.util.Random
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import scala.collection.mutable.HashSet
import benchmarks.coloredGraph.Color
import java.io.FileOutputStream
import java.io.BufferedOutputStream
import scala.sys.process._
import benchmarks.coloredGraph.api.SkillFile
import de.ust.skill.common.scala.api.Write
import de.ust.skill.common.scala.api.Read
import de.ust.skill.common.scala.api.Create
import benchmarks.coloredGraph.api.internal.NodePool

/**
 * Write - Read - Append - Benchmark, based on the WSR'14 paper, but without sets.
 *
 * run with: -Xmx5G
 * @author Timm Felden
 */
@RunWith(classOf[JUnitRunner])
class WRABenchmark extends FunSuite {
  import benchmarks.BenchmarkTools.printGraph

  final def tmpFile(s : String) = {
    val r = File.createTempFile(s, ".sf")
    //    r.deleteOnExit
    r.toPath
  }

  var timer = System.nanoTime;
  def init {
    timer = System.nanoTime;
  }
  case class Result(val name : String) {
    val timings = HashMap[Int, Double]()
    val speeds = HashMap[Int, Double]()

    /**
     * @param kind: none, read, write, append
     */
    def end(x : Int) {
      val t = System.nanoTime
      if (!timings.contains(x)) {
        // TODO factor this out to "reset" and add a "dryRunCount" to the test
        timings.put(x, 0.0)
        speeds.put(x, 0.0)
      }
      timings(x) += (t - timer) * 1e-9
      speeds(x) += (x.toDouble * 1e-6) / ((t - timer) * 1e-9)

      timer = System.nanoTime
    }

    def average(repetitions : Int) {
      for (x ← timings.keys)
        timings(x) /= repetitions.toDouble
    }
  }

  // we use a deterministic random number generator, in order to get reproducible results over several runs
  val random = new Random
  val randomSeed = 31337

  // set upper bound to 7 for nice results; max = 8??(fdp takes too long); reduced to 4 for test-suite
  val counts = (5 to 8).map(20 << _).toArray
  // results
  val create = Result("create")
  val write = Result("write")
  val read = Result("read")
  val createDot = Result("create dot")
  val results = Seq(create, write, read, createDot)

  // set to 10 for nice results; max = 100 (0⇀7); reduced to 1 for tests
  val repetitions = 2;

  def eval(test : Int ⇒ Unit) {
    for (count ← 0 until repetitions) {
      for (n ← counts) {
        Random.setSeed(31337)
        System.gc
        // yield to the gc
        Thread.sleep((Math.sqrt(n) * 0.3).toLong);

        test(n)
        print(".")
      }
      System.gc
      System.runFinalization
      Thread.sleep(4)
      println(s"[${count + 1}/$repetitions]")
    }
    for (r ← results)
      r.average(repetitions)
  }

  test("make wsr14 results") {
    eval(task)

    printGraph("total time taken", "loglogaxis", counts, results.map { r ⇒ (r.name, r.timings) })
    printGraph("$\\frac{MObjects}{sec}$", "semilogxaxis", counts, results.map { r ⇒ (r.name, r.speeds) })
  }

  def task(n : Int) {
    // for publication use "new File(s"out-$n.sf").toPath" instead
    val f = tmpFile("wsr.append");
    locally {
      init;
      val σ = SkillFile.open(f, Create, Write);
      // n nodes, random color 0->10 (for later distribution; keep below 255 for printing)
      for (i ← 0 until n)
        σ.Node.make(σ.Color.make(Random.nextInt(16).toByte, Random.nextInt(16).toByte, Random.nextInt(16).toByte), HashSet())

      // up to 10 random edges; distribute color over edge
      val nodes = σ.Node.filter(_.getSkillID == -1)
      @inline def merge(a : Color, b : Color) {
        a.red = (a.red + b.red).toByte
        a.green = (a.green + b.green).toByte
        a.blue = (a.blue + b.blue).toByte
      }
      for (node ← σ.Node; i ← 0 until 16) {
        val other = nodes(Random.nextInt(nodes.size))
        node.edges += other
        merge(node.color, other.color)
      }

      create.end(n);

      σ.close
      write.end(n);
    }

    locally {
      val σ = SkillFile.open(f, Read, Write);
      read.end(n);

      //      // create a dot file
      //      // for publication use "new File(s"out-$n.dot")" instead
      //      val dotFile = File.createTempFile(s"out-$n", ".dot")
      //      val dot = new BufferedOutputStream(new FileOutputStream(dotFile))
      //      @inline def put(s : String) {
      //        dot.write(s.getBytes())
      //      }
      //      put(s"digraph sfBenchmark$n{")
      //      for (n ← σ.Node) {
      //        put(f"""
      //  ${n.getSkillID}[label="",style=filled,color="#${n.color.red}%2X${n.color.green}%2X${n.color.blue}%2X"];
      //  ${n.getSkillID} -> ${n.edges.map(_.getSkillID).mkString("{", ";", "}")}[dir=none,color="#${n.color.red}%2X${n.color.green}%2X${n.color.blue}%2X"];""")
      //      }
      //      put("\n}")
      //      dot.close
      createDot.end(n)
    }
  }
}