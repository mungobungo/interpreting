package core

import java.util.Scanner

interface StrongType{
    val type:String
    fun toScheme():TScheme
}

data class TypeCheckResult(val success:Boolean, val result:TScheme, val te: TypeEnv)
class Helper{
    companion object {
        var typeCount = 0

    fun newTypeVar(): TypeVariable{
        typeCount += 1
        return TypeVariable(type = "t$typeCount", "t$typeCount")
    }
    }
}
data class TypeEnv(val env:HashMap<String, TScheme>, val parent:TypeEnv?=null){
    fun expand(): TypeEnv{
        return TypeEnv(hashMapOf(), this)
    }
    fun isLocallyDefined(symbol:String):Pair<Boolean, TScheme>{
        val res = env[symbol]

        return if(res !=null){
            Pair(true, res)
        }else{
            Pair(false,TypeError("binding_not_found",ESymbol(symbol), "symbol `$symbol` not found in typeEnv $this").toScheme())
        }
    }
    fun isGloballyDefined(symbol: String):Pair<Boolean, TScheme>{
        var current: TypeEnv? = this
        val res = Pair(false,TypeError("binding_not_found",ESymbol(symbol), "symbol `$symbol` not found in typeEnv $this").toScheme())
        while(current!=null){
            val value = current!!.isLocallyDefined(symbol)
            if(value.first) {
                return value
            }
            current = current.parent
        }
        return res
    }
}

data class TInt(override val type:String = "int"): StrongType {
    override fun toScheme(): TScheme {
       return TScheme(listOf(), this)
    }
}

data class  TFloat(override val type:String="float"): StrongType {
    override fun toScheme(): TScheme {
       return TScheme(listOf(), this)
    }
}

data class  TBool(override val type:String="bool"): StrongType {
    override fun toScheme(): TScheme {
       return TScheme(listOf(), this)
    }
}

data class TypeError(override val type:String ="type_error", val input: Expression,  val error:String):StrongType {
    override fun toScheme(): TScheme {
       return TScheme(listOf(), this)
    }
}

data class TypeVariable(override val type:String= "tvar", val name:String) :StrongType {
    override fun toScheme(): TScheme {
       return TScheme(listOf(name), this)
    }
}
// is_int :: a -> Bool

data class TScheme(val typeVars:List<String>, val type:StrongType) // for all, a, b :  a -> b -> a
// Scheme without typeVars is considered to be just a type // forall () : Int == Int
data class TFunc(override val type:String ="fun",  val params:List<StrongType>, val result:StrongType):StrongType {
    override fun toScheme(): TScheme {
       return TScheme(listOf(), this)
    }
}

// lambda x, y : x  :: TScheme([a, b], TFunc(params:[a, b], result:a)


// lambda x : [call, x, 1]  ::  TScheme([b], TFunc(params:Func(Int->b), result : b)
// we don't know type of x. But we know it is a function. and this function accepts int. and it returns yet unknown type b.


// [let, x, 5, x] :: int
// [let, id, [lambda, [x], x], [call, id, true]] :: Bool
// [let, id, [lambda, [x], x]] :: TScheme([a], TFunc(params:[a], result : a)) // forall a. :: a->a
fun typeOf(e: Expression, te: TypeEnv) : TypeCheckResult{
    when(e){
        is EInt -> return  TypeCheckResult(true, TInt().toScheme(), te)
        is EFloat -> return TypeCheckResult(true, TFloat().toScheme(), te)
        is EBool -> return  TypeCheckResult(true, TBool().toScheme(), te)
        is EBinaryIntegerOp -> return binaryIntegerOpType(e, te)
        is EBinaryFloatOp -> return binaryFloatOpType(e, te)
        is EBinaryNumericOp -> return binaryNumericOpType(e, te)
        is EBinaryBoolOp -> return binaryBoolOpType(e, te)
        is ESymbol -> return symbolType(e, te)
        is ESetVar -> return setVarType(e, te)
        is EDo -> return doType(e, te)
        is ELambdaDefinition -> return lambdaType(e, te)
    }
    return TypeCheckResult(false,  TypeError("type_error", e, "unsupported expression ${e.unparse()}").toScheme(),te)
}

data class Substitution(val subs:HashMap<TypeVariable, StrongType>)

