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


    private fun toEx(yaml: String): Expression {
        return parse(yaml).value!!.desugar()
    }

    @Test
    fun testSimpleInt() {
        assertEquals(TInt(),  typeOf(toEx("10")))
        assertEquals(TFloat(), typeOf(toEx("1.02")))
        assertEquals(TBool(), typeOf(toEx("true")))
    }

    @Test
    fun testIntegerBinaryOperation(){

        assertEquals(TInt(), typeOf(toEx("[iadd, 4, 5]")))
        assertEquals(TInt(), typeOf(toEx("[iadd, 4, [iadd, 9, 100]]")))

        assertEquals(TInt(), typeOf(toEx("[iadd, 4, [imul, 9, [idiv, 10, 0]]]")))
    }
    @Test
    fun testFloatBinaryOperation(){

        assertEquals(TFloat(), typeOf(toEx("[fadd, 4.0, 5.0]")))
        assertTrue(typeOf(toEx("[fadd, 4, 5.0]")) is TypeError)
        assertEquals(TFloat(), typeOf(toEx("[fadd, 4.1, [fadd, 9.0, 100.0]]")))

        assertEquals(TFloat(), typeOf(toEx("[fadd, 4.0, [fmul, 9.0, [fdiv, 10.0, 0.0]]]")))
    }
}