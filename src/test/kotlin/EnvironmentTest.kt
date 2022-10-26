import core.EInt
import core.ESymbol
import core.Environment
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class EnvironmentTest {

    @Test
    fun isDefined() {
        val env = Environment(hashMapOf())
        assertFalse(env.isDefined("x"))
        val env2 = Environment(hashMapOf(Pair("x", EInt(5))))
        assertTrue(env2.isDefined("x"))
    }

    @Test
    fun addBinding() {
        val env = Environment(hashMapOf())
        env.addBinding("x", EInt(5))
        assertTrue(env.isDefined("x"))
        assertEquals(EInt(5), env.bindings["x"])
    }
}