fun lambdaType(e: ELambdaDefinition, te: TypeEnv): TypeCheckResult {

   val localTypeEnv = te.expand()

    val argNames = e.argumentNames
    val argTypes = mutableListOf<TypeVariable>()
    for(argName in argNames){
        val argType= Helper.newTypeVar()
        localTypeEnv.env[argName] = argType.toScheme()
        argTypes.add(argType)
    }
// [lambda, [a,b], 3]] :: forall a,b   Func([a,b] , int)

    // what about
    // [lambda, [a,b], [iadd, a, 4]] ??
    // a can only be integer, we need to capture this info somehow
    // To deal with it , we need to have a way of capturing this information
    // during the type inference
    // original type :: a -> b -> int
    // updated type :: int -> b -> int
    // the only way to satisfy [iadd, a, 4] is for a to have type int.

    for(exp in e.body){
        val type = typeOf(exp, localTypeEnv)
        if(!type.success){
            return type
        }
    }
    val resType = typeOf(e.body.last(), localTypeEnv).result
    return TypeCheckResult(true, TScheme(argTypes.map { it.name }, TFunc(params = argTypes, result = resType.type)), te)

}

fun doType(e: EDo, te: TypeEnv): TypeCheckResult {
   val localTypeEnv = te.expand()
    for(exp in e.expressions){
        val type = typeOf(exp, localTypeEnv)
        if(!type.success){
            return type
        }
    }
    return TypeCheckResult(true, typeOf(e.expressions.last(), localTypeEnv).result, te)
}

fun setVarType(e: ESetVar, te: TypeEnv): TypeCheckResult {
   val t = typeOf(e.variableValue, te)
    if(!t.success){
        return t
    }
    te.env[e.name] = t.result
    return TypeCheckResult(true, t.result, te)
}


fun symbolType(e: ESymbol, te:TypeEnv): TypeCheckResult {
    val res = te.isGloballyDefined(e.name)
    if(res.first){
        return TypeCheckResult(true, res.second, te)
    }
    return TypeCheckResult(false, res.second, te)
}

fun binaryBoolOpType(e: EBinaryBoolOp, te:TypeEnv): TypeCheckResult {
   val left = typeOf(e.left, te)
    if(!left.success){
        return left
    }
   val right = typeOf(e.right,te)
    if(!right.success){
        return right
    }
   if(left.result.type is TBool && right.result.type is TBool){
       return TypeCheckResult(true, TBool().toScheme(), te)
   }
    return TypeCheckResult(false, TypeError("binary_bool_type_error", e, "left and right arguments of `${e.operationName}` are expected to be booleans, but got $left and $right in ${e.unparse()}").toScheme(),te)
}

fun binaryNumericOpType(e: EBinaryNumericOp, te:TypeEnv): TypeCheckResult {
    val left = typeOf(e.left, te)
    if(!left.success){
        return left
    }
    val right = typeOf(e.right,te)
    if(!right.success){
        return right
    }
    if(left.result.type is TInt && right.result.type is TInt){
        return TypeCheckResult(true, left.result, te)
    }
    if(left.result.type is TFloat && right.result.type is TInt){
        return TypeCheckResult(true, left.result, te)
    }
    if(left.result.type is TInt && right.result.type is TFloat){
        return TypeCheckResult(true, right.result, te)
    }
    if(left.result.type is TFloat && right.result.type is TFloat){
        return TypeCheckResult(true, left.result, te)
    }

    return TypeCheckResult(false,  TypeError("binary_numeric_type_error", e, "left and right arguments of `${e.operationName}` are expected to be numerics, but got $left and $right in ${e.unparse()}").toScheme(), te)
}

fun binaryIntegerOpType(e: EBinaryIntegerOp, te:TypeEnv): TypeCheckResult {
    val left = typeOf(e.left, te)
    if(!left.success){
        return left
    }
    val right = typeOf(e.right,te)
    if(!right.success){
        return right
    }
    if(left.result.type is TInt && right.result.type is TInt){
        return TypeCheckResult(true, left.result, te)
    }
    return TypeCheckResult(false, TypeError("binary_integer_type_error", e, "left and right arguments of `${e.operationName}` are expected to be integers, but got $left and $right in ${e.unparse()}").toScheme(), te)
}
fun binaryFloatOpType(e: EBinaryFloatOp, te:TypeEnv): TypeCheckResult {
    val left = typeOf(e.left, te)
    if(!left.success){
        return left
    }
    val right = typeOf(e.right,te)
    if(!right.success){
        return right
    }
    if(left.result.type is TFloat && right.result.type is TFloat){
        return TypeCheckResult(true, left.result, te)
    }
    return TypeCheckResult(false,  TypeError("binary_float_type_error", e, "left and right arguments of `${e.operationName}` are expected to be floats, but got $left and $right in ${e.unparse()}").toScheme(), te)
}
