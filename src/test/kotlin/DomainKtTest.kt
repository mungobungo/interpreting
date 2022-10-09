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
        assertEquals(EInt(5), EInt(5).eval(emptyEnv))
        assertEquals(EInt(-5),(EInt(-5)).eval(emptyEnv))
        assertEquals(EInt(0), EInt(0).eval(emptyEnv))
    }

    @Test
    fun testSimpleAddition() {
        assertEquals(EInt(5), EAdd(EInt(5), EInt(0)).eval(emptyEnv))
        assertEquals(EInt(5), (EAdd(EInt(0), EInt(5))).eval(emptyEnv))
        assertEquals(EInt(10),(EAdd(EInt(7), EInt(3))).eval(emptyEnv))
        assertEquals(EInt(7), (EAdd(EInt(-3), EInt(10))).eval(emptyEnv))
        assertEquals(EInt(6), (EAdd(EInt(10), EInt(-4))).eval(emptyEnv))
    }

    @Test
    fun testComplexAddition() {
        assertEquals(EInt(6), (EAdd(EInt(1), EAdd(EInt(2), EInt(3)))).eval(emptyEnv))
    }

    @Test
    fun testSimpleMultiplication() {
        assertEquals(EInt(0), (EMul(EInt(0), EInt(42))).eval(emptyEnv))
        assertEquals(EInt(0), (EMul(EInt(42), EInt(0))).eval(emptyEnv))
        assertEquals(EInt(42),(EMul(EInt(1), EInt(42))).eval(emptyEnv))
        assertEquals(EInt(42), (EMul(EInt(42), EInt(1))).eval(emptyEnv))
        assertEquals(EInt(4), (EMul(EInt(2), EInt(2))).eval(emptyEnv))
    }

    @Test
    fun testComplicatedMultiplication() {
        assertEquals(EInt(10), (EMul(EMul(EInt(1), EInt(5)), EInt(2))).eval(emptyEnv))
        assertEquals(EInt(12), (EMul(EAdd(EInt(1), EInt(5)), EInt(2))).eval(emptyEnv))
    }
    @Test
    fun testComplicatedExpressionWithParsing() {
        assertEquals(EInt(10), (EMul(EMul(EInt(1), EInt(5)), EInt(2))).eval(emptyEnv))
        assertEquals(EInt(10), parse("[mul, [mul, 1, 5], 2]").desugar().eval(emptyEnv))
        assertEquals(EInt(12), (EMul(EAdd(EInt(1), EInt(5)), EInt(2))).eval(emptyEnv))
        assertEquals(EInt(12), parse("[mul, [add, 1, 5], 2]").desugar().eval(emptyEnv))

    }

    @Test
    fun testSimpleIntegerWithParsing() {
        assertEquals(EInt(5), parse("5").desugar().eval(emptyEnv))
        assertEquals(EInt(-5),parse("-5").desugar().eval(emptyEnv))
        assertEquals(EInt(0), parse("0").desugar().eval(emptyEnv))
    }

    @Test
    fun testSimpleAdditionWithParsing() {
        assertEquals(EInt(5), parse("[add, 5, 0]").desugar().eval(emptyEnv))
        assertEquals(EInt(5), parse("[add, 0, 5]").desugar().eval(emptyEnv))
        assertEquals(EInt(10),parse("[add, 7, 3]").desugar().eval(emptyEnv))
        assertEquals(EInt(7), parse("[add, -3, 10]").desugar().eval(emptyEnv))
        assertEquals(EInt(6), parse("[add, 10, -4]").desugar().eval(emptyEnv))
    }

    @Test
    fun testComplexAdditionWithParsing() {
        assertEquals(EInt(6),  parse("[add, 1, [add, 2,3]]").desugar().eval(emptyEnv))
    }

    @Test
    fun testSimpleMultiplicationWithParsing() {
        assertEquals(EInt(0), parse("[mul, 0, 42]").desugar().eval(emptyEnv))
        assertEquals(EInt(0), parse("[mul, 42, 0]").desugar().eval(emptyEnv))
        assertEquals(EInt(42),parse("[mul, 1, 42]").desugar().eval(emptyEnv))
        assertEquals(EInt(42), parse("[mul, 42, 1]").desugar().eval(emptyEnv))
        assertEquals(EInt(4), parse("[mul, 2, 2]").desugar().eval(emptyEnv))
    }

    @Test
    fun testComplicatedMultiplicationWithParsing() {
        assertEquals(EInt(10), parse("[mul, 1, [mul, 5, 2]]").desugar().eval(emptyEnv))
        assertEquals(EInt(12), parse("[mul, 2, [add, 5, 1]]").desugar().eval(emptyEnv))

        assertEquals("10", parse("[mul, 1, [mul, 5, 2]]").desugar().eval(emptyEnv).unparse())
        assertEquals("12", parse("[mul, 2, [add, 5, 1]]").desugar().eval(emptyEnv).unparse())
    }

    @Test
    fun testMulAndSubEvaluationWithParsing(){
        val yaml = "[mul, [sub, 22, 11], 44]"
        assertEquals("484", parse(yaml).desugar().eval(emptyEnv).unparse())
    }

    @Test
    fun testNegationEvaluation(){
        val yaml = "[neg, [mul, [sub, 22, 11], 44]]"
        assertEquals("-484", parse(yaml).desugar().eval(emptyEnv).unparse())
    }

    @Test
    fun testSubstitution(){
        val env1 = Environment(hashMapOf(Pair(ESymbol("x"), EInt(5))))
        assertEquals(EInt(5), ESymbol("x").substitute(ESymbol("x"), env1))
        assertEquals(ESymbol("y"), ESymbol("y").substitute(ESymbol("x"), env1))

        assertEquals("[add, 5, 5]",
            parse("[add, x, x]").desugar()
                .substitute(ESymbol("x"), env1).unparse())

        assertEquals("[mul, 5, 5]",
            parse("[mul, x, x]").desugar()
                .substitute(ESymbol("x"), env1).unparse())

        assertEquals("[add, [mul, 5, 5], 5]",
            parse("[add, [mul, x, x], x]").desugar()
                .substitute(ESymbol("x"), env1).unparse())


        val env2 = Environment(hashMapOf(Pair(ESymbol("x"), EInt(5)),
            Pair(ESymbol("y"), EInt(7))))

        assertEquals("[add, [mul, 5, y], 5]",
            parse("[add, [mul, x, y], x]").desugar()
                .substitute(ESymbol("x"), env2).unparse())


        assertEquals("[add, [mul, 5, 7], 5]",
            parse("[add, [mul, x, y], x]").desugar()
                .substitute(ESymbol("x"), env2)
                .substitute(ESymbol("y"), env2)
                .unparse())

        val complexEnv = Environment(hashMapOf(Pair(ESymbol("x"),
            parse("[mul, 11, [add, [add, 3, 4], 11]]").desugar())))

        assertEquals("[add, 198, 198]",
            parse("[add, x, x]").desugar()
                .substitute(ESymbol("x"), complexEnv)
                .unparse()
            )
    }
    @Test
    fun testComplicatedExpression(){
        val complexEnv = Environment(hashMapOf(Pair(ESymbol("x"),
            parse("[add, 10, 33]").desugar())))

        assertEquals("[add, 43, 43]",
        parse("[add, x, x]").desugar()
            .substitute(ESymbol("x"), complexEnv)
            .unparse())

        assertEquals("86",
            parse("[add, x, x]").desugar()
                .substitute(ESymbol("x"), complexEnv)
                .eval(Environment(hashMapOf()))
                .unparse())


        val expression=parse("[add, x, x]").desugar()

        assertEquals("86",
                expression
                .eval(complexEnv)
                .unparse())

        val anotherEnv = Environment(hashMapOf(Pair(ESymbol("x"),
            parse("[add, 20, 1]").desugar())))

        assertEquals("42",
                expression
                .eval(anotherEnv)
                .unparse())

    }
    @Test
    fun testingSameExpressionDifferentEnvironment(){

        val expression=parse("[add, x, x]").desugar()

        val firstEnv = Environment(hashMapOf(Pair(ESymbol("x"),
            parse("[add, 10, 33]").desugar())))

        assertEquals("86",
            expression
            .eval(firstEnv)
            .unparse())

        val secondEnv = Environment(hashMapOf(Pair(ESymbol("x"),
            parse("[add, 20, 11]").desugar())))

        assertEquals("62",
                expression
                .eval(secondEnv)
                .unparse())
    }
    @Test
    fun testingExactlySameExpressionDifferentEnvironment(){
        val firstEnv = Environment(hashMapOf(Pair(ESymbol("x"),
            parse("[add, 10, 33]").desugar())))

        val expression=parse("[add, x, x]").desugar()

        assertEquals("86",
            expression
                .eval(firstEnv)
                .unparse())

        val secondEnv = Environment(hashMapOf(Pair(ESymbol("x"),
            parse("[add, 20, 11]").desugar())))
        assertEquals("62",
            expression
                .eval(secondEnv)
                .unparse())

    }
}