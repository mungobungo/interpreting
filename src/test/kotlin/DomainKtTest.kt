import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DomainKtTest {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }
    @Test
    fun testSimpleInteger(){
        assertEquals(EInt(5), eval(EInt(5)))
        assertEquals(EInt(-5), eval(EInt(-5)))
        assertEquals(EInt(0), eval(EInt(0)))
    }
    @Test
    fun testSimpleAddition(){
        assertEquals(EInt(5), eval(EAdd(EInt(5), EInt(0))))
        assertEquals(EInt(5), eval(EAdd(EInt(0), EInt(5))))
        assertEquals(EInt(10), eval(EAdd(EInt(7), EInt(3))))
        assertEquals(EInt(7), eval(EAdd(EInt(-3), EInt(10))))
        assertEquals(EInt(6), eval(EAdd(EInt(10), EInt(-4))))
    }
    @Test
    fun testComplexAddition(){
        assertEquals(EInt(6), eval( EAdd(EInt(1), EAdd( EInt(2),EInt(3)) )))
    }
}