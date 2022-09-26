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
}