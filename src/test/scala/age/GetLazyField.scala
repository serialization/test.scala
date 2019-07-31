package age

import java.nio.file.Path

import org.junit.Assert

import ogss.common.scala.api.Read
import ogss.common.scala.api.Write
import ogss.common.scala.api.Create


import common.CommonTest

/**
 * Tests LazyField.
 */
class GetLazyField extends CommonTest {

    test("Get LazyField test - ??") {
        val sf = age.OGFile.open(tmpFile("lazyfield"), Create, Write);
        sf.Age.make;
        sf.close

        val sf2 = empty.OGFile.open(sf.currentPath, Read, Write);
        val o = sf2.allTypes.next().get(1);
        val f = sf2.allTypes.next().fields.next();
        f.get(o);
        
    } 
}
