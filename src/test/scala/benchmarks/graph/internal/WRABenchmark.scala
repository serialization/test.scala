package benchmarks.graph.internal

import java.io.File
import java.nio.file.Files
import scala.Array.canBuildFrom
import scala.util.Random
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import scala.collection.mutable.HashSet
import benchmarks.graph.api.SkillState

/**
 * Write - Read - Append - Benchmark, based on the WSR'14 paper, but without sets.
 *
 * run with: -Xmx5G
 * @author Timm Felden
 */
@RunWith(classOf[JUnitRunner])
class WRABenchmark extends FunSuite {

  final def tmpFile(s : String) = {
    val r = File.createTempFile(s, ".sf")
    //    r.deleteOnExit
    r.toPath
  }

  // we use a deterministic random number generator, in order to get reproducible results over several runs
  val Random = new Random

  // set upper bound to 6 for nice results; max = 20; reduced to 8 for test-suite
  val counts = (12 to 16).map(7 << _).toArray

  // set to 10 for nice results; max = 300 (0⇀18); reduced for tests
  val repetitions = 2;

  def averageTime(test : Int ⇒ Long) = counts.map { n ⇒
    Random.setSeed(31337)
    var total = 0.0
    for (count ← 0 until repetitions) {
      System.gc
      // yield to the gc
      Thread.sleep((Math.sqrt(n) * 0.3).toLong);

      val t = System.nanoTime

      test(n)

      total += (System.nanoTime - t)
    }

    total *= 1e-9 / repetitions.toDouble
    println(f"$total%4.4f")
    total
  }

  def averageSize(implicit test : Int ⇒ Long) = counts.map { n ⇒
    Random.setSeed(31337)

    val size = test(n)

    println(size+" Bytes")
    size
  }

  /**
   * produces nice latex output; first size, then speed to get rid of JIT effects
   */
  def eval(name : String, test : Int ⇒ Long) = {
    println(name)
    (averageSize(test), averageTime(test))
  }

  test("make wsr14 results") {
    val cr = create
    System.gc
    System.runFinalization
    val wr = write
    System.gc
    System.runFinalization
    val re = read
    System.gc
    System.runFinalization
    val cr2 = create2
    System.gc
    System.runFinalization
    val ap = append

    // first plot: absolute time spend for each phase
    locally {
      println("""
\begin{figure}
 \begin{tikzpicture}
  \begin{loglogaxis}[
  xlabel={$n$},
  ylabel={time$[sec]$},
  legend style={
    at={(0.03,0.97)},
    anchor=north west,
    cells={anchor=center},
    inner xsep=2pt,inner ysep=2pt,nodes={inner sep=1pt,text depth=0.1em},
  },
  legend entries={create, write, read, create more, append},
  ]""")

      val results = Array(cr._2, wr._2, re._2, cr2._2, ap._2)

      for (r ← results)
        println(
          (
            for (i ← 0 until r.length)
              yield s"(${counts(i)},${r(i)})"
          ).mkString("""
    \addplot+[smooth] coordinates
     {""", " ", "};")
        )

      println("""
  \end{loglogaxis}
 \end{tikzpicture}
 \caption{time per node}
\end{figure}""")
    }

    // second plot: time spend for each phase divided by base count
    locally {
      println("""
\begin{figure}
 \begin{tikzpicture}
  \begin{semilogxaxis}[
  ymin=0,
  ymax=100,
  xlabel={$n$},
  ylabel={average speed$[\frac{MB}{sec}]$},
  legend style={
    at={(0.03,0.97)},
    anchor=north west,
    cells={anchor=center},
    inner xsep=2pt,inner ysep=2pt,nodes={inner sep=1pt,text depth=0.1em},
  },
  legend entries={write, read, append},
  ]""")

      locally {
        val r = wr
        val last = cr
        println(
          (
            for (i ← 0 until r._1.length)
              yield s"(${counts(i)},${1e-6 * r._1(i).toDouble / (r._2(i) - last._2(i)).toDouble})"
          ).mkString("""
    \addplot+[smooth] coordinates
     {""", " ", "};")
        )
      }
      locally {
        val r = re
        val last = wr
        println(
          (
            for (i ← 0 until r._1.length)
              yield s"(${counts(i)},${1e-6 * r._1(i).toDouble / (r._2(i) - last._2(i)).toDouble})"
          ).mkString("""
    \addplot+[smooth] coordinates
     {""", " ", "};")
        )
      }
      locally {
        val r = ap
        val last = cr2
        println(
          (
            for (i ← 0 until r._1.length)
              yield s"(${counts(i)},${1e-6 * r._1(i).toDouble / (r._2(i) - last._2(i)).toDouble})"
          ).mkString("""
    \addplot+[smooth] coordinates
     {""", " ", "};")
        )
      }

      println("""
  \end{semilogxaxis}
 \end{tikzpicture}
 \caption{average speed [MB/sec]}
\end{figure}""")
    }
  }

  def create = {
    val f = tmpFile("wsr.create");

    def t(n : Int) : Long = {
      val σ = SkillState.create;
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

      0L
    }

    eval("create", t)
  }

  def write = {
    val f = tmpFile("wsr.write");

    def t(n : Int) : Long = {
      val σ = SkillState.create;
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

      σ.write(f)
      Files.size(f)
    }

    eval("\\ +write", t)
  }

  def read = {
    val f = tmpFile("wsr.read");

    def t(n : Int) : Long = {
      locally {
        val σ = SkillState.create;
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

        σ.write(f)
      }

      locally {
        val σ = SkillState.read(f);
        Files.size(f)
      }
    }

    eval("\\ +read", t)
  }

  def create2 = {
    val f = tmpFile("wsr.append");

    def t(n : Int) : Long = {
      locally {
        val σ = SkillState.create;
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

        σ.write(f)
      }

      locally {
        val σ = SkillState.read(f);
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

        Files.size(f)
      }
    }

    eval("\\ +create2", t)
  }

  def append = {
    val f = tmpFile("wsr.append");

    def t(n : Int) : Long = {
      locally {
        val σ = SkillState.create;
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

        σ.write(f)
      }

      locally {
        val σ = SkillState.read(f);
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

        σ.append

        Files.size(f)
      }
    }

    eval("\\ +append", t)
  }
}