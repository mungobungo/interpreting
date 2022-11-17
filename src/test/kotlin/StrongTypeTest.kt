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

    private val emptyEnv = Environment(hashMapOf())
    private val emptyContext = Context(emptyEnv)

    private fun toEx(yaml: String): Expression {
        return parse(yaml).value!!.desugar().eval(emptyContext).value!!
    }

    @Test
    fun testSimpleInt() {
        assertTrue(typeOf(toEx("10")) is TInt)
        assertTrue(typeOf(toEx("1.02")) is TFloat)
        assertTrue(typeOf(toEx("true")) is TBool)
    }
}