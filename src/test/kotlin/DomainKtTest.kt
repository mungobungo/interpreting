import core.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import parser.parse

internal class DomainKtTest {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    private val emptyEnv = Environment(hashMapOf())
    private val emptyContext = defaultContext

    private fun expressionToExpression(evaluated:Expression, input:Expression){
        val res = input.eval(emptyContext)
        assertTrue(res.success)
        assertNull(res.error)
        assertNotNull(res.value)
        assertEquals(evaluated, res.value!!)
    }
    private fun yamlToExpression(expression: Expression, yaml: String) {
        assertEquals(expression, parse(yaml).value!!.desugar().eval(emptyContext).value!!)
    }

    private fun yamlToYaml(evaluated: String, yaml: String) {
        assertEquals(evaluated, parse(yaml).value!!.desugar().eval(emptyContext).value!!.unparse())
    }

    private fun failsWithError(yaml: String) {

        val res = parse(yaml).value!!.desugar().eval(emptyContext)
        assertEquals(false, res.success)
        assertFalse(res.error == null)
        assertTrue(res.value == null)
        assertTrue(res.error is ICoreError)
    }
    @Test
    fun testSimpleInteger() {
        expressionToExpression(EInt(5), EInt(5))

        expressionToExpression(EInt(-5), EInt(-5))

        expressionToExpression(EInt(0), EInt(0))

    }

    @Test
    fun testSimpleAddition() {
        expressionToExpression(EInt(5), ECall(ESymbol("add"), listOf(EInt(5), EInt(0))))
        expressionToExpression(EInt(5), ECall( ESymbol("add"), listOf(EInt(0), EInt(5))))
        expressionToExpression(EInt(10), ECall(ESymbol("add"), listOf(EInt(7), EInt(3))))
        expressionToExpression(EInt(7), ECall(ESymbol("add"),listOf( EInt(-3), EInt(10))))
        expressionToExpression(EInt(6), ECall(ESymbol("add"),listOf( EInt(10), EInt(-4))))
    }

    @Test
    fun testComplexAddition() {
        val add = ESymbol("add")
        expressionToExpression(EInt(6), ECall(add, listOf(EInt(1), ECall(add,  listOf(EInt(2), EInt(3))))))
    }

    @Test
    fun testSimpleMultiplication() {
        val mul = ESymbol("mul")
        expressionToExpression(EInt(0), ECall(mul,listOf( EInt(0), EInt(42))))
        expressionToExpression(EInt(0), ECall(mul,listOf(EInt(42), EInt(0))))
        expressionToExpression(EInt(42), ECall(mul, listOf(EInt(1), EInt(42))))
        expressionToExpression(EInt(42), ECall(mul,listOf(EInt(42), EInt(1))))
        expressionToExpression(EInt(4), ECall(mul,listOf(EInt(2), EInt(2))))
    }

    @Test
    fun testComplicatedMultiplication() {
        val mul = ESymbol("mul")
        val add = ESymbol("add")
        expressionToExpression(EInt(10), (ECall( mul, listOf((ECall(mul, listOf(EInt(1), EInt(5)))), EInt(2)))))
        expressionToExpression(EInt(12), (ECall( mul, listOf((ECall(add, listOf(EInt(1), EInt(5)))), EInt(2)))))
    }


    @Test
    fun testComplicatedExpressionWithParsing() {
        yamlToExpression(EInt(10), "[mul, [mul, 1, 5], 2]")
        yamlToYaml("10", "[mul, [mul, 1, 5], 2]")
        yamlToYaml("12", "[mul, [add, 1, 5], 2]")
    }

    @Test
    fun testSimpleIntegerWithParsing() {
        yamlToExpression(EInt(5), "5")
        yamlToYaml("5", "5")
        yamlToExpression(EInt(-5), "-5")
        yamlToYaml("-5", "-5")
        yamlToExpression(EInt(0), "0")
        yamlToYaml("0", "0")
    }

    @Test
    fun testSimpleAdditionWithParsing() {
        yamlToYaml("5", "[add, 5, 0]")
        yamlToYaml("5", "[add, 0, 5]")
        yamlToYaml("10", "[add, 7, 3]")
        yamlToYaml("7", "[add, -3, 10]")
        yamlToYaml("6", "[add, 10, -4]")
        yamlToYaml("5", "[iadd, 5, 0]")
        yamlToYaml("5", "[iadd, 0, 5]")
        yamlToYaml("10", "[iadd, 7, 3]")
        yamlToYaml("7", "[iadd, -3, 10]")
        yamlToYaml("6", "[iadd, 10, -4]")
    }

    @Test
    fun testComplexAdditionWithParsing() {
        yamlToYaml("6", "[add, 1, [add, 2, 3]]")
        yamlToYaml("6", "[add, 1, [iadd, 2, 3]]")
        yamlToYaml("6", "[iadd, 1, [add, 2, 3]]")
    }

