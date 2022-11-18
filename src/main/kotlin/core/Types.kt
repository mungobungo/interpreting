package core

interface StrongType{
    val type:String
}

data class TypeCheckResult(val success:Boolean, val result:StrongType, val te: TypeEnv)

data class TypeEnv(val env:HashMap<String, StrongType>, val parent:TypeEnv?=null){
    fun expand(): TypeEnv{
        return TypeEnv(hashMapOf(), this)
    }
    fun isLocallyDefined(symbol:String):Pair<Boolean, StrongType>{
        val res = env[symbol]

        return if(res !=null){
            Pair(true, res)
        }else{
            Pair(false,TypeError("binding_not_found",ESymbol(symbol), "symbol `$symbol` not found in typeEnv $this"))
        }
    }
    fun isGloballyDefined(symbol: String):Pair<Boolean, StrongType>{
        var current: TypeEnv? = this
        val res = Pair(false,TypeError("binding_not_found",ESymbol(symbol), "symbol `$symbol` not found in typeEnv $this"))
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

data class TInt(override val type:String = "int"): StrongType

data class  TFloat(override val type:String="float"): StrongType

data class  TBool(override val type:String="bool"): StrongType

data class TypeError(override val type:String ="type_error", val input: Expression,  val error:String):StrongType

data class TFunc(override val type:String ="fun",  val params:List<StrongType>, val result:List<StrongType>):StrongType

fun typeOf(e: Expression, te: TypeEnv) : TypeCheckResult{
    when(e){
        is EInt -> return  TypeCheckResult(true, TInt(), te)
        is EFloat -> return TypeCheckResult(true, TFloat(), te)
        is EBool -> return  TypeCheckResult(true, TBool(), te)
        is EBinaryIntegerOp -> return binaryIntegerOpType(e, te)
        is EBinaryFloatOp -> return binaryFloatOpType(e, te)
        is EBinaryNumericOp -> return binaryNumericOpType(e, te)
        is EBinaryBoolOp -> return binaryBoolOpType(e, te)
        is ESymbol -> return symbolType(e, te)
        is ESetVar -> return setVarType(e, te)
        is EDo -> return doType(e, te)
        is ELambdaDefinition -> return lambdaType(e, te)
    }
    return TypeCheckResult(false,  TypeError("type_error", e, "unsupported expression ${e.unparse()}"),te)
}

fun lambdaType(e: ELambdaDefinition, te: TypeEnv): TypeCheckResult {

    return TypeCheckResult(false,  TypeError("type_error", e, "unsupported expression ${e.unparse()}"),te)
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
   if(left.result is TBool && right.result is TBool){
       return TypeCheckResult(true, TBool(), te)
   }
    return TypeCheckResult(false, TypeError("binary_bool_type_error", e, "left and right arguments of `${e.operationName}` are expected to be booleans, but got $left and $right in ${e.unparse()}"),te)
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
    if(left.result is TInt && right.result is TInt){
        return TypeCheckResult(true, TInt(), te)
    }
    if(left.result is TFloat && right.result is TInt){
        return TypeCheckResult(true, TFloat(), te)
    }
    if(left.result is TInt && right.result is TFloat){
        return TypeCheckResult(true, TFloat(), te)
    }
    if(left.result is TFloat && right.result is TFloat){
        return TypeCheckResult(true, TFloat(), te)
    }

    return TypeCheckResult(false,  TypeError("binary_numeric_type_error", e, "left and right arguments of `${e.operationName}` are expected to be numerics, but got $left and $right in ${e.unparse()}"), te)
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
    if(left.result is TInt && right.result is TInt){
        return TypeCheckResult(true, TInt(), te)
    }
    return TypeCheckResult(false, TypeError("binary_integer_type_error", e, "left and right arguments of `${e.operationName}` are expected to be integers, but got $left and $right in ${e.unparse()}"), te)
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
    if(left.result is TFloat && right.result is TFloat){
        return TypeCheckResult(true, TFloat(), te)
    }
    return TypeCheckResult(false,  TypeError("binary_float_type_error", e, "left and right arguments of `${e.operationName}` are expected to be floats, but got $left and $right in ${e.unparse()}"), te)
}
