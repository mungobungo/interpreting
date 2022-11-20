import core.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import parser.parse
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class StrongTypeTest {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    val emptyTypeEnv = TypeEnv(hashMapOf())

    private fun toEx(yaml: String): Expression {
        return parse(yaml).value!!.desugar()
    }

    @Test
    fun testSimpleInt() {
        assertEquals(TInt().toScheme(),  typeOf(toEx("10"), emptyTypeEnv).result)
        assertEquals(TFloat().toScheme(), typeOf(toEx("1.02"), emptyTypeEnv).result)
        assertEquals(TBool().toScheme(), typeOf(toEx("true"),emptyTypeEnv).result)
    }

    @Test
    fun testIntegerBinaryOperation(){

        assertEquals(TInt().toScheme(), typeOf(toEx("[iadd, 4, 5]"),emptyTypeEnv).result)
        assertEquals(TInt().toScheme(), typeOf(toEx("[iadd, 4, [iadd, 9, 100]]"),emptyTypeEnv).result)

        assertEquals(TInt().toScheme(), typeOf(toEx("[iadd, 4, [imul, 9, [idiv, 10, 0]]]"),emptyTypeEnv).result)
    }
    @Test
    fun testFloatBinaryOperation(){

        assertEquals(TFloat().toScheme(), typeOf(toEx("[fadd, 4.0, 5.0]"),emptyTypeEnv).result)
        assertTrue(typeOf(toEx("[fadd, 4, 5.0]"),emptyTypeEnv).result.type is TypeError)
        assertEquals(TFloat().toScheme(), typeOf(toEx("[fadd, 4.1, [fadd, 9.0, 100.0]]"),emptyTypeEnv).result)

        assertEquals(TFloat().toScheme(), typeOf(toEx("[fadd, 4.0, [fmul, 9.0, [fdiv, 10.0, 0.0]]]"),emptyTypeEnv).result)
    }
    @Test
    fun testNumericBinaryOperation(){

        assertEquals(TFloat().toScheme(), typeOf(toEx("[add, 4.0, 5.0]"),emptyTypeEnv).result)
        assertEquals(TFloat().toScheme(), typeOf(toEx("[add, 4.0, 5]"),emptyTypeEnv).result)
        assertEquals(TFloat().toScheme(), typeOf(toEx("[add, 4, 5.0]"),emptyTypeEnv).result)
        assertEquals(TInt().toScheme(), typeOf(toEx("[add, 4, 5]"),emptyTypeEnv).result)
        assertEquals(TFloat().toScheme(), typeOf(toEx("[add, 4.1, [add, 9, 100.0]]"),emptyTypeEnv).result)
        assertEquals(TFloat().toScheme(), typeOf(toEx("[mul, 4.0, [add, 9.0, [idiv, 10, 0]]]"),emptyTypeEnv).result)
    }
    @Test
    fun testBinaryBoolOperation(){

        assertEquals(TBool().toScheme(), typeOf(toEx("[and, True, False]"), emptyTypeEnv).result)
        assertEquals(TBool().toScheme(), typeOf(toEx("[and, True, [or, False, True]]"),emptyTypeEnv).result)
        assertEquals(TBool().toScheme(), typeOf(toEx("[or, True, [and, True, False]]"),emptyTypeEnv).result)
    }
    @Test
    fun testSetVar(){

        assertEquals(TBool().toScheme(), typeOf(toEx("[setvar, x , True]"), emptyTypeEnv).result)
        assertEquals(TInt().toScheme(), typeOf(toEx("[setvar, x, 12]"),emptyTypeEnv).result)
    }
    @Test
    fun substitution0Test(){

        val sub= Substitution(hashMapOf("a" to TInt(), "b" to TFloat()))
        // after substitution  f:: int -> int -> int
        assertEquals(TInt(), applySubstitution(sub, TInt()))
        assertEquals(TBool(), applySubstitution(sub, TBool()))
        assertEquals(TFloat(), applySubstitution(sub, TFloat()))
        assertEquals(TInt(), applySubstitution(sub, TVar("a")))
        assertEquals(TFloat(), applySubstitution(sub, TVar("b")))
        assertEquals(TVar("x"), applySubstitution(sub, TVar("x")))
    }
    @Test
    fun substitution1Test(){
        // f = [lambda, [a, b], [iadd, a, b]]
        val sub= Substitution(hashMapOf("a" to TInt(), "b" to TInt()))
        // f:: a -> b -> int
        val f = TFunc("fun", listOf(TVar("a"), TVar("b")), TInt())
        val subRes = applySubstitution(sub, f)
        // after substitution  f:: int -> int -> int
        assertEquals(TFunc("fun", listOf(TInt(), TInt()), TInt()), subRes)
    }
    @Test
    fun substitution2Test(){
        // f = [lambda, [a, b], [iadd, a, 8]]
        val sub= Substitution(hashMapOf("a" to TInt()))
       // f:: a -> b -> int
        val f = TFunc("fun", listOf(TVar("a"), TVar("b")), TInt())
        val subRes = applySubstitution(sub, f)
        // after substitution  f:: int -> b -> int
        assertEquals(TFunc("fun", listOf(TInt(), TVar("b")), TInt()), subRes)
    }
    @Test
    fun substitutionSchemeTest(){
        val sub= Substitution(hashMapOf("a" to TInt()))
        val t = TScheme(listOf("a"), TVar("a"))// forall a : a
        // bound variables should not be substituted
        val subScheme = applySchemeSubstitution(sub, t)
        assertEquals( TScheme(listOf("a"), TVar("a")), subScheme)// forall a : a
    }

    @Test
    fun substitution2SchemeTest(){
        val f = TFunc("fun", listOf(TVar("a")), TVar("b"))
        val t = TScheme(listOf("a"), f)// forall a  : a -> b
        // bound variables should not be substituted
        val sub= Substitution(hashMapOf("a" to TInt(), "b" to TInt()))
        val subScheme = applySchemeSubstitution(sub, t)
        val subF = TFunc("fun", listOf(TVar("a")),TInt()) //  a-> int
        assertEquals( TScheme(listOf("a"), subF), subScheme)// forall a : a : a -> int
    }
    @Test
    fun substitutionTypeEnvTest(){
        val tEnv = TypeEnv(hashMapOf())

        val f = TFunc("fun", listOf(TVar("a")), TVar("b"))
        val t = TScheme(listOf("a"), f)// forall a  : a -> b
        tEnv.env["x"] = t

        val sub= Substitution(hashMapOf("a" to TInt(), "b" to TInt()))
        val subEnv = applyEnvSubstitution(sub, tEnv)
        val subF = TFunc("fun", listOf(TVar("a")),TInt()) //  a-> int
       assertEquals( TScheme(listOf("a"), subF), subEnv.env["x"])

    }

    @Test
    fun composeSubstitutionTest(){

        val sub1= Substitution(hashMapOf("a" to TInt())) // a:: int
        val sub2 = Substitution(hashMapOf("b" to TFloat(), // b:; float
            "x" to TFunc("fun", listOf(TVar("a")), TInt()))) // x:: a -> int
        // f:: a -> x -> b -> z -> int
        val f = TFunc("fun", listOf(TVar("a"), TVar("x"), TVar("b"), TVar("z")), TInt())
        val sub3 = composeSub(sub1, sub2)
        assertTrue(sub3.subs["a"] is TInt)
        assertTrue(sub3.subs["b"] is TFloat)
        assertEquals(sub3.subs["x"], TFunc("fun", listOf(TInt()), TInt())) // x :: int -> int
        val subRes = applySubstitution(sub3, f)
        // after substitution  f:: int -> (int-> int) -> float -> z -> int
        val res = TFunc("fun", listOf(TInt(), TFunc("fun", listOf(TInt()), TInt()), TFloat(), TVar("z")), TInt())
        assertEquals(res,  subRes)
    }
    @Test
    fun simpleUnifyTest(){
       assertEquals(emptySub, unify(TInt(), TInt()).sub)
        assertEquals(emptySub, unify(TFloat(), TFloat()).sub)
        assertEquals(emptySub, unify(TBool(), TBool()).sub)
        assertEquals(emptySub, unify(TVar("x"), TVar("x")).sub)

        assertEquals(Substitution(hashMapOf("x" to TBool())), unify(TVar("x"), TBool()).sub)
        assertEquals(Substitution(hashMapOf("x" to TBool())), unify(TBool(), TVar("x")).sub)

        assertEquals(Substitution(hashMapOf("x" to TVar("y"))), unify(TVar("x"), TVar("y")).sub)
        assertEquals(Substitution(hashMapOf("y" to TVar("x"))), unify(TVar("y"), TVar("x")).sub)
    }

    @Test
    fun brokenUnificationTest(){
        val u1 = unify(TInt(), TBool())
        assertFalse(u1.success)
        assertEquals("Unification error: Cannot unify TInt(type=int) and TBool(type=bool)", u1.toString())
    }
    @Test
    fun brokenUnificationFunctionArgumentMismatchTest(){
        val f1 = TFunc("fun", listOf(TInt(), TInt()), TInt())
        val f2 = TFunc("fun", listOf(TInt()), TInt())
        assertEquals("Func argument count mismatch,\n" +
                "2 arguments for TFunc(type=fun, params=[TInt(type=int), TInt(type=int)], result=TInt(type=int))\n" +
                "1 arguments for TFunc(type=fun, params=[TInt(type=int)], result=TInt(type=int))\n" +
                " Cannot unify TFunc(type=fun, params=[TInt(type=int), TInt(type=int)], result=TInt(type=int)) and TFunc(type=fun, params=[TInt(type=int)], result=TInt(type=int))", unify(f1, f2).toString())
    }

    @Test
    fun functionUnificationTest(){

        val f1 = TFunc("fun", listOf(TVar("x")), TInt())
        val f2 = TFunc("fun", listOf(TInt()), TInt())
        val res = unify(f1, f2)
        assertTrue(res.success)
        assertEquals(Substitution(hashMapOf("x" to TInt())), res.sub)

        val res2 = unify(f2, f1)

        assertTrue(res2.success)
        assertEquals(Substitution(hashMapOf("x" to TInt())), res2.sub)
    }
}