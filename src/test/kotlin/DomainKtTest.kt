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
    fun testSimpleInteger() {
        assertEquals(EInt(5), eval(EInt(5)))
        assertEquals(EInt(-5), eval(EInt(-5)))
        assertEquals(EInt(0), eval(EInt(0)))
    }

    @Test
    fun testSimpleAddition() {
        assertEquals(EInt(5), eval(EAdd(EInt(5), EInt(0))))
        assertEquals(EInt(5), eval(EAdd(EInt(0), EInt(5))))
        assertEquals(EInt(10), eval(EAdd(EInt(7), EInt(3))))
        assertEquals(EInt(7), eval(EAdd(EInt(-3), EInt(10))))
        assertEquals(EInt(6), eval(EAdd(EInt(10), EInt(-4))))
    }

    @Test
    fun testComplexAddition() {
        assertEquals(EInt(6), eval(EAdd(EInt(1), EAdd(EInt(2), EInt(3)))))
    }

    @Test
    fun testSimpleMultiplication() {
        assertEquals(EInt(0), eval(EMul(EInt(0), EInt(42))))
        assertEquals(EInt(0), eval(EMul(EInt(42), EInt(0))))
        assertEquals(EInt(42), eval(EMul(EInt(1), EInt(42))))
        assertEquals(EInt(42), eval(EMul(EInt(42), EInt(1))))
        assertEquals(EInt(4), eval(EMul(EInt(2), EInt(2))))
    }

    @Test
    fun testComplicatedMultiplication() {
        assertEquals(EInt(10), eval(EMul(EMul(EInt(1), EInt(5)), EInt(2))))
        assertEquals(EInt(12), eval(EMul(EAdd(EInt(1), EInt(5)), EInt(2))))
    }
}