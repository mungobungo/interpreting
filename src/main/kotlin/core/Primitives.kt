package core

import kotlin.contracts.Effect


val binaryFloatPrimitives = hashMapOf(
    "fadd" to {a:Double, b:Double -> a +b},
    "add" to {a:Double, b: Double-> a+b},
    "fmul" to {a:Double, b:Double -> a *b},
    "mul" to {a:Double, b:Double -> a*b},
    "fsub" to {a:Double, b:Double -> a -b},
    "sub" to {a:Double, b:Double -> a-b},
    "fdiv" to {a:Double, b:Double -> a /b},
    "div" to {a:Double, b:Double -> a/b},
)
fun evalBinaryFloat(operationName:String, leftExpression:Expression, rightExpression:Expression,
                      ):CoreResult<Expression>{

    val leftResult = leftExpression.eval()
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EFloat && leftResult.value !is EInt){
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected a float  or integer value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!!

    val rightResult = rightExpression.eval()
    if(!rightResult.success){
        return rightResult
    }
    if(rightResult.value !is EInt && rightResult.value !is EFloat){
        return evalTypeError(left, "type error while evaluating right argument of '${operationName}'," +
                " expected a float or integer value, but got `${rightResult.value!!.unparse()}`")
    }
    val right = rightResult.value!!

    val op = binaryFloatPrimitives[operationName]!!
    if(left is EInt && right is EInt){
        return evalSuccess(EFloat(op(left.value.toDouble(), right.value.toDouble())))
    }
    if(left is EInt && right is EFloat){
        return evalSuccess(EFloat(op(left.value.toDouble(), right.value)))
    }

    if(left is EFloat && right is EInt){
        return evalSuccess(EFloat(op(left.value, right.value.toDouble())))
    }

    return evalSuccess(EFloat(op((left as EFloat).value, (right as EFloat).value)))
}
val binaryIntPrimitives = hashMapOf(
    "iadd" to {a:Int, b:Int -> a +b},
    "add" to {a:Int, b:Int -> a+b},
    "imul" to {a:Int, b:Int -> a *b},
    "mul" to {a:Int, b:Int -> a*b},
    "isub" to {a:Int, b:Int -> a -b},
    "sub" to {a:Int, b:Int -> a-b},
    "idiv" to {a:Int, b:Int -> a /b},
    "div" to {a:Int, b:Int -> a/b},
    )
fun evalBinaryInteger(operationName:String, leftExpression:Expression, rightExpression:Expression):CoreResult<Expression>{

    val leftResult = leftExpression.eval()
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EInt){
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected an integer value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!! as EInt

    val rightResult = rightExpression.eval()
    if(!rightResult.success){
        return rightResult
    }
    if(rightResult.value !is EInt){
        return evalTypeError(left, "type error while evaluating right argument of '${operationName}'," +
                " expected an integer value, but got `${rightResult.value!!.unparse()}`")
    }
    val right = rightResult.value!! as EInt

    val op = binaryIntPrimitives[operationName]!!
    if((operationName == "div" || operationName =="idiv")  && right.value == 0){
        return CoreResult(false, null,
            DivisionByZeroError(leftExpression, "division by zero")
        )
    }
    return evalSuccess(EInt(op(left.value, right.value)))
}

fun evalBinaryNumeric(operationName:String, leftExpression:Expression, rightExpression:Expression):CoreResult<Expression>{
    val leftResult = leftExpression.eval()
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EFloat && leftResult.value !is EInt) {
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected a numeric value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!!

    val rightResult = rightExpression.eval()
    if(!rightResult.success){
        return rightResult
    }
    if(rightResult.value !is EInt && rightResult.value !is EFloat){
        return evalTypeError(left, "type error while evaluating right argument of '${operationName}'," +
                " expected a numeric value, but got `${rightResult.value!!.unparse()}`")
    }
    val right = rightResult.value!!

    if(left is EInt && right is EInt){
        return evalBinaryInteger(operationName, left, right)
    }
    if(left is EFloat && right is EInt){
        return evalBinaryFloat(operationName, left, EFloat((right).value.toDouble()))
    }
    if(left is EInt && right is EFloat){
        return evalBinaryFloat(operationName, EFloat(left.value.toDouble()), right)
    }

    return evalBinaryFloat(operationName, left, right)
}
data class EIntMul(val left:Expression, val right:Expression): Expression {
    override fun eval(): CoreResult<Expression> {
        return evalBinaryInteger("imul", left, right)
    }

    override fun unparse(): String {
        return "[imul, ${left.unparse()}, ${right.unparse()}]".format()
    }
}

data class EMul(val left:Expression, val right:Expression): Expression {
    override fun eval(): CoreResult<Expression> {
        return evalBinaryNumeric("mul", left, right)
    }

    override fun unparse(): String {
        return "[mul, ${left.unparse()}, ${right.unparse()}]".format()
    }
}
data class EFloatMul(val left:Expression, val right: Expression): Expression {
    override fun eval(): CoreResult<Expression> {

        return evalBinaryFloat("fmul", left, right)
    }


    override fun unparse(): String {
        return "[fmul, ${left.unparse()}, ${right.unparse()}]"
    }
}
data class EFloatAdd(val left:Expression, val right: Expression): Expression {
    override fun eval(): CoreResult<Expression> {

        return evalBinaryFloat("fadd", left, right)
    }


    override fun unparse(): String {
        return "[fadd, ${left.unparse()}, ${right.unparse()}]"
    }
}
data class EIntAdd(val left:Expression, val right: Expression): Expression {
    override fun eval(): CoreResult<Expression> {

        return evalBinaryInteger("iadd", left, right)
    }


    override fun unparse(): String {
        return "[iadd, ${left.unparse()}, ${right.unparse()}]"
    }
}

data class EAdd(val left:Expression, val right: Expression): Expression {
    override fun eval(): CoreResult<Expression> {
        return evalBinaryNumeric("add", left, right)
    }


    override fun unparse(): String {
        return "[add, ${left.unparse()}, ${right.unparse()}]"
    }
}
data class EIntDiv(val left:Expression, val right: Expression): Expression {
    override fun eval(): CoreResult<Expression> {
        return evalBinaryNumeric("idiv", left, right)
    }
    override fun unparse(): String {
        return "[idiv, ${left.unparse()}, ${right.unparse()}]"
    }
}
data class EDiv(val left:Expression, val right: Expression): Expression {
    override fun eval(): CoreResult<Expression> {
        return evalBinaryNumeric("div", left, right)
    }

    override fun unparse(): String {
        return "[div, ${left.unparse()}, ${right.unparse()}]"
    }
}


data class EIsInt(val v:Expression):Expression{
    override fun eval(): CoreResult<Expression> {
        return  evalSuccess(EBool( v is EInt))
    }

    override fun unparse(): String {
        return "[is_int, ${v.unparse()}]"
    }

}


data class EIsFloat(val v:Expression):Expression{
    override fun eval(): CoreResult<Expression> {
        return  evalSuccess(EBool( v is EFloat))
    }

    override fun unparse(): String {
        return "[is_float, ${v.unparse()}]"
    }

}
data class EIsBool(val v:Expression):Expression{
    override fun eval(): CoreResult<Expression> {
        return  evalSuccess(EBool( v is EBool))
    }

    override fun unparse(): String {
        return "[is_bool, ${v.unparse()}]"
    }

}
