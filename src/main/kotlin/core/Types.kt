package core

interface StrongType{
    val type:String
}

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
        var res = current!!.isLocallyDefined(symbol)
        if(res.first){
            return res
        }
        while(current!=null){
            current = current.parent
            res = current!!.isLocallyDefined(symbol)
            if(res.first) {
                return res
            }
        }
        return Pair(false,TypeError("binding_not_found",ESymbol(symbol), "symbol `$symbol` not found in typeEnv $this"))
    }
}

data class TInt(override val type:String = "int"): StrongType

data class  TFloat(override val type:String="float"): StrongType

data class  TBool(override val type:String="bool"): StrongType

data class TypeError(override val type:String ="type_error", val input: Expression,  val error:String):StrongType


fun typeOf(e: Expression) : StrongType{
    when(e){
        is EInt -> return TInt()
        is EFloat -> return TFloat()
        is EBool -> return TBool()
        is EBinaryIntegerOp -> return binaryIntegerOpType(e)
        is EBinaryFloatOp -> return binaryFloatOpType(e)
        is EBinaryNumericOp -> return binaryNumericOpType(e)
        is EBinaryBoolOp -> return binaryBoolOpType(e)
    }
    return TypeError("type_error", e, "unsupported expression ${e.unparse()}")
}

fun binaryBoolOpType(e: EBinaryBoolOp): StrongType {
   val left = typeOf(e.left)
   val right = typeOf(e.right)
   if(left is TBool && right is TBool){
       return TBool()
   }
    return TypeError("binary_bool_type_error", e, "left and right arguments of `${e.operationName}` are expected to be booleans, but got $left and $right in ${e.unparse()}")
}

fun binaryNumericOpType(e: EBinaryNumericOp): StrongType {
    val left = typeOf(e.left)
    val right = typeOf(e.right)
    if(left is TInt && right is TInt){
        return TInt()
    }
    if(left is TFloat && right is TInt){
        return TFloat()
    }
    if(left is TInt && right is TFloat){
        return TFloat()
    }
    if(left is TFloat && right is TFloat){
        return TFloat()
    }

    return TypeError("binary_numeric_type_error", e, "left and right arguments of `${e.operationName}` are expected to be numerics, but got $left and $right in ${e.unparse()}")
}

fun binaryIntegerOpType(e: EBinaryIntegerOp): StrongType {
    val left = typeOf(e.left)
    val right = typeOf(e.right)
    if(left is TInt && right is TInt){
        return TInt()
    }
    return TypeError("binary_integer_type_error", e, "left and right arguments of `${e.operationName}` are expected to be integers, but got $left and $right in ${e.unparse()}")
}
fun binaryFloatOpType(e: EBinaryFloatOp): StrongType {
    val left = typeOf(e.left)
    val right = typeOf(e.right)
    if(left is TFloat && right is TFloat){
        return TFloat()
    }
    return TypeError("binary_float_type_error", e, "left and right arguments of `${e.operationName}` are expected to be floats, but got $left and $right in ${e.unparse()}")
}
