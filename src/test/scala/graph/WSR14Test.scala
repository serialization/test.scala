package graph

import java.io.File
import java.io.PrintWriter
import java.nio.file.Files
import scala.Array.canBuildFrom
import scala.collection.mutable.HashSet
import scala.util.Random
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import graph.api.SkillState
import org.scalatest.junit.JUnitRunner

/**
 * This test is used to produce results for the WSR'14 paper.
 *
 * run with: -XX:MaxHeapFreeRatio=99 -Xmx4G -Xms4G
 * @author Timm Felden
 */
@RunWith(classOf[JUnitRunner])
class WSR14Test extends FunSuite {
  protected implicit def nameToPath(s: String) = new File("src/test/resources/"+s).toPath

  @inline final def tmpFile(s: String) = {
    val r = File.createTempFile(s, ".sf")
    //    r.deleteOnExit
    r.toPath
  }

  val writer = new PrintWriter(new File("raw.txt"))

  // we use a deterministic random number generator, in order to get reproducible results over several runs
  val Random = new Random

  // @note: increase to get larger test sets; we have to keep heap size below 1GB for the CI to work as expected
  val counts = (0 to 6).map(1000 * 1 << _).toArray

  // set to 10 for wsr results; reduced for tests
  val repetitions = 1;

  @inline def averageTime(test: Int ⇒ Long) = counts.map { n ⇒
    Random.setSeed(31337)
    var total = 0.0
    for (count ← 0 until repetitions) {
      System.gc
      System.runFinalization
      // yield to the gc
      Thread.sleep(1);
      // @note: we use a different thread to escape from a very wired memory leak produced by scala's parser combinators
      val t = new Thread(new Runnable {
        def run {
          val t = System.nanoTime

          test(n)

          total += (System.nanoTime - t)
        }
      })
      t.start
      t.join
    }

    total *= 1e-9 / repetitions.toDouble
    println(f"$total%4.4f")
    writer.write(f"$total%4.4f\n")
    writer.flush
    total
  }

  @inline def averageSize(implicit test: Int ⇒ Long) = counts.map { n ⇒
    Random.setSeed(31337)
    // @note: we use a different thread to escape from a very wired memory leak produced by scala's parser combinators
    System.gc
    var size = 0L
    val t = new Thread(new Runnable {
      def run {
        size = test(n)
      }
    })
    t.start
    t.join

    println(size+" Bytes")
    writer.write(f"$size\n")
    writer.flush
    size
  }

  /**
   * produces nice latex output; first size, then speed to get rid of JIT effects
   */
  @inline def eval(name: String, test: Int ⇒ Long) = {
    println(name)
    (averageSize(test), averageTime(test))
  }

  test("main") {
    writer.write(counts.mkString("", "\n", "\n"))
    val cr = create
    System.gc
    System.runFinalization
    val wr = write
    System.gc
    System.runFinalization
    val re = read
    System.gc
    System.runFinalization
    val ap = append

    writer.close

    // first plot: time for append against IO-time
    locally {
      val size = ap._1
      val time = ap._2

      println("""\begin{figure}
\begin{tikzpicture}
 \begin{semilogxaxis}
  \addplot coordinates""")
      //@note change the constant in the following expression to match raw IOspeed in s/B for read+write of the executing
      // machine
      println(counts.zip(wr._1).map({ case (c, s) ⇒ f"($c,${s / c})" }).mkString("{", " ", "};"))
      println("""  \addplot coordinates""")
      println(counts.zip(ap._1).map({ case (c, s) ⇒ f"($c,${0.5 * s / c})" }).mkString("{", " ", "};"))
      println("""
  \end{semilogxaxis}
\end{tikzpicture} 
\caption{size}
\end{figure}
""")
    }

    // second plot: time spend for each phase divided by base count
    locally {
      println("""
\begin{figure}
 \begin{tikzpicture}
  \begin{semilogxaxis}""")

      val results = Array(cr._2, wr._2, re._2, ap._2)

      for (r ← results)
        println(
          (
            for (i ← 0 until r.length)
              yield s"(${counts(i)},${r(i) / counts(i).toDouble})"
          ).mkString("""
    \addplot+[smooth] coordinates
     {""", " ", "};")
        )

      println("""
  \end{semilogxaxis}
 \end{tikzpicture}
 \caption{time taken}
\end{figure}""")
    }
  }

