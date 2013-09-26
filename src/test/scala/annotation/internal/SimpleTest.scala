package annotation.internal

import org.junit.Test

import annotation.Date
import annotation.api.SkillState
import common.CommonTest

class SimpleTest extends CommonTest {

  @Test def readAnnotation: Unit = SkillState.read("annotationTest.sf")

  @Test def checkAnnotation: Unit = {
    val σ = SkillState.read("annotationTest.sf")
    val t = σ.getTests.next
    val d = σ.getDates.next
    assert(t.getF[Date] == d)
  }

  @Test def changeAnnotation: Unit = {
    val σ = SkillState.read("annotationTest.sf")
    val t = σ.getTests.next
    val d = σ.getDates.next
    t.setF(t)
    assert(t.getF[annotation.Test] == t)
  }

  @Test(expected = classOf[AnnotationTypeCastException]) def safeAnnotation: Unit = {
    val σ = SkillState.read("annotationTest.sf")
    val t = σ.getTests.next
    val d = σ.getDates.next
    // no its not
    if (t.getF[annotation.Test] == t)
      fail;
  }

}