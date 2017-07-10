package common

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet
import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions
import scala.util.Random
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import de.ust.skill.common.scala.api.SkillObject
import de.ust.skill.common.scala.internal.FieldDeclaration
import de.ust.skill.common.scala.internal.SkillState
import de.ust.skill.common.scala.internal.fieldTypes.AnnotationType
import de.ust.skill.common.scala.internal.fieldTypes.BoolType
import de.ust.skill.common.scala.internal.fieldTypes.ConstantInteger
import de.ust.skill.common.scala.internal.fieldTypes.ConstantLengthArray
import de.ust.skill.common.scala.internal.fieldTypes.F32
import de.ust.skill.common.scala.internal.fieldTypes.F64
import de.ust.skill.common.scala.internal.fieldTypes.FieldType
import de.ust.skill.common.scala.internal.fieldTypes.I16
import de.ust.skill.common.scala.internal.fieldTypes.I32
import de.ust.skill.common.scala.internal.fieldTypes.I64
import de.ust.skill.common.scala.internal.fieldTypes.I8
import de.ust.skill.common.scala.internal.fieldTypes.ListType
import de.ust.skill.common.scala.internal.fieldTypes.MapType
import de.ust.skill.common.scala.internal.fieldTypes.SetType
import de.ust.skill.common.scala.internal.fieldTypes.StringType
import de.ust.skill.common.scala.internal.fieldTypes.UserType
import de.ust.skill.common.scala.internal.fieldTypes.V64
import de.ust.skill.common.scala.internal.fieldTypes.VariableLengthArray
import de.ust.skill.common.scala.internal.restrictions.NonNull
import de.ust.skill.common.scala.internal.restrictions.Range.RangeI8
import de.ust.skill.common.scala.internal.restrictions.Range.RangeI16
import de.ust.skill.common.scala.internal.restrictions.Range.RangeI32
import de.ust.skill.common.scala.internal.restrictions.Range.RangeI64
import de.ust.skill.common.scala.internal.restrictions.Range.RangeF32
import de.ust.skill.common.scala.internal.restrictions.Range.RangeF64

@RunWith(classOf[JUnitRunner])
class CommonTest extends FunSuite {
  @inline final def tmpFile(s: String) = {
    val r = File.createTempFile(s, ".sf")
    r.deleteOnExit
    r.toPath
  }

  final def createFile(packagePath: String, name: String) = {
    val dir = new File("src/test/resources/serializedTestfiles/" + packagePath);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    val f = new File("src/test/resources/serializedTestfiles/" + packagePath + name + ".sf");
    f.toPath
  }

  final def sha256(name: String): String = sha256(new File("src/test/resources/" + name).toPath)
  @inline final def sha256(path: Path): String = {
    val bytes = Files.readAllBytes(path)
    MessageDigest.getInstance("SHA-256").digest(bytes).map("%02X".format(_)).mkString
  }

  final def ∀[T](is: Iterable[T])(p: T ⇒ Boolean) = is.forall(p)

  /**
   * initialize a skill file using reflection
   *
   * allocates 100 instances for each type and fills all fields with random data
   *
   * collections will contain about 10 entries, or 2 keys
   */
  final def reflectiveInit(sf: SkillState) {
    // create instances
    for (t ← sf.par; i ← 0 until 100) try {
      t.reflectiveAllocateInstance
    } catch {
      case e: Exception ⇒ // can not be instantiated
    }

    // ensure existence of some strings
    for (t ← sf)
      sf.String.add(t.name)

    // set fields
    for (
      t ← sf.par;
      i ← t.par;
      f ← t.allFields
    ) try {
      // note: we will even set auto fields
      set(f.asInstanceOf[FieldDeclaration[_, _]], i, sf)
    } catch {
      case e: Throwable ⇒ // no legal value selected
    }
  }

  private final def set[T, Obj <: SkillObject](field: FieldDeclaration[_, _], i: SkillObject, sf: SkillState) {
    val f = field.asInstanceOf[FieldDeclaration[T, Obj]]

    f.setR(i, random(f.t, sf, f))
  }

  private final def isNonnull(f: FieldDeclaration[_, _]) = f.restrictions.contains(NonNull.theNonNull)
  private final def toRange(f: FieldDeclaration[_, _], value: Byte): Byte = {
    var r = value
    f.restrictions.foreach {
      case RangeI8(min, max) ⇒ r = (min + (r % (max - min))).toByte
      case _ ⇒
    }
    r
  }
  private final def toRange(f: FieldDeclaration[_, _], value: Short): Short = {
    var r = value
    f.restrictions.foreach {
      case RangeI16(min, max) ⇒ r = (min + (r % (max - min))).toShort
      case _ ⇒
    }
    r
  }
  private final def toRange(f: FieldDeclaration[_, _], value: Int): Int = {
    var r = value
    f.restrictions.foreach {
      case RangeI32(min, max) ⇒ r = (min + (r % (max - min)))
      case _ ⇒
    }
    r
  }
  private final def toRange(f: FieldDeclaration[_, _], value: Long): Long = {
    var r = value
    f.restrictions.foreach {
      case RangeI64(min, max) ⇒ r = (min + (r % (max - min)))
      case _ ⇒
    }
    r
  }
  private final def toRange(f: FieldDeclaration[_, _], value: Float): Float = {
    var r = value
    f.restrictions.foreach {
      case RangeF32(min, max) ⇒ r = (min + (r % (max - min)))
      case _ ⇒
    }
    r
  }
  private final def toRange(f: FieldDeclaration[_, _], value: Double): Double = {
    var r = value
    f.restrictions.foreach {
      case RangeF64(min, max) ⇒ r = (min + (r % (max - min)))
      case _ ⇒
    }
    r
  }

