
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
        assertEquals(SugarInt(42), parse("42").value!!)
    }
    @Test
    fun testSimpleAdd(){
        assertEquals(SugarAdd(SugarInt(3), SugarInt(5)), parse("[add, 3, 5]").value!!)
    }
    @Test
    fun testComplicatedAdd(){
        assertEquals(
            SugarAdd(SugarAdd(SugarInt(3), SugarInt(5)), SugarInt(42)),
            parse("[add, [add, 3,5], 42]").value!!)
    }
    @Test
    fun testSimpleMultiplication(){
        assertEquals(SugarMul(SugarInt(11), SugarInt(33)), parse("[mul, 11, 33]").value!!)
    }
    @Test
    fun testComplicatedMultiplication(){
        assertEquals(
            SugarMul(SugarMul(SugarInt(3), SugarInt(5)), SugarInt(42)),
            parse("[mul, [mul, 3,5], 42]").value!!)
    }
    @Test
    fun testMulAndAdd(){
         assertEquals(
            SugarMul(SugarAdd(SugarInt(22), SugarInt(11)), SugarInt(44)),
            parse("[mul, [add, 22, 11], 44]").value!!)
    }

    @Test
    fun testSimpleIntUnparse(){
        assertEquals("42",SugarInt(42).desugar().unparse())
        assertEquals("42", parse("42").value!!.desugar().unparse())
    }

    @Test
    fun testSimpleAddUnparse(){
        val yaml = "[add, 3, 5]"
        assertEquals(yaml, SugarAdd(SugarInt(3), SugarInt(5)).desugar().unparse())
        assertEquals(yaml, parse(yaml).value!!.desugar().unparse())
    }
    @Test
    fun testComplicatedAddUnparse(){
        val yaml = "[add, [add, 3, 5], 42]"
        assertEquals(
            yaml,
            SugarAdd(SugarAdd(SugarInt(3), SugarInt(5)), SugarInt(42)).desugar().unparse())
        assertEquals(yaml, parse(yaml).value!!.desugar().unparse())
    }
    @Test
    fun testSimpleMultiplicationUnparse(){
        val yaml = "[mul, 11, 33]"

        assertEquals(yaml, SugarMul(SugarInt(11), SugarInt(33)).desugar().unparse())
        assertEquals(yaml, parse(yaml).value!!.desugar().unparse())
    }
    @Test
    fun testComplicatedMultiplicationUnparse(){
        val yaml = "[mul, [mul, 3, 5], 42]"
        assertEquals(
            yaml,
            SugarMul(SugarMul(SugarInt(3), SugarInt(5)), SugarInt(42)).desugar().unparse())
        assertEquals(yaml, parse(yaml).value!!.desugar().unparse())
    }
    @Test
    fun testMulAndAddUnparse(){
        val yaml = "[mul, [add, 22, 11], 44]"
        assertEquals(
            yaml,
            SugarMul(SugarAdd(SugarInt(22), SugarInt(11)), SugarInt(44)).desugar().unparse())
        assertEquals(yaml, parse(yaml).value!!.desugar().unparse())
    }

    @Test
    fun testMulAndSubUnparse(){

        val yaml = "[mul, [sub, 22, 11], 44]"
        val desugared = "[mul, [add, 22, [mul, -1, 11]], 44]"
        assertEquals(
            desugared,
            SugarMul(SugarSub(SugarInt(22), SugarInt(11)), SugarInt(44)).desugar().unparse())
        assertEquals(desugared, parse(yaml).value!!.desugar().unparse())
    }
    @Test
    fun testNegUnparse()
    {
        val yaml = "[neg, [mul, 2, 10]]"
        val desugared = "[mul, -1, [mul, 2, 10]]"
        assertEquals(desugared, SugarNeg(SugarMul(SugarInt(2), SugarInt(10))).desugar().unparse())
        assertEquals(desugared, parse(yaml).value!!.desugar().unparse())
    }

    @Test
    fun testSymbolParsing()
    {
        val yaml = "x"

        assertEquals(SugarSymbol("x"), parse(yaml).value!!)
    }

    @Test
    fun testOperationWithSymbol(){
        assertEquals(SugarAdd(SugarSymbol("x"), SugarInt(5)), parse("[add, x, 5]").value!!)
        assertEquals(SugarAdd(SugarSymbol("x"), SugarSymbol("y")), parse("[add, x, y]").value!!)

        assertEquals(SugarAdd( SugarAdd(SugarSymbol("x"), SugarSymbol("y")), SugarSymbol("z")),
            parse("[add, [add, x, y], z]").value!!)

    }
}