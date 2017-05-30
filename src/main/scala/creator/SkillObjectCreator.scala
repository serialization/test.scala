package creator

import de.ust.skill.common.scala.api.Access
import de.ust.skill.common.scala.api.SkillFile
import de.ust.skill.common.scala.internal.FieldDeclaration
import de.ust.skill.common.scala.api.SkillObject

class SkillObjectCreator {

  def generateSkillFileTypeMappings(sf: SkillFile): Map[String, Access[_]] = {
    val typeMappings: Map[String, Access[_]] = sf.map(t ⇒ t.name -> t).toMap;
    return typeMappings;
  }

  def generateSkillFileFieldMappings(sf: SkillFile): Map[String, Map[String, FieldDeclaration[_, _]]] = {
    val fieldMappings: Map[String, Map[String, FieldDeclaration[_, _]]] = sf.map(t ⇒ (t.name, t.fields.map(f => 
        (f.name, f.asInstanceOf[FieldDeclaration[_, _ <: SkillObject]])).toMap : Map[String, FieldDeclaration[_, _]] 
      )).toMap;
    return fieldMappings;
  }
}