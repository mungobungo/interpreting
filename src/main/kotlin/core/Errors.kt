package core


fun evalSuccess(expression: Expression, context: Context): CoreResult<Expression> {
    return CoreResult(true, context, expression, null)
}

data class OperationTypeMismatchError(
    override val input: Any,
    override val message: String,
) : ICoreError

fun evalTypeError(expression: Expression, context: Context, error:String): CoreResult<Expression> {
    return CoreResult(false, context, null, OperationTypeMismatchError(expression, error))
}


data class ArgumentCountError(
    override val input: Any,
    override val message: String,
) : ICoreError

fun evalArgumentCountError(arguments: List<Expression>, context:Context, error:String): CoreResult<Expression> {
    return CoreResult(false, context, null, ArgumentCountError(arguments, error))
}
data class DivisionByZeroError(
    override val input: Any,
    override val message: String,
) : ICoreError
