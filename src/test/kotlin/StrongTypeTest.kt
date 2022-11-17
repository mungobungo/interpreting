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
    fun testIntegerAddition(){

        assertEquals(TInt(), typeOf(toEx("[iadd, 4, 5]")))
        assertEquals(TInt(), typeOf(toEx("[iadd, 4, [iadd, 9, 100]]")))
    }
}