
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

    @Test()
    fun testSimpleInt(){
        assertEquals(EInt(42), parse("42"))
    }
    @Test()
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
}