package core

import kotlin.math.abs

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
data class EBinaryFloatOp(val operationName:String, val left:Expression, val right: Expression): Expression {
    override fun eval(context:Context): CoreResult<Expression> {

        return evalBinaryFloat(operationName, left, right, context)
    }
    override fun unparse(): String {
        return "[$operationName, ${left.unparse()}, ${right.unparse()}]"
    }
}
fun evalBinaryFloat(operationName:String, leftExpression:Expression, rightExpression:Expression,
                      context: Context):CoreResult<Expression>{

    val leftResult = leftExpression.eval(context)
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EFloat && leftResult.value !is EInt){
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected a float  or integer value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!!

    val rightResult = rightExpression.eval(context)
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

val binaryFloatBoolPrimitives = hashMapOf(
    "lt" to {a:Double, b:Double -> a <b},
    "lte" to {a:Double, b: Double-> a<=b},
    "gt" to {a:Double, b:Double -> a >b},
    "gte" to {a:Double, b:Double -> a>=b},
    "eq" to {a:Double, b:Double -> abs(a -b) <0.0000001 },
    "neq" to {a:Double, b:Double -> abs(a-b) >0.0000001},
)
fun evalBinaryFloatBool(operationName:String, leftExpression:Expression, rightExpression:Expression,
                        context: Context
):CoreResult<Expression>{

    val leftResult = leftExpression.eval(context)
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EFloat && leftResult.value !is EInt){
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected a float  or integer value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!!

    val rightResult = rightExpression.eval(context)
    if(!rightResult.success){
        return rightResult
    }
    if(rightResult.value !is EInt && rightResult.value !is EFloat){
        return evalTypeError(left, "type error while evaluating right argument of '${operationName}'," +
                " expected a float or integer value, but got `${rightResult.value!!.unparse()}`")
    }
    val right = rightResult.value!!

    val op = binaryFloatBoolPrimitives[operationName]!!
    if(left is EInt && right is EInt){
        return evalSuccess(EBool(op(left.value.toDouble(), right.value.toDouble())))
    }
    if(left is EInt && right is EFloat){
        return evalSuccess(EBool(op(left.value.toDouble(), right.value)))
    }

    if(left is EFloat && right is EInt){
        return evalSuccess(EBool(op(left.value, right.value.toDouble())))
    }

    return evalSuccess(EBool(op((left as EFloat).value, (right as EFloat).value)))
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
fun evalBinaryInteger(operationName:String, leftExpression: Expression, rightExpression: Expression,
                        context:Context
                      ):CoreResult<Expression>{

    val leftResult = leftExpression.eval(context)
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EInt){
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected an integer value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!! as EInt

    val rightResult = rightExpression.eval(context)
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

val binaryIntBoolPrimitives = hashMapOf(
    "lt" to {a:Int, b:Int -> a < b},
    "lte" to {a:Int, b:Int -> a<=b},
    "gt" to {a:Int, b:Int -> a >b},
    "gte" to {a:Int, b:Int -> a>=b},
    "eq" to {a:Int, b:Int -> a ==b},
    "neq" to {a:Int, b:Int -> a != b},
)
fun evalBinaryIntegerBool(operationName:String, leftExpression:Expression, rightExpression:Expression,
context: Context
                          ):CoreResult<Expression>{

    val leftResult = leftExpression.eval(context)
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EInt){
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected an integer value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!! as EInt

    val rightResult = rightExpression.eval(context)
    if(!rightResult.success){
        return rightResult
    }
    if(rightResult.value !is EInt){
        return evalTypeError(left, "type error while evaluating right argument of '${operationName}'," +
                " expected an integer value, but got `${rightResult.value!!.unparse()}`")
    }
    val right = rightResult.value!! as EInt

    val op = binaryIntBoolPrimitives[operationName]!!
    return evalSuccess(EBool(op(left.value, right.value)))
}
fun evalBinaryNumeric(operationName:String, params:List<Expression>,
context: Context):CoreResult<Expression>{

    if(params.size !=2){
        return evalArgumentCountError(params,
            "parameter count mismatch for $operationName, expected 2, got ${params.size} \n" +
                    params.joinToString(", ") { it.unparse() })
    }
    val leftExpression = params[0]!!
    val rightExpression = params[1]!!
    val leftResult = leftExpression.eval(context)
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EFloat && leftResult.value !is EInt) {
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected a numeric value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!!

    val rightResult = rightExpression.eval(context)
    if(!rightResult.success){
        return rightResult
    }
    if(rightResult.value !is EInt && rightResult.value !is EFloat){
        return evalTypeError(left, "type error while evaluating right argument of '${operationName}'," +
                " expected a numeric value, but got `${rightResult.value!!.unparse()}`")
    }
    val right = rightResult.value!!

    if(left is EInt && right is EInt){
        return evalBinaryInteger(operationName, left, right, context)
    }
    if(left is EFloat && right is EInt){
        return evalBinaryFloat(operationName, left, EFloat((right).value.toDouble()), context)
    }
    if(left is EInt && right is EFloat){
        return evalBinaryFloat(operationName, EFloat(left.value.toDouble()), right, context)
    }

    return evalBinaryFloat(operationName, left, right,context)
}


data class EBinaryNumericBoolOp(val operationName:String, val left:Expression, val right:Expression): Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        return evalBinaryNumericBool(operationName, left, right, context)
    }

    override fun unparse(): String {
        return "[$operationName, ${left.unparse()}, ${right.unparse()}]".format()
    }
}
fun evalBinaryNumericBool(operationName:String, leftExpression:Expression, rightExpression:Expression,
context: Context):CoreResult<Expression>{
    val leftResult = leftExpression.eval(context)
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EFloat && leftResult.value !is EInt) {
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected a numeric value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!!

    val rightResult = rightExpression.eval(context)
    if(!rightResult.success){
        return rightResult
    }
    if(rightResult.value !is EInt && rightResult.value !is EFloat){
        return evalTypeError(left, "type error while evaluating right argument of '${operationName}'," +
                " expected a numeric value, but got `${rightResult.value!!.unparse()}`")
    }
    val right = rightResult.value!!

    if(left is EInt && right is EInt){
        return evalBinaryIntegerBool(operationName, left, right, context)
    }
    if(left is EFloat && right is EInt){
        return evalBinaryFloatBool(operationName, left, EFloat((right).value.toDouble()), context)
    }
    if(left is EInt && right is EFloat){
        return evalBinaryFloatBool(operationName, EFloat(left.value.toDouble()), right,context)
    }

    return evalBinaryFloatBool(operationName, left, right, context)
}


data class EIsInt(val v:Expression):Expression{
    override fun eval(context: Context): CoreResult<Expression> {
        return  evalSuccess(EBool( v is EInt))
    }

    override fun unparse(): String {
        return "[is_int, ${v.unparse()}]"
    }

}


data class EIsFloat(val v:Expression):Expression{
    override fun eval(context: Context): CoreResult<Expression> {
        return  evalSuccess(EBool( v is EFloat))
    }

    override fun unparse(): String {
        return "[is_float, ${v.unparse()}]"
    }

}
data class EIsBool(val v:Expression):Expression{
    override fun eval(context: Context): CoreResult<Expression> {
        return  evalSuccess(EBool( v is EBool))
    }

    override fun unparse(): String {
        return "[is_bool, ${v.unparse()}]"
    }

}
data class ENot(val v:Expression):Expression{
    override fun eval(context: Context): CoreResult<Expression> {
        val evaluated = v.eval(context)
        if(!evaluated.success){
            return evaluated
        }
        val res = evaluated.value!!
        if(res !is EBool){

            return evalTypeError(v, "type error while evaluating  argument of 'not'," +
                    " expected a boolean value, but got `${res.unparse()}`")
        }
        return  evalSuccess(EBool( !res.value))
    }

    override fun unparse(): String {
        return "[not, ${v.unparse()}]"
    }

}
val binaryBoolPrimitives = hashMapOf(
    "and" to {a:Boolean, b:Boolean -> a &&b},
    "or" to {a:Boolean, b:Boolean -> a||b},
    "xor" to {a:Boolean, b:Boolean -> a xor b},
)
data class EBinaryBoolOp(val operationName: String, val left:Expression, val right:Expression): Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        return evalBinaryBool(operationName, left, right, context)
    }

    override fun unparse(): String {
        return "[$operationName, ${left.unparse()}, ${right.unparse()}]".format()
    }
}
fun evalBinaryBool(operationName:String, leftExpression:Expression, rightExpression:Expression,
context: Context):CoreResult<Expression>{

    val leftResult = leftExpression.eval(context)
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EBool){
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected a boolean value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!! as EBool

    val rightResult = rightExpression.eval(context)
    if(!rightResult.success){
        return rightResult
    }
    if(rightResult.value !is EBool){
        return evalTypeError(left, "type error while evaluating right argument of '${operationName}'," +
                " expected a boolean value, but got `${rightResult.value!!.unparse()}`")
    }
    val right = rightResult.value!! as EBool

    val op = binaryBoolPrimitives[operationName]!!
    return evalSuccess(EBool(op(left.value, right.value)))
}

data class ESetVar(val name:String, val variableValue:Expression):Expression{
    override fun eval(context: Context): CoreResult<Expression>{
        val valueResult = variableValue.eval(context)
        if(!valueResult.success){
            return valueResult
        }
        context.variables.bindings[name] = valueResult.value!!
        return evalSuccess( valueResult.value!!)
    }

    override fun unparse(): String {
        return "[setvar, $name, ${variableValue.unparse()}]"
    }

}
data class EDo(val expressions:List<Expression>): Expression{
    override fun eval(context: Context): CoreResult<Expression> {
        val localContext = context.expand()
        val res = EList(expressions).eval(localContext)
        if(!res.success){
            return res
        }
        val computed = res.value as EList
        return evalSuccess( computed.elems.last())
    }

    override fun unparse(): String {

        return "[do, ${expressions.joinToString(", ") { it.unparse() }}]"
    }
}

