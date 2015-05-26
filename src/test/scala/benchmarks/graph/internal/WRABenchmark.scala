package benchmarks.graph.internal

import java.io.File
import scala.Array.canBuildFrom
import scala.collection.mutable.HashMap
import scala.util.Random
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import benchmarks.graph.api._
import org.scalatest.junit.JUnitRunner
import java.nio.file.Files

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
    r.deleteOnExit
    r.toPath
  }

  val f = tmpFile("wsr.append");
  var timer = System.nanoTime;
  def init {
    Files.deleteIfExists(f)
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

  // set upper bound to 6 for nice results; max = 20; reduced to 8 for test-suite
  val counts = (4 to 6).map(7 << _).toArray
  // results
  val create = Result("create")
  val write = Result("write")
  val read = Result("read")
  val create2 = Result("create more")
  val append = Result("append")
  val results = Seq(create, write, read, create2, append)

  // set to 10 for nice results; max = 300 (0⇀18); reduced for tests
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
    locally {
      init;
      val σ = SkillFile.open(f, Create, Write);
      for (i ← 0 until n)
        σ.Node(null, null, null, null)

      // random edges
      val nodes = σ.Node.asInstanceOf[NodeStoragePool].newObjects
      for (node ← σ.Node) {
        node.north = nodes(Random.nextInt(nodes.size))
        node.south = nodes(Random.nextInt(nodes.size))
        node.east = nodes(Random.nextInt(nodes.size))
        node.west = nodes(Random.nextInt(nodes.size))
      }

      create.end(n);

      σ.close
      write.end(n);
    }

    locally {
      val σ = SkillFile.open(f, Read, Append);
      read.end(n);

      // add 100% orange nodes
      // fix, because pool access is not yet an indexed seq or something like that
      val nodes = σ.Node.asInstanceOf[NodeStoragePool].data
      for (i ← 0 until n) {
        σ.Node(
          nodes(Random.nextInt(nodes.size)),
          nodes(Random.nextInt(nodes.size)),
          nodes(Random.nextInt(nodes.size)),
          nodes(Random.nextInt(nodes.size))
        )
      }
      create2.end(n)

      σ.close
      append.end(n)
    }
  }
}