  def create = {
    val f = tmpFile("wsr.create");

    @inline def t(n: Int): Long = {
      val σ = SkillState.create;
      for (i ← 0 until n)
        σ.Node("black", HashSet())

      // create random colors
      for (n ← σ.Node.all)
        n.color = Random.nextInt(4) match {
          case 0 ⇒ "black"
          case 1 ⇒ "red"
          case 2 ⇒ "blue"
          case 3 ⇒ "green"
        }

      // add edges 100 tries
      val nodes = σ.Node.all.toArray
      for (node ← σ.Node.all; j ← 0 until 100)
        node.edges.add(nodes(Random.nextInt(nodes.length)));

      0L
    }

    eval("create", t)
  }

  def write = {
    val f = tmpFile("wsr.write");

    @inline def t(n: Int): Long = {
      val σ = SkillState.create;
      for (i ← 0 until n)
        σ.Node("black", HashSet())

      // create random colors
      for (n ← σ.Node.all)
        n.color = Random.nextInt(4) match {
          case 0 ⇒ "black"
          case 1 ⇒ "red"
          case 2 ⇒ "blue"
          case 3 ⇒ "green"
        }

      // add edges 100 tries
      val nodes = σ.Node.all.toArray
      for (node ← σ.Node.all; j ← 0 until 100)
        node.edges.add(nodes(Random.nextInt(nodes.length)));

      σ.write(f)
      Files.size(f)
    }

    eval("\\ +write", t)
  }

  def read = {
    val f = tmpFile("wsr.read");

    @inline def t(n: Int): Long = {
      locally {
        val σ = SkillState.create;
        for (i ← 0 until n)
          σ.Node("black", HashSet())

        // create random colors
        for (n ← σ.Node.all)
          n.color = Random.nextInt(4) match {
            case 0 ⇒ "black"
            case 1 ⇒ "red"
            case 2 ⇒ "blue"
            case 3 ⇒ "green"
          }

        // add edges 100 tries
        val nodes = σ.Node.all.toArray
        for (node ← σ.Node.all; j ← 0 until 100)
          node.edges.add(nodes(Random.nextInt(nodes.length)));

        σ.write(f)
      }

      locally {
        val σ = SkillState.read(f);
        Files.size(f)
      }
    }

    eval("\\ +read", t)
  }

  def append = {
    val f = tmpFile("wsr.append");

    @inline def t(n: Int): Long = {
      locally {
        val σ = SkillState.create;
        for (i ← 0 until n)
          σ.Node("black", HashSet())

        // create random colors
        for (n ← σ.Node.all)
          n.color = Random.nextInt(4) match {
            case 0 ⇒ "black"
            case 1 ⇒ "red"
            case 2 ⇒ "blue"
            case 3 ⇒ "green"
          }

        // add edges 100 tries
        val nodes = σ.Node.all.toArray
        for (node ← σ.Node.all; j ← 0 until 100)
          node.edges.add(nodes(Random.nextInt(nodes.length)));

        σ.write(f)
      }

      locally {
        val σ = SkillState.read(f);
        // add 100% orange nodes
        // fix, because pool access is not yet an indexed seq or something like that
        val nodes = σ.Node.all.toArray
        for (i ← 0 until n) {
          val n = σ.Node("orange", HashSet())
          for (j ← 0 until 100)
            n.edges.add(nodes(Random.nextInt(nodes.length)));
        }

        σ.append

        Files.size(f)
      }
    }

    eval("\\ +append", t)
  }
}