  private final def random[T](t: FieldType[T], sf: SkillState, f: FieldDeclaration[_, _]): T = t match {
    case t: ConstantInteger[_] ⇒ ???
    case t: AnnotationType ⇒
      if (!isNonnull(f) && Random.nextBoolean) null
      else {
        val t = sf(Random.nextInt(sf.size))
        t(1 + Random.nextInt(t.size - 1))
      }

    case t: StringType ⇒ {
      val i = sf.String.iterator.drop(
        if (isNonnull(f)) Random.nextInt(sf.String.size - 1)
        else Random.nextInt(sf.String.size))
      if (i.hasNext) i.next else null
    }

    case BoolType ⇒ Random.nextBoolean
    case I8 ⇒ toRange(f, Random.nextInt.toByte)
    case I16 ⇒ toRange(f, Random.nextInt.toShort)
    case I32 ⇒ toRange(f, Random.nextInt)
    case I64 ⇒ toRange(f, Random.nextLong)
    case V64 ⇒ toRange(f, Random.nextLong)

    case F32 ⇒ toRange(f, Random.nextFloat)
    case F64 ⇒ toRange(f, Random.nextDouble)

    case ConstantLengthArray(l, t) ⇒ ArrayBuffer() ++ (0 until l).map(i ⇒ random(t, sf, f)).to

    case VariableLengthArray(t) ⇒ ArrayBuffer() ++ (0 until Random.nextInt(20)).map(i ⇒ random(t, sf, f)).to
    case ListType(t) ⇒ ListBuffer() ++ (0 until Random.nextInt(20)).map(i ⇒ random(t, sf, f)).to
    case SetType(t) ⇒ HashSet() ++ (0 until Random.nextInt(20)).map(i ⇒ random(t, sf, f)).toSet.to

    case MapType(k, v) ⇒ makeMap(k, v, sf, f)

    case t: UserType[T] ⇒ t(
      if (isNonnull(f)) 1 + Random.nextInt(t.size - 1)
      else Random.nextInt(t.size))
  }

  private final def makeMap[K, V](k: FieldType[K], v: FieldType[V], sf: SkillState, f: FieldDeclaration[_, _]): HashMap[K, V] = {
    val r = new HashMap[K, V]
    for (i ← 0 until Random.nextInt(4))
      r(random(k, sf, f)) = random(v, sf, f)

    r
  }

  protected def getProperCollectionType(collectionType: String): String = {
    if (collectionType.contains("list")) {
      return "scala.collection.mutable.ListBuffer";
    } else if (collectionType.contains("set")) {
      return "scala.collection.mutable.HashSet";
    } else if (collectionType.contains("[]")) {
      return "scala.collection.mutable.ArrayBuffer";
    } else {
      throw new IllegalArgumentException("Could not parse provided SKilL collection type.\n" + "Type was: " + collectionType
        + "\n" + "Expected one of { 'list', 'set', 'array' }");
    }
  }

  protected def wrapPrimitveTypes(value: Double,
    declaration: de.ust.skill.common.scala.api.FieldDeclaration[_]): Any = {

    if (declaration.toString().contains("f32")) {
      if (Math.abs(value) > Float.MaxValue) {
        return value;
      } else {
        return value.toFloat;
      }
    } else if (declaration.toString().contains("f64")) {
      return value.toDouble;
    }
    if (declaration.toString().contains("i8")) {
      if (Math.abs(value) > Byte.MaxValue) {
        return value;
      } else {
        return value.toByte;
      }
    } else if (declaration.toString().contains("i16")) {
      if (Math.abs(value) > Short.MaxValue) {
        return value;
      } else {
        return value.toShort;
      }
    } else if (declaration.toString().contains("i32")) {
      if (Math.abs(value) > Integer.MAX_VALUE) {
        return value;
      } else {
        return value.toInt;
      }
    } else if (declaration.toString().contains("i64") || declaration.toString().contains("v64")) {
      if (Math.abs(value) > Long.MaxValue) {
        return value;
      } else {
        return value.toLong;
      }
    } else {
      throw new IllegalArgumentException(
        "The given fieldDeclaration is not supported.\n" + "Declaration was: " + declaration.toString()
          + "\n" + "But should contain one of the following : {'i8','i16','i32','i64,'f32','f64'}");
    }
  }

  protected def wrapPrimitveMapTypes(value: String,
    mapDeclaration: de.ust.skill.common.scala.api.FieldDeclaration[_], isKey: Boolean): Any = {
    def mapDeclarationSplit = mapDeclaration.toString().split(",");

    def declaration = if (isKey) mapDeclarationSplit(0) else mapDeclarationSplit(1);

    if (declaration.toString().contains("f32")) {
      return value.toFloat;
    } else if (declaration.toString().contains("f64")) {
      return value.toDouble;
    } else if (declaration.toString().contains("i8")) {
      return value.toByte;
    } else if (declaration.toString().contains("i16")) {
      return value.toShort;
    } else if (declaration.toString().contains("i32")) {
      return value.toInt;
    } else if (declaration.toString().contains("i64") || declaration.toString().contains("v64")) {
      return value.toLong;
    } else if (declaration.toString().contains("string")) {
      return value;
    } else if (declaration.toString().contains("bool")) {
      return value.toBoolean;
    } else {
      throw new IllegalArgumentException("The given fieldDeclaration is not supported.\n" + "Declaration was: "
        + declaration.toString() + "\n"
        + "But should contain one of the following : {'i8','i16','i32','i64,'f32','f64','string','bool'}");
    }
  }

}