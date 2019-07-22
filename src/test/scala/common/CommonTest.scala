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
import ogss.common.scala.internal.State
import ogss.common.scala.internal.Field
import ogss.common.scala.internal.Obj
import ogss.common.scala.internal.FieldType
import ogss.common.scala.internal.AnyRefType
import ogss.common.scala.internal.StringPool
import ogss.common.scala.internal.fieldTypes.Bool
import ogss.common.scala.internal.fieldTypes.I8
import ogss.common.scala.internal.fieldTypes.I16
import ogss.common.scala.internal.fieldTypes.I32
import ogss.common.scala.internal.fieldTypes.I64
import ogss.common.scala.internal.fieldTypes.V64
import ogss.common.scala.internal.fieldTypes.F32
import ogss.common.scala.internal.fieldTypes.F64
import ogss.common.scala.internal.fieldTypes.ArrayType
import ogss.common.scala.internal.fieldTypes.SetType
import ogss.common.scala.internal.fieldTypes.MapType
import ogss.common.scala.internal.fieldTypes.ListType
import ogss.common.scala.internal.fieldTypes.ContainerType
import ogss.common.scala.internal.Pool
import ogss.common.scala.api.FieldAccess

@RunWith(classOf[JUnitRunner])
class CommonTest extends FunSuite {
  @inline final def tmpFile(s : String) = {
    val r = File.createTempFile(s, ".sf")
    r.deleteOnExit
    r.toPath
  }

  final def createFile(packagePath : String, name : String) = {
    val dir = new File("src/test/resources/serializedTestfiles/" + packagePath);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    val file = new File("src/test/resources/serializedTestfiles/" + packagePath + name + ".sf");
    if (file.exists()) {
      file.delete();
    }
    file.toPath();
  }

  final def sha256(name : String) : String = sha256(new File("src/test/resources/" + name).toPath)
  @inline final def sha256(path : Path) : String = {
    val bytes = Files.readAllBytes(path)
    MessageDigest.getInstance("SHA-256").digest(bytes).map("%02X".format(_)).mkString
  }

  final def ∀[T](is : Iterable[T])(p : T ⇒ Boolean) = is.forall(p)

  /**
   * initialize a skill file using reflection
   *
   * allocates 100 instances for each type and fills all fields with random data
   *
   * collections will contain about 10 entries, or 2 keys
   */
  final def reflectiveInit(sf : State) {
    // create instances
    for (t ← sf.allTypes.toSeq.par; i ← 0 until 100) try {
      t.make
    } catch {
      case e : Exception ⇒ // can not be instantiated
    }

    // set fields
    for (
      t ← sf.allTypes.toSeq.par;
      i ← t.par;
      f ← t.allFields
    ) try {
      // note: we will even set auto fields
      set(f.asInstanceOf[Field[_, _]], i, sf)
    } catch {
      case e : Throwable ⇒ // no legal value selected
    }
  }

  private final def set[T, Ref <: Obj](field : Field[_, _], i : Obj, sf : State) {
    val f = field.asInstanceOf[Field[T, Ref]]

    f.set(i, random(f.t, sf, f))
  }

  private final def isNonnull(f : Field[_, _]) = false //f.restrictions.contains(NonNull.theNonNull)
  private final def toRange(f : Field[_, _], value : Byte) : Byte = {
    var r = value
    //    f.restrictions.foreach {
    //      case RangeI8(min, max) ⇒ r = (min + (r % (max - min))).toByte
    //      case _                 ⇒
    //    }
    r
  }
  private final def toRange(f : Field[_, _], value : Short) : Short = {
    var r = value
    //    f.restrictions.foreach {
    //      case RangeI16(min, max) ⇒ r = (min + (r % (max - min))).toShort
    //      case _                  ⇒
    //    }
    r
  }
  private final def toRange(f : Field[_, _], value : Int) : Int = {
    var r = value
    //    f.restrictions.foreach {
    //      case RangeI32(min, max) ⇒ r = (min + (r % (max - min)))
    //      case _                  ⇒
    //    }
    r
  }
  private final def toRange(f : Field[_, _], value : Long) : Long = {
    var r = value
    //    f.restrictions.foreach {
    //      case RangeI64(min, max) ⇒ r = (min + (r % (max - min)))
    //      case _                  ⇒
    //    }
    r
  }
  private final def toRange(f : Field[_, _], value : Float) : Float = {
    var r = value
    //    f.restrictions.foreach {
    //      case RangeF32(min, max) ⇒ r = (min + (r % (max - min)))
    //      case _                  ⇒
    //    }
    r
  }
  private final def toRange(f : Field[_, _], value : Double) : Double = {
    var r = value
    //    f.restrictions.foreach {
    //      case RangeF64(min, max) ⇒ r = (min + (r % (max - min)))
    //      case _                  ⇒
    //    }
    r
  }

