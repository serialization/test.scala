package benchmarks

import scala.collection.mutable.HashMap

object BenchmarkTools {
  /**
   * @param axisStyle "loglogaxis", "semilogxaxis", ...
   */
  def printGraph(caption:String, axisStyle : String, xs : Array[Int], ys : Seq[(String, HashMap[Int, Double])]) {
    println(s"""
\\begin{figure}
 \\begin{tikzpicture}
  \\begin{$axisStyle}[
  legend style={
    at={(0.03,0.97)},
    anchor=north west,
    cells={anchor=center},
    inner xsep=2pt,inner ysep=2pt,nodes={inner sep=1pt,text depth=0.1em},
  },
  legend entries=${ys.map { y ⇒ y._1 }.mkString("{", ", ", "}")},
  ]""")

    for (y ← ys)
      println(s"""
    \\addplot+[smooth] coordinates
      {${xs.map { x ⇒ s"($x,${y._2(x)})" }.mkString(" ")}};""")

    println(s"""
  \\end{$axisStyle}
 \\end{tikzpicture}
 \\caption{$caption}
\\end{figure}""")
  }
}