    @Test
    fun testSimpleMultiplicationWithParsing() {
        yamlToYaml("0", "[mul, 0, 42]")
        yamlToYaml("0", "[mul, 42, 0]")
        yamlToYaml("42", "[mul, 1, 42]")
        yamlToYaml("42", "[mul, 42,1]")
        yamlToYaml("4", "[mul, 2, 2]")
        yamlToYaml("4", "[mul, -2, -2]")
    }

    @Test
    fun testSimpleDivisionWithParsing() {
        yamlToYaml("0", "[div, 0, 42]")
        failsWithError("[div, 42,0]")
        failsWithError("[div, [add, -1, 1],0]")
        yamlToYaml("42", "[div, 42, 1]")
        yamlToYaml("1", "[div, 2,2]")
    }

    @Test
    fun testComplicatedMultiplicationWithParsing() {
        yamlToYaml("10", "[mul, 1, [mul, 5,2]]")
        yamlToYaml("12", "[mul, 2, [add, 5,1]]")
    }

    @Test
    fun testMulAndSubEvaluationWithParsing() {
        yamlToYaml("484", "[mul, [sub, 22,11], 44]")
    }

    @Test
    fun testNegationEvaluation() {
        yamlToYaml("-484", "[neg, [mul, [sub, 22,11], 44]]")
    }

    @Test
    fun testIsIntEvaluation() {
        yamlToYaml("true", "[is_int, 10]")
        yamlToYaml("false", "[is_int, 10.0]")
        yamlToYaml("false", "[is_int, true]")
    }


    @Test
    fun testIsFloatEvaluation() {
        yamlToYaml("true", "[is_float, 10.1]")
        yamlToYaml("false", "[is_float, 10]")
        yamlToYaml("false", "[is_float, true]")
    }


    @Test
    fun testIsBoolEvaluation() {

        yamlToYaml("false", "[is_bool, 10.1]")
        yamlToYaml("false", "[is_bool, 10]")
        yamlToYaml("true", "[is_bool, true]")
    }

