import core.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import parser.parse
import sugared.*
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

    @Test
    fun testBasicTypes() {
        assertEquals("int", TInt().unparse())
        assertEquals("bool", TBool().unparse())
        assertEquals("float", TFloat().unparse())
        assertEquals("invalid_type", TInvalidType().unparse())
    }

    @Test
    fun testFuncTypes(){
        assertEquals("int -> int", TFunc(listOf(TInt()), TInt()).unparse())
        assertEquals("int -> int -> int", TFunc(listOf(TInt(), TInt()), TInt()).unparse())
        assertEquals("bool -> float -> int", TFunc(listOf(TBool(), TFloat()), TInt()).unparse())
        assertEquals("(int -> bool) -> int", TFunc(listOf(TFunc(listOf(TInt()), TBool())) , TInt()).unparse())
    }
}
