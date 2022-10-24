package core


fun evalSuccess(expression: Expression): CoreResult<Expression> {
    return CoreResult(true, expression, null)
}

data class OperationTypeMismatchError(
    override val input: Any,
    override val message: String,
) : ICoreError

fun evalTypeError(expression: Expression, error:String): CoreResult<Expression> {
    return CoreResult(false, null, OperationTypeMismatchError(expression, error))
}


data class DivisionByZeroError(
    override val input: Any,
    override val message: String,
) : ICoreError
