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
        assertEquals(EInt(5), EInt(5).eval())
        assertEquals(EInt(-5),(EInt(-5)).eval())
        assertEquals(EInt(0), EInt(0).eval())
    }

    @Test
    fun testSimpleAddition() {
        assertEquals(EInt(5), EAdd(EInt(5), EInt(0)).eval())
        assertEquals(EInt(5), (EAdd(EInt(0), EInt(5))).eval())
        assertEquals(EInt(10),(EAdd(EInt(7), EInt(3))).eval())
        assertEquals(EInt(7), (EAdd(EInt(-3), EInt(10))).eval())
        assertEquals(EInt(6), (EAdd(EInt(10), EInt(-4))).eval())
    }

    @Test
    fun testComplexAddition() {
        assertEquals(EInt(6), (EAdd(EInt(1), EAdd(EInt(2), EInt(3)))).eval())
    }

    @Test
    fun testSimpleMultiplication() {
        assertEquals(EInt(0), (EMul(EInt(0), EInt(42))).eval())
        assertEquals(EInt(0), (EMul(EInt(42), EInt(0))).eval())
        assertEquals(EInt(42),(EMul(EInt(1), EInt(42))).eval())
        assertEquals(EInt(42), (EMul(EInt(42), EInt(1))).eval())
        assertEquals(EInt(4), (EMul(EInt(2), EInt(2))).eval())
    }

    @Test
    fun testComplicatedMultiplication() {
        assertEquals(EInt(10), (EMul(EMul(EInt(1), EInt(5)), EInt(2))).eval())
        assertEquals(EInt(12), (EMul(EAdd(EInt(1), EInt(5)), EInt(2))).eval())
    }
    @Test
    fun testComplicatedExpressionWithParsing() {
        assertEquals(EInt(10), (EMul(EMul(EInt(1), EInt(5)), EInt(2))).eval())
        assertEquals(EInt(10), parse("[mul, [mul, 1, 5], 2]").eval())
        assertEquals(EInt(12), (EMul(EAdd(EInt(1), EInt(5)), EInt(2))).eval())
        assertEquals(EInt(12), parse("[mul, [add, 1, 5], 2]").eval())

    }

    @Test
    fun testSimpleIntegerWithParsing() {
        assertEquals(EInt(5), parse("5").eval())
        assertEquals(EInt(-5),parse("-5").eval())
        assertEquals(EInt(0), parse("0").eval())
    }

    @Test
    fun testSimpleAdditionWithParsing() {
        assertEquals(EInt(5), parse("[add, 5, 0]").eval())
        assertEquals(EInt(5), parse("[add, 0, 5]").eval())
        assertEquals(EInt(10),parse("[add, 7, 3]").eval())
        assertEquals(EInt(7), parse("[add, -3, 10]").eval())
        assertEquals(EInt(6), parse("[add, 10, -4]").eval())
    }

    @Test
    fun testComplexAdditionWithParsing() {
        assertEquals(EInt(6),  parse("[add, 1, [add, 2,3]]").eval())
    }

    @Test
    fun testSimpleMultiplicationWithParsing() {
        assertEquals(EInt(0), parse("[mul, 0, 42]").eval())
        assertEquals(EInt(0), parse("[mul, 42, 0]").eval())
        assertEquals(EInt(42),parse("[mul, 1, 42]").eval())
        assertEquals(EInt(42), parse("[mul, 42, 1]").eval())
        assertEquals(EInt(4), parse("[mul, 2, 2]").eval())
    }

    @Test
    fun testComplicatedMultiplicationWithParsing() {
        assertEquals(EInt(10), parse("[mul, 1, [mul, 5, 2]]").eval())
        assertEquals(EInt(12), parse("[mul, 2, [add, 5, 1]]").eval())

        assertEquals("10", parse("[mul, 1, [mul, 5, 2]]").eval().unparse())
        assertEquals("12", parse("[mul, 2, [add, 5, 1]]").eval().unparse())
    }

}