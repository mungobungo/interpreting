
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
}