package core
val binaryFloatPrimitives = hashMapOf(
    "fadd" to { a: Double, b: Double -> a + b },
    "add" to { a: Double, b: Double -> a + b },
    "fmul" to { a: Double, b: Double -> a * b },
    "mul" to { a: Double, b: Double -> a * b },
    "fsub" to { a: Double, b: Double -> a - b },
    "sub" to { a: Double, b: Double -> a - b },
    "fdiv" to { a: Double, b: Double -> a / b },
    "div" to { a: Double, b: Double -> a / b },
)

fun evalBinaryFloat(
    operationName: String, leftExpression: Expression, rightExpression: Expression,
    context: Context
): CoreResult<Expression> {

    val leftResult = leftExpression.eval(context)
    if (!leftResult.success) {
        return leftResult
    }

    if (leftResult.value !is EFloat && leftResult.value !is EInt) {
        return evalTypeError(
            leftExpression, "type error while evaluating left argument of '${operationName}'," +
                    " expected a float  or integer value, but got `${leftResult.value!!.unparse()}`"
        )
    }

    val left = leftResult.value!!

    val rightResult = rightExpression.eval(context)
    if (!rightResult.success) {
        return rightResult
    }
    if (rightResult.value !is EInt && rightResult.value !is EFloat) {
        return evalTypeError(
            left, "type error while evaluating right argument of '${operationName}'," +
                    " expected a float or integer value, but got `${rightResult.value!!.unparse()}`"
        )
    }
    val right = rightResult.value!!

    val op = binaryFloatPrimitives[operationName]!!
    if (left is EInt && right is EInt) {
        return evalSuccess(EFloat(op(left.value.toDouble(), right.value.toDouble())))
    }
    if (left is EInt && right is EFloat) {
        return evalSuccess(EFloat(op(left.value.toDouble(), right.value)))
    }

    if (left is EFloat && right is EInt) {
        return evalSuccess(EFloat(op(left.value, right.value.toDouble())))
    }

    return evalSuccess(EFloat(op((left as EFloat).value, (right as EFloat).value)))
}


val binaryIntPrimitives = hashMapOf(
    "iadd" to { a: Int, b: Int -> a + b },
    "add" to { a: Int, b: Int -> a + b },
    "imul" to { a: Int, b: Int -> a * b },
    "mul" to { a: Int, b: Int -> a * b },
    "isub" to { a: Int, b: Int -> a - b },
    "sub" to { a: Int, b: Int -> a - b },
    "idiv" to { a: Int, b: Int -> a / b },
    "div" to { a: Int, b: Int -> a / b },
)

fun evalBinaryInteger(
    operationName: String, leftExpression: Expression, rightExpression: Expression,
    context: Context
): CoreResult<Expression> {

    val leftResult = leftExpression.eval(context)
    if (!leftResult.success) {
        return leftResult
    }

    if (leftResult.value !is EInt) {
        return evalTypeError(
            leftExpression, "type error while evaluating left argument of '${operationName}'," +
                    " expected an integer value, but got `${leftResult.value!!.unparse()}`"
        )
    }

    val left = leftResult.value!! as EInt

    val rightResult = rightExpression.eval(context)
    if (!rightResult.success) {
        return rightResult
    }
    if (rightResult.value !is EInt) {
        return evalTypeError(
            left, "type error while evaluating right argument of '${operationName}'," +
                    " expected an integer value, but got `${rightResult.value!!.unparse()}`"
        )
    }
    val right = rightResult.value!! as EInt

    val op = binaryIntPrimitives[operationName]!!
    if ((operationName == "div" || operationName == "idiv") && right.value == 0) {
        return CoreResult(
            false, null,
            DivisionByZeroError(leftExpression, "division by zero")
        )
    }
    return evalSuccess(EInt(op(left.value, right.value)))
}

fun evalBinaryNumeric(
    operationName: String, params: List<Expression>,
    context: Context
): CoreResult<Expression> {

    if (params.size != 2) {
        return evalArgumentCountError(params,
            "parameter count mismatch for $operationName, expected 2, got ${params.size} \n" +
                    params.joinToString(", ") { it.unparse() })
    }
    val leftExpression = params[0]!!
    val rightExpression = params[1]!!
    val leftResult = leftExpression.eval(context)
    if (!leftResult.success) {
        return leftResult
    }

    if (leftResult.value !is EFloat && leftResult.value !is EInt) {
        return evalTypeError(
            leftExpression, "type error while evaluating left argument of '${operationName}'," +
                    " expected a numeric value, but got `${leftResult.value!!.unparse()}`"
        )
    }

    val left = leftResult.value!!

    val rightResult = rightExpression.eval(context)
    if (!rightResult.success) {
        return rightResult
    }
    if (rightResult.value !is EInt && rightResult.value !is EFloat) {
        return evalTypeError(
            left, "type error while evaluating right argument of '${operationName}'," +
                    " expected a numeric value, but got `${rightResult.value!!.unparse()}`"
        )
    }
    val right = rightResult.value!!

    if (left is EInt && right is EInt) {
        if(operationName.startsWith("f")){

            return evalBinaryFloat(operationName, EFloat( left.value.toDouble()), EFloat((right).value.toDouble()), context)
        }
        return evalBinaryInteger(operationName, left, right, context)
    }
    if(operationName.startsWith("i")){
        return evalTypeError(leftExpression, "'$operationName' is working only with integers, but got ${leftExpression.unparse()} and ${rightExpression.unparse()}")
    }
    if (left is EFloat && right is EInt) {
        return evalBinaryFloat(operationName, left, EFloat((right).value.toDouble()), context)
    }
    if (left is EInt && right is EFloat) {
        return evalBinaryFloat(operationName, EFloat(left.value.toDouble()), right, context)
    }

    return evalBinaryFloat(operationName, left, right, context)
}