    @Test
    fun testBinaryNumericEvaluation(){
        yamlToYaml("3", "[add, 1, 2]")
        yamlToYaml("3", "[iadd, 1, 2]")
        yamlToYaml("3.0", "[fadd, 1,2]")
        yamlToYaml("3.0", "[add, 1.0, 2]")
        yamlToYaml("3.0", "[add, 1, 2.0]")
        yamlToYaml("3.0", "[add, 1.0, 2.0]")
        yamlToYaml("-1", "[sub, 1, 2]")
        yamlToYaml("-1", "[isub, 1, 2]")
        yamlToYaml("-1.0", "[fsub, 1,2]")
        yamlToYaml("-1.0", "[sub, 1.0, 2]")
        yamlToYaml("-1.0", "[sub, 1, 2.0]")
        yamlToYaml("-1.0", "[sub, 1.0, 2.0]")
        yamlToYaml("3", "[mul, 1, 3]")
        yamlToYaml("3", "[imul, 1, 3]")
        yamlToYaml("3.0", "[fmul, 1,3]")
        yamlToYaml("3.0", "[mul, 1.0, 3]")
        yamlToYaml("3.0", "[mul, 1, 3.0]")
        yamlToYaml("3.0", "[mul, 1.0, 3.0]")
        yamlToYaml("3", "[div, 9, 3]")
        failsWithError("[div, 1, 0]")
        failsWithError("[idiv, 1, 0]")
        yamlToYaml("3", "[idiv, 9, 3]")
        yamlToYaml("3.0", "[fdiv, 9,3]")
        yamlToYaml("3.0", "[div, 9.0, 3]")
        yamlToYaml("3.0", "[div, 9, 3.0]")
        yamlToYaml("3.0", "[div, 9.0, 3.0]")
        yamlToYaml("Infinity", "[fdiv, 10.0, 0.0]")
        yamlToYaml("Infinity", "[fdiv, 10, 0.0]")
        yamlToYaml("Infinity", "[fdiv, 10.0, 0]")
        yamlToYaml("Infinity", "[fdiv, 10, 0]")
        yamlToYaml("Infinity", "[div, 10.0, 0.0]")
        yamlToYaml("Infinity", "[div, 10, 0.0]")
        yamlToYaml("Infinity", "[div, 10.0, 0]")
    }
    @Test
   fun testNumericBinaryBoolOperations(){

        yamlToYaml("true","[lt, 1, 2]")
        yamlToYaml("true","[lte, 1, 2]")
        yamlToYaml("false","[gt, 1, 2]")
        yamlToYaml("false","[gte, 1, 2]")
        yamlToYaml("false","[eq, 1, 2]")
        yamlToYaml("true","[neq, 1, 2]")

        yamlToYaml("true","[lt, 1.0, 2]")
        yamlToYaml("true","[lte, 1.0, 2]")
        yamlToYaml("false","[gt, 1.0, 2]")
        yamlToYaml("false","[gte, 1.0, 2]")
        yamlToYaml("false","[eq, 1.0, 2]")
        yamlToYaml("true","[neq, 1.0, 2]")

        yamlToYaml("true","[lt, 1, 2.0]")
        yamlToYaml("true","[lte, 1, 2.0]")
        yamlToYaml("false","[gt, 1, 2.0]")
        yamlToYaml("false","[gte, 1, 2.0]")
        yamlToYaml("false","[eq, 1, 2.0]")
        yamlToYaml("true","[neq, 1, 2.0]")

        yamlToYaml("true","[lt, 1.0, 2.0]")
        yamlToYaml("true","[lte, 1.0, 2.0]")
        yamlToYaml("false","[gt, 1.0, 2.0]")
        yamlToYaml("false","[gte, 1.0, 2.0]")
        yamlToYaml("false","[eq, 1.0, 2.0]")
        yamlToYaml("true","[neq, 1.0, 2.0]")


        yamlToYaml("false","[lt, 1, 1]")
        yamlToYaml("true","[lte, 1, 1]")
        yamlToYaml("false","[gt, 1, 1]")
        yamlToYaml("true","[gte, 1, 1]")
        yamlToYaml("true","[eq, 1, 1]")
        yamlToYaml("false","[neq, 1, 1]")

        yamlToYaml("false","[lt, 1.0, 1]")
        yamlToYaml("true","[lte, 1.0, 1]")
        yamlToYaml("false","[gt, 1.0, 1]")
        yamlToYaml("true","[gte, 1.0, 1]")
        yamlToYaml("true","[eq, 1.0, 1]")
        yamlToYaml("false","[neq, 1.0, 1]")

        yamlToYaml("false","[lt, 1, 1.0]")
        yamlToYaml("true","[lte, 1, 1.0]")
        yamlToYaml("false","[gt, 1, 1.0]")
        yamlToYaml("true","[gte, 1, 1.0]")
        yamlToYaml("true","[eq, 1, 1.0]")
        yamlToYaml("false","[neq, 1, 1.0]")

        yamlToYaml("false","[lt, 1.0, 1.0]")
        yamlToYaml("true","[lte, 1.0, 1.0]")
        yamlToYaml("false","[gt, 1.0, 1.0]")
        yamlToYaml("true","[gte, 1.0, 1.0]")
        yamlToYaml("true","[eq, 1.0, 1.0]")
        yamlToYaml("false","[neq, 1.0, 1.0]")


        yamlToYaml("false","[lt, 2, 1]")
        yamlToYaml("false","[lte, 2, 1]")
        yamlToYaml("true","[gt, 2, 1]")
        yamlToYaml("true","[gte, 2, 1]")
        yamlToYaml("false","[eq, 2, 1]")
        yamlToYaml("true","[neq, 2, 1]")

        yamlToYaml("false","[lt, 2.0, 1]")
        yamlToYaml("false","[lte, 2.0, 1]")
        yamlToYaml("true","[gt, 2.0, 1]")
        yamlToYaml("true","[gte, 2.0, 1]")
        yamlToYaml("false","[eq, 2.0, 1]")
        yamlToYaml("true","[neq, 2.0, 1]")

        yamlToYaml("false","[lt, 2, 1.0]")
        yamlToYaml("false","[lte, 2, 1.0]")
        yamlToYaml("true","[gt, 2, 1.0]")
        yamlToYaml("true","[gte, 2, 1.0]")
        yamlToYaml("false","[eq, 2, 1.0]")
        yamlToYaml("true","[neq, 2, 1.0]")

        yamlToYaml("false","[lt, 2.0, 1.0]")
        yamlToYaml("false","[lte, 2.0, 1.0]")
        yamlToYaml("true","[gt, 2.0, 1.0]")
        yamlToYaml("true","[gte, 2.0, 1.0]")
        yamlToYaml("false","[eq, 2.0, 1.0]")
        yamlToYaml("true","[neq, 2.0, 1.0]")
   }

    @Test
    fun testBinaryBoolOperations(){
        yamlToYaml("true", "[and, true, true]")
        yamlToYaml("false", "[and, true, false]")
        yamlToYaml("false", "[and, false, true]")
        yamlToYaml("false", "[and, false, false]")
        yamlToYaml("true", "[or, true, true]")
        yamlToYaml("true", "[or, true, false]")
        yamlToYaml("true", "[or, false, true]")
        yamlToYaml("false", "[or, false, false]")
        yamlToYaml("false", "[xor, true, true]")
        yamlToYaml("true", "[xor, true, false]")
        yamlToYaml("true", "[xor, false, true]")
        yamlToYaml("false", "[xor, false, false]")
    }

    @Test
    fun testBooleanNot(){
        yamlToYaml("true", "[not, false]")
        yamlToYaml("true", "[not, [not, true]]")

        yamlToYaml("false", "[not, [not, false]]")

    }
}