import core.*
import parser.*
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

    private val emptyEnv = Environment(hashMapOf())
    @Test
    fun testSimpleInteger() {
        assertEquals(EInt(5), EInt(5).eval().value!!)
        assertEquals(EInt(-5),(EInt(-5)).eval().value!!)
        assertEquals(EInt(0), EInt(0).eval().value!!)
    }

    @Test
    fun testSimpleAddition() {
        assertEquals(EInt(5), EIntAdd(EInt(5), EInt(0)).eval().value!!)
        assertEquals(EInt(5), (EIntAdd(EInt(0), EInt(5))).eval().value!!)
        assertEquals(EInt(10),(EIntAdd(EInt(7), EInt(3))).eval().value!!)
        assertEquals(EInt(7), (EIntAdd(EInt(-3), EInt(10))).eval().value!!)
        assertEquals(EInt(6), (EIntAdd(EInt(10), EInt(-4))).eval().value!!)
    }

    @Test
    fun testComplexAddition() {
        assertEquals(EInt(6), (EIntAdd(EInt(1), EIntAdd(EInt(2), EInt(3)))).eval().value!!)
    }

    @Test
    fun testSimpleMultiplication() {
        assertEquals(EInt(0), (EIntMul(EInt(0), EInt(42))).eval().value!!)
        assertEquals(EInt(0), (EIntMul(EInt(42), EInt(0))).eval().value!!)
        assertEquals(EInt(42),(EIntMul(EInt(1), EInt(42))).eval().value!!)
        assertEquals(EInt(42), (EIntMul(EInt(42), EInt(1))).eval().value!!)
        assertEquals(EInt(4), (EIntMul(EInt(2), EInt(2))).eval().value!!)
    }

    @Test
    fun testComplicatedMultiplication() {
        assertEquals(EInt(10), (EIntMul(EIntMul(EInt(1), EInt(5)), EInt(2))).eval().value!!)
        assertEquals(EInt(12), (EIntMul(EIntAdd(EInt(1), EInt(5)), EInt(2))).eval().value!!)
    }
    @Test
    fun testComplicatedExpressionWithParsing() {
        assertEquals(EInt(10), (EIntMul(EIntMul(EInt(1), EInt(5)), EInt(2))).eval().value!!)
        assertEquals(EInt(10), parse("[mul, [mul, 1, 5], 2]").value!!.desugar().eval().value!!)
        assertEquals(EInt(12), (EIntMul(EIntAdd(EInt(1), EInt(5)), EInt(2))).eval().value!!)
        assertEquals(EInt(12), parse("[mul, [add, 1, 5], 2]").value!!.desugar().eval().value!!)

    }

    @Test
    fun testSimpleIntegerWithParsing() {
        assertEquals(EInt(5), parse("5").value!!.desugar().eval().value!!)
        assertEquals(EInt(-5),parse("-5").value!!.desugar().eval().value!!)
        assertEquals(EInt(0), parse("0").value!!.desugar().eval().value!!)
    }

    @Test
    fun testSimpleAdditionWithParsing() {
        assertEquals(EInt(5), parse("[add, 5, 0]").value!!.desugar().eval().value!!)
        assertEquals(EInt(5), parse("[add, 0, 5]").value!!.desugar().eval().value!!)
        assertEquals(EInt(10),parse("[add, 7, 3]").value!!.desugar().eval().value!!)
        assertEquals(EInt(7), parse("[add, -3, 10]").value!!.desugar().eval().value!!)
        assertEquals(EInt(6), parse("[add, 10, -4]").value!!.desugar().eval().value!!)
    }

    @Test
    fun testComplexAdditionWithParsing() {
        assertEquals(EInt(6),  parse("[add, 1, [add, 2,3]]").value!!.desugar().eval().value!!)
    }

    @Test
    fun testSimpleMultiplicationWithParsing() {
        assertEquals(EInt(0), parse("[mul, 0, 42]").value!!.desugar().eval().value!!)
        assertEquals(EInt(0), parse("[mul, 42, 0]").value!!.desugar().eval().value!!)
        assertEquals(EInt(42),parse("[mul, 1, 42]").value!!.desugar().eval().value!!)
        assertEquals(EInt(42), parse("[mul, 42, 1]").value!!.desugar().eval().value!!)
        assertEquals(EInt(4), parse("[mul, 2, 2]").value!!.desugar().eval().value!!)
    }
    @Test
    fun testSimpleDivisionWithParsing() {
        assertEquals(EInt(0), parse("[div, 0, 42]").value!!.desugar().eval().value!!)
        assertEquals(true, parse("[div, 42, 0]").value!!.desugar().eval().error is DivisionByZeroError)
        assertEquals(true, parse("[div, 42, [add, -1, 1]]").value!!.desugar().eval().error is DivisionByZeroError)
        assertEquals(EInt(42),parse("[div, 42, 1]").value!!.desugar().eval().value!!)
        assertEquals(EInt(1), parse("[div, 2, 2]").value!!.desugar().eval().value!!)
    }

    @Test
    fun testComplicatedMultiplicationWithParsing() {
        assertEquals(EInt(10), parse("[mul, 1, [mul, 5, 2]]").value!!.desugar().eval().value!!)
        assertEquals(EInt(12), parse("[mul, 2, [add, 5, 1]]").value!!.desugar().eval().value!!)

        assertEquals("10", parse("[mul, 1, [mul, 5, 2]]").value!!.desugar().eval().value!!.unparse())
        assertEquals("12", parse("[mul, 2, [add, 5, 1]]").value!!.desugar().eval().value!!.unparse())
    }

    @Test
    fun testMulAndSubEvaluationWithParsing(){
        val yaml = "[mul, [sub, 22, 11], 44]"
        assertEquals("484", parse(yaml).value!!.desugar().eval().value!!.unparse())
    }

    @Test
    fun testNegationEvaluation(){
        val yaml = "[neg, [mul, [sub, 22, 11], 44]]"
        assertEquals("-484", parse(yaml).value!!.desugar().eval().value!!.unparse())
    }

}