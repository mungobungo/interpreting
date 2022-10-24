import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import parser.parse
import sugared.*
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class ParserKtTest {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun testSimpleInt() {
        assertEquals(SugarInt(42), parse("42").value!!)
        assertEquals(SugarInt(-42), parse("-42").value!!)
    }

    @Test
    fun testSimpleFloat() {
        assertEquals(SugarFloat(42.0), parse("42.0").value!!)
        assertEquals(SugarFloat(42.1), parse("42.1").value!!)
        assertEquals(SugarFloat(-42.0), parse("-42.0").value!!)
        assertEquals(SugarFloat(-42.1), parse("-42.1").value!!)
    }


    @Test
    fun testSimpleBool() {
        assertEquals(SugarBool(true), parse("True").value!!)
        assertEquals(SugarBool(true), parse("true").value!!)
        assertFalse(parse("tRuE").success)
        assertEquals(SugarBool(false), parse("False").value!!)
        assertEquals(SugarBool(false), parse("false").value!!)
        assertFalse(parse("fAlSe").success)
    }

    @Test
    fun testSimpleAdd() {
        assertEquals(SugarAdd(SugarInt(3), SugarInt(5)), parse("[add, 3, 5]").value!!)
        assertEquals(SugarIAdd(SugarInt(3), SugarInt(5)), parse("[iadd, 3, 5]").value!!)
    }

    @Test
    fun testComplicatedAdd() {
        assertEquals(
            SugarAdd(SugarAdd(SugarInt(3), SugarInt(5)), SugarInt(42)),
            parse("[add, [add, 3,5], 42]").value!!
        )
    }

    @Test
    fun testSimpleMultiplication() {
        assertEquals(SugarMul(SugarInt(11), SugarInt(33)), parse("[mul, 11, 33]").value!!)
    }

    @Test
    fun testComplicatedMultiplication() {
        val data =
            parse("[mul, [mul, 3,5], 42]")
        data.toString()
        assertEquals(
            SugarMul(SugarMul(SugarInt(3), SugarInt(5)), SugarInt(42)),
            parse("[mul, [mul, 3,5], 42]").value!!
        )
    }

    @Test
    fun testMulAndAdd() {
        assertEquals(
            SugarMul(SugarAdd(SugarInt(22), SugarInt(11)), SugarInt(44)),
            parse("[mul, [add, 22, 11], 44]").value!!
        )
    }

    @Test
    fun testSimpleIntUnparse() {
        assertEquals("42", SugarInt(42).desugar().unparse())
        assertEquals("42", parse("42").value!!.desugar().unparse())
    }

    @Test
    fun testSimpleAddUnparse() {
        val yaml = "[add, 3, 5]"
        assertEquals(yaml, SugarAdd(SugarInt(3), SugarInt(5)).desugar().unparse())
        assertEquals(yaml, parse(yaml).value!!.desugar().unparse())
    }

    @Test
    fun testComplicatedAddUnparse() {
        val yaml = "[iadd, [add, 3, 5], 42]"
        assertEquals(
            yaml,
            SugarIAdd(SugarAdd(SugarInt(3), SugarInt(5)), SugarInt(42)).desugar().unparse()
        )
        assertEquals(yaml, parse(yaml).value!!.desugar().unparse())
    }

    @Test
    fun testSimpleMultiplicationUnparse() {
        val yaml = "[mul, 11, 33]"

        assertEquals(yaml, SugarMul(SugarInt(11), SugarInt(33)).desugar().unparse())
        assertEquals(yaml, parse(yaml).value!!.desugar().unparse())
    }

    @Test
    fun testComplicatedMultiplicationUnparse() {
        val yaml = "[mul, [mul, 3, 5], 42]"
        assertEquals(
            yaml,
            SugarMul(SugarMul(SugarInt(3), SugarInt(5)), SugarInt(42)).desugar().unparse()
        )
        assertEquals(yaml, parse(yaml).value!!.desugar().unparse())
    }

    @Test
    fun testMulAndAddUnparse() {
        val yaml = "[mul, [add, 22, 11], 44]"
        assertEquals(
            yaml,
            SugarMul(SugarAdd(SugarInt(22), SugarInt(11)), SugarInt(44)).desugar().unparse()
        )
        assertEquals(yaml, parse(yaml).value!!.desugar().unparse())
    }

    @Test
    fun testMulAndSubUnparse() {

        val yaml = "[mul, [sub, 22, 11], 44]"
        val desugared = "[mul, [sub, 22, 11], 44]"
        assertEquals(
            desugared,
            SugarMul(SugarSub(SugarInt(22), SugarInt(11)), SugarInt(44)).desugar().unparse()
        )
        assertEquals(desugared, parse(yaml).value!!.desugar().unparse())
    }

    @Test
    fun testNegUnparse() {
        val yaml = "[neg, [mul, 2, 10]]"
        val g = parse((yaml))
        val desugared = "[mul, -1, [mul, 2, 10]]"
        assertEquals(desugared, SugarNeg(SugarMul(SugarInt(2), SugarInt(10))).desugar().unparse())
        assertEquals(desugared, parse(yaml).value!!.desugar().unparse())
    }

    @Test
    fun testIsIntUnparse() {
        val yaml = "[is_int, [imul, 1, 20]]"
        val desugared = "[is_int, [imul, 1, 20]]"
        assertEquals(desugared, SugarIsInt(SugarIMul(SugarInt(1), SugarInt(20))).desugar().unparse())

        assertEquals(desugared, parse(yaml).value!!.desugar().unparse())
    }

    @Test
    fun testIsFloatUnparse() {
        val yaml = "[is_float, [imul, 1, 20]]"
        val desugared = "[is_float, [imul, 1, 20]]"
        assertEquals(desugared, SugarIsFloat(SugarIMul(SugarInt(1), SugarInt(20))).desugar().unparse())

        assertEquals(desugared, parse(yaml).value!!.desugar().unparse())
    }

    @Test
    fun testIsBoolUnparse() {
        val yaml = "[is_bool, [is_int, 1]]"
        val desugared = "[is_bool, [is_int, 1]]"
        assertEquals(desugared, SugarIsBool(SugarIsInt(SugarInt(1))).desugar().unparse())

        assertEquals(desugared, parse(yaml).value!!.desugar().unparse())
    }
    @Test
    fun testFloatAddUnparse(){
        val yaml = "[fadd, 2.0, 4.0]"
        assertEquals(yaml, parse(yaml).value!!.desugar().unparse())
    }

    @Test
    fun testFloatMulUnparse(){
        val yaml = "[fmul, 2.0, 4.0]"
        assertEquals(yaml, parse(yaml).value!!.desugar().unparse())
    }

    @Test
    fun testFloatSubUnparse(){
        val yaml = "[fsub, 2.0, 4.0]"
        assertEquals(yaml, parse(yaml).value!!.desugar().unparse())
    }

    @Test
    fun testFloatDivUnparse(){
        val yaml = "[fdiv, 2.0, 4.0]"
        assertEquals(yaml, parse(yaml).value!!.desugar().unparse())
        testConsistency("[fdiv, 2.0, 4.0]")
    }

private fun testConsistency(yaml:String){
    val parsed = parse(yaml)
    assertTrue { parsed.success }
    assertNotNull(parsed.value)
    assertNull(parsed.error)
    assertEquals(yaml, parsed.value!!.desugar().unparse())
}
    @Test
    fun testNumericBoolOperations(){
        testConsistency("[lt, 1, 2]")
        testConsistency("[lte, 1, 2]")
        testConsistency("[gt, 1, 2]")
        testConsistency("[gte, 1, 2]")
        testConsistency("[eq, 1, 2]")
        testConsistency("[neq, 1, 2]")

        testConsistency("[lt, 1.0, 2]")
        testConsistency("[lte, 1.0, 2]")
        testConsistency("[gt, 1.0, 2]")
        testConsistency("[gte, 1.0, 2]")
        testConsistency("[eq, 1.0, 2]")
        testConsistency("[neq, 1.0, 2]")

        testConsistency("[lt, 1, 2.0]")
        testConsistency("[lte, 1, 2.0]")
        testConsistency("[gt, 1, 2.0]")
        testConsistency("[gte, 1, 2.0]")
        testConsistency("[eq, 1, 2.0]")
        testConsistency("[neq, 1, 2.0]")

        testConsistency("[lt, 1.0, 2.0]")
        testConsistency("[lte, 1.0, 2.0]")
        testConsistency("[gt, 1.0, 2.0]")
        testConsistency("[gte, 1.0, 2.0]")
        testConsistency("[eq, 1.0, 2.0]")
        testConsistency("[neq, 1.0, 2.0]")
    }
}