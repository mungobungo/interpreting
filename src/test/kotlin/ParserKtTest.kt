
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ParserKtTest {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun testSimpleInt(){
        assertEquals(EInt(42), parse("42"))
    }
    @Test
    fun testSimpleAdd(){
        assertEquals(EAdd(EInt(3), EInt(5)), parse("[add, 3, 5]"))
    }
    @Test
    fun testComplicatedAdd(){
        assertEquals(
            EAdd(EAdd(EInt(3), EInt(5)), EInt(42)),
            parse("[add, [add, 3,5], 42]"))
    }
    @Test
    fun testSimpleMultiplication(){
        assertEquals(EMul(EInt(11), EInt(33)), parse("[mul, 11, 33]"))
    }
    @Test
    fun testComplicatedMultiplication(){
        assertEquals(
            EMul(EMul(EInt(3), EInt(5)), EInt(42)),
            parse("[mul, [mul, 3,5], 42]"))
    }
    @Test
    fun testMulAndAdd(){
         assertEquals(
            EMul(EAdd(EInt(22), EInt(11)), EInt(44)),
            parse("[mul, [add, 22, 11], 44]"))
    }

    @Test
    fun testSimpleIntUnparse(){
        assertEquals("42",EInt(42).unparse())
        assertEquals("42", parse("42").unparse())
    }

    @Test
    fun testSimpleAddUnparse(){
        val yaml = "[add, 3, 5]"
        assertEquals(yaml, EAdd(EInt(3), EInt(5)).unparse())
        assertEquals(yaml, parse(yaml).unparse())
    }
    @Test
    fun testComplicatedAddUnparse(){
        val yaml = "[add, [add, 3, 5], 42]"
        assertEquals(
            yaml,
            EAdd(EAdd(EInt(3), EInt(5)), EInt(42)).unparse())
        assertEquals(yaml, parse(yaml).unparse())
    }
    @Test
    fun testSimpleMultiplicationUnparse(){
        val yaml = "[mul, 11, 33]"

        assertEquals(yaml, EMul(EInt(11), EInt(33)).unparse())
        assertEquals(yaml, parse(yaml).unparse())
    }
    @Test
    fun testComplicatedMultiplicationUnparse(){
        val yaml = "[mul, [mul, 3, 5], 42]"
        assertEquals(
            yaml,
            EMul(EMul(EInt(3), EInt(5)), EInt(42)).unparse())
        assertEquals(yaml, parse(yaml).unparse())
    }
    @Test
    fun testMulAndAddUnparse(){
        val yaml = "[mul, [add, 22, 11], 44]"
        assertEquals(
            yaml,
            EMul(EAdd(EInt(22), EInt(11)), EInt(44)).unparse())
        assertEquals(yaml, parse(yaml).unparse())
    }
}