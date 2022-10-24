import core.EInt
import core.ESymbol
import core.Environment
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class EnvironmentTest {

    @Test
    fun isDefined() {
        val env = Environment(hashMapOf())
        assertFalse(env.isDefined(ESymbol("x")))
        val env2 = Environment(hashMapOf(Pair(ESymbol("x"), EInt(5))))
        assertTrue(env2.isDefined(ESymbol("x")))
    }

    @Test
    fun addBinding() {
        val env = Environment(hashMapOf())
        env.addBinding(ESymbol("x"), EInt(5))
        assertTrue(env.isDefined(ESymbol("x")))
        assertEquals(EInt(5), env.bindings[ESymbol("x")])
        assertEquals(EInt(5), env.get(ESymbol("x")))

    }


}