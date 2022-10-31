package core
val binaryBoolPrimitives = hashMapOf(
    "and" to { a: Boolean, b: Boolean -> a && b },
    "or" to { a: Boolean, b: Boolean -> a || b },
    "xor" to { a: Boolean, b: Boolean -> a xor b },
)

fun evalBinaryBool(
    operationName: String, params: List<Expression>,
    context: Context
): CoreResult<Expression> {

    val leftExpression = params[0]!!
    val rightExpression = params[1]!!
    val leftResult = leftExpression.eval(context)
    if (!leftResult.success) {
        return leftResult
    }

    if (leftResult.value !is EBool) {
        return evalTypeError(
            leftExpression, context,"type error while evaluating left argument of '${operationName}'," +
                    " expected a boolean value, but got `${leftResult.value!!.unparse()}`"
        )
    }

    val left = leftResult.value!! as EBool

    val rightResult = rightExpression.eval(context)
    if (!rightResult.success) {
        return rightResult
    }
    if (rightResult.value !is EBool) {
        return evalTypeError(
            left, context, "type error while evaluating right argument of '${operationName}'," +
                    " expected a boolean value, but got `${rightResult.value!!.unparse()}`"
        )
    }
    val right = rightResult.value!! as EBool

    val op = binaryBoolPrimitives[operationName]!!
    return evalSuccess(EBool(op(left.value, right.value)), context)
}
