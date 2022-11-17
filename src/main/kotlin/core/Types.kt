package core

interface StrongType{
    val type:String
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
    }
    return TypeError("type_error", e, "unsupported expression ${e.unparse()}")
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