  private final def random[T](t : FieldType[T], sf : State, f : Field[_, _]) : T = t match {
    case t : AnyRefType ⇒
      if (!isNonnull(f) && Random.nextBoolean) null
      else {
        val ts = sf.allTypes.toArray
        val t = ts(Random.nextInt(ts.size))
        t.iterator.drop(Random.nextInt(t.size - 1)).next
      }

    case t : StringPool ⇒ {
      val i = t.iterator.drop(t.size)
      if (i.hasNext) i.next else null
    }

    case Bool                                       ⇒ Random.nextBoolean
    case I8                                         ⇒ toRange(f, Random.nextInt.toByte)
    case I16                                        ⇒ toRange(f, Random.nextInt.toShort)
    case I32                                        ⇒ toRange(f, Random.nextInt)
    case I64                                        ⇒ toRange(f, Random.nextLong)
    case V64                                        ⇒ toRange(f, Random.nextLong)

    case F32                                        ⇒ toRange(f, Random.nextFloat)
    case F64                                        ⇒ toRange(f, Random.nextDouble)

    case t : ContainerType[_] if Random.nextBoolean ⇒ null.asInstanceOf[T] // choose null containers randomly
    case t : ArrayType[_]                           ⇒ ArrayBuffer() ++ (0 until Random.nextInt(20)).map(i ⇒ random(t.base, sf, f)).to
    case t : ListType[_]                            ⇒ ListBuffer() ++ (0 until Random.nextInt(20)).map(i ⇒ random(t.base, sf, f)).to
    case t : SetType[_]                             ⇒ HashSet() ++ (0 until Random.nextInt(20)).map(i ⇒ random(t.base, sf, f)).toSet.to

    case t : MapType[_, _]                          ⇒ makeMap(t.keyType, t.valueType, sf, f)

    case t : Pool[T] ⇒
      val i = t.iterator.drop(t.size)
      if (i.hasNext) i.next else null.asInstanceOf[T]
  }

  private final def makeMap[K, V](k : FieldType[K], v : FieldType[V], sf : State, f : Field[_, _]) : HashMap[K, V] = {
    val r = new HashMap[K, V]
    for (i ← 0 until Random.nextInt(4))
      r(random(k, sf, f)) = random(v, sf, f)

    r
  }

  protected def array[T](elements : T*) : ArrayBuffer[T] = {
    val r = new ArrayBuffer[T]
    for (e ← elements)
      r += e
    r
  }
  protected def list[T](elements : T*) : ListBuffer[T] = {
    val r = new ListBuffer[T]
    for (e ← elements)
      r += e
    r
  }
  protected def set[T](elements : T*) : HashSet[T] = {
    val r = new HashSet[T]
    for (e ← elements)
      r += e
    r
  }
  protected def map[K, V] = new HashMap[K, V];

  protected def put[K, V](m : HashMap[K, V], k : K, v : V) : HashMap[K, V] = {
    m.put(k, v)
    m
  }

  protected def initCollection[T](t : FieldType[T], f : Field[_, _], elements : Any*) : T = ???

  // @note the following code was taken from SKilL/Scala, but was obviously never correct (likely a bug in Scala's type theory)
  //  protected def initCollection[T](t : FieldType[T], f : Field[_, _], elements : Any*) : T = t match {
  //    case t : ArrayType[_] ⇒ ArrayBuffer() ++ elements
  //    case t : ListType[_]  ⇒ ListBuffer() ++ elements
  //    case t : SetType[_]   ⇒ HashSet() ++ elements
  //  }

  protected def getProperCollectionType(collectionType : String) : String = {
    if (collectionType.contains("list")) {
      return "scala.collection.mutable.ListBuffer";
    } else if (collectionType.contains("set")) {
      return "scala.collection.mutable.HashSet";
    } else if (collectionType.contains("[")) {
      return "scala.collection.mutable.ArrayBuffer";
    } else {
      throw new IllegalArgumentException("Could not parse provided SKilL collection type.\n" + "Type was: " + collectionType
        + "\n" + "Expected one of { 'list', 'set', 'array' }");
    }
  }

  protected def wrapPrimitveTypes(
    value :       Double,
    declaration : FieldAccess[_]
  ) : Any = {

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
        "The given Field is not supported.\n" + "Declaration was: " + declaration.toString()
          + "\n" + "But should contain one of the following : {'i8','i16','i32','i64,'f32','f64'}"
      );
    }
  }

  protected def wrapPrimitveTypes(
    value :       Double,
    declaration : String
  ) : Any = {

    if (declaration.contains("f32")) {
      if (Math.abs(value) > Float.MaxValue) {
        return value;
      } else {
        return value.toFloat;
      }
    } else if (declaration.contains("f64")) {
      return value.toDouble;
    }
    if (declaration.contains("i8")) {
      if (Math.abs(value) > Byte.MaxValue) {
        return value;
      } else {
        return value.toByte;
      }
    } else if (declaration.contains("i16")) {
      if (Math.abs(value) > Short.MaxValue) {
        return value;
      } else {
        return value.toShort;
      }
    } else if (declaration.contains("i32")) {
      if (Math.abs(value) > Integer.MAX_VALUE) {
        return value;
      } else {
        return value.toInt;
      }
    } else if (declaration.contains("i64") || declaration.contains("v64")) {
      if (Math.abs(value) > Long.MaxValue) {
        return value;
      } else {
        return value.toLong;
      }
    } else {
      throw new IllegalArgumentException(
        "The given Field is not supported.\n" + "Declaration was: " + declaration
          + "\n" + "But should contain one of the following : {'i8','i16','i32','i64,'f32','f64'}"
      );
    }
  }

  protected def wrapPrimitveMapTypes(
    value :          String,
    mapDeclaration : FieldAccess[_], isKey : Boolean
  ) : Any = {
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
      throw new IllegalArgumentException("The given Field is not supported.\n" + "Declaration was: "
        + declaration.toString() + "\n"
        + "But should contain one of the following : {'i8','i16','i32','i64,'f32','f64','string','bool'}");
    }
  }

  protected def wrapPrimitveMapTypes(
    value :          String,
    mapDeclaration : String, isKey : Boolean
  ) : Any = {
    def mapDeclarationSplit = mapDeclaration.split(",");

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
      throw new IllegalArgumentException("The given Field is not supported.\n" + "Declaration was: "
        + declaration.toString() + "\n"
        + "But should contain one of the following : {'i8','i16','i32','i64,'f32','f64','string','bool'}");
    }
  }

}