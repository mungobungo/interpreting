import core.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import parser.parse
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class StrongTypeTest {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    val emptyTypeEnv = TypeEnv(hashMapOf())

    private fun toEx(yaml: String): Expression {
        return parse(yaml).value!!.desugar()
    }

    @Test
    fun testSimpleInt() {
        assertEquals(TInt().toScheme(),  typeOf(toEx("10"), emptyTypeEnv).result)
        assertEquals(TFloat().toScheme(), typeOf(toEx("1.02"), emptyTypeEnv).result)
        assertEquals(TBool().toScheme(), typeOf(toEx("true"),emptyTypeEnv).result)
    }

    @Test
    fun testIntegerBinaryOperation(){

        assertEquals(TInt().toScheme(), typeOf(toEx("[iadd, 4, 5]"),emptyTypeEnv).result)
        assertEquals(TInt().toScheme(), typeOf(toEx("[iadd, 4, [iadd, 9, 100]]"),emptyTypeEnv).result)

        assertEquals(TInt().toScheme(), typeOf(toEx("[iadd, 4, [imul, 9, [idiv, 10, 0]]]"),emptyTypeEnv).result)
    }
    @Test
    fun testFloatBinaryOperation(){

        assertEquals(TFloat().toScheme(), typeOf(toEx("[fadd, 4.0, 5.0]"),emptyTypeEnv).result)
        assertTrue(typeOf(toEx("[fadd, 4, 5.0]"),emptyTypeEnv).result.type is TypeError)
        assertEquals(TFloat().toScheme(), typeOf(toEx("[fadd, 4.1, [fadd, 9.0, 100.0]]"),emptyTypeEnv).result)

        assertEquals(TFloat().toScheme(), typeOf(toEx("[fadd, 4.0, [fmul, 9.0, [fdiv, 10.0, 0.0]]]"),emptyTypeEnv).result)
    }
    @Test
    fun testNumericBinaryOperation(){

        assertEquals(TFloat().toScheme(), typeOf(toEx("[add, 4.0, 5.0]"),emptyTypeEnv).result)
        assertEquals(TFloat().toScheme(), typeOf(toEx("[add, 4.0, 5]"),emptyTypeEnv).result)
        assertEquals(TFloat().toScheme(), typeOf(toEx("[add, 4, 5.0]"),emptyTypeEnv).result)
        assertEquals(TInt().toScheme(), typeOf(toEx("[add, 4, 5]"),emptyTypeEnv).result)
        assertEquals(TFloat().toScheme(), typeOf(toEx("[add, 4.1, [add, 9, 100.0]]"),emptyTypeEnv).result)
        assertEquals(TFloat().toScheme(), typeOf(toEx("[mul, 4.0, [add, 9.0, [idiv, 10, 0]]]"),emptyTypeEnv).result)
    }
    @Test
    fun testBinaryBoolOperation(){

        assertEquals(TBool().toScheme(), typeOf(toEx("[and, True, False]"), emptyTypeEnv).result)
        assertEquals(TBool().toScheme(), typeOf(toEx("[and, True, [or, False, True]]"),emptyTypeEnv).result)
        assertEquals(TBool().toScheme(), typeOf(toEx("[or, True, [and, True, False]]"),emptyTypeEnv).result)
    }
    @Test
    fun testSetVar(){

        assertEquals(TBool().toScheme(), typeOf(toEx("[setvar, x , True]"), emptyTypeEnv).result)
        assertEquals(TInt().toScheme(), typeOf(toEx("[setvar, x, 12]"),emptyTypeEnv).result)
    }
}