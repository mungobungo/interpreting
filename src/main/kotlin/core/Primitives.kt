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
            leftExpression, "type error while evaluating left argument of '${operationName}'," +
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
            left, "type error while evaluating right argument of '${operationName}'," +
                    " expected a boolean value, but got `${rightResult.value!!.unparse()}`"
        )
    }
    val right = rightResult.value!! as EBool

    val op = binaryBoolPrimitives[operationName]!!
    return evalSuccess(EBool(op(left.value, right.value)))
}

data class ESetVar(val name: String, val variableValue: Expression) : Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        val valueResult = variableValue.eval(context)
        if (!valueResult.success) {
            return valueResult
        }
        context.variables.bindings[name] = valueResult.value!!
        return evalSuccess(valueResult.value!!)
    }

    override fun unparse(): String {
        return "[setvar, $name, ${variableValue.unparse()}]"
    }

}

data class EDo(val expressions: List<Expression>) : Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        val localContext = context.expand()
        val res = EList(expressions).eval(localContext)
        if (!res.success) {
            return res
        }
        val computed = res.value as EList
        return evalSuccess(computed.elems.last())
    }

    override fun unparse(): String {

        return "[do, ${expressions.joinToString(", ") { it.unparse() }}]"
    }
}

data class EEq(val first: Expression, val second: Expression) : Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        val firstResult = first.eval(context)
        if (!firstResult.success) {
            return firstResult
        }
        if (firstResult.value !is IComparable) {
            return evalTypeError(first, "cannot call 'eq'  on ${first.unparse()} \n ${firstResult.value!!.unparse()}")
        }
        val secondResult = second.eval(context)
        if (!secondResult.success) {
            return secondResult
        }
        val comparision = (firstResult.value as IComparable).eq(secondResult.value!!)
        if (comparision.success) {
            return CoreResult(true, comparision.value!!, null)
        }
        return CoreResult(false, null, comparision.error)
    }

    override fun unparse(): String {
        return "[eq, ${first.unparse()}, ${second.unparse()}]"
    }

}

data class ENeq(val first: Expression, val second: Expression) : Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        val firstResult = first.eval(context)
        if (!firstResult.success) {
            return firstResult
        }
        if (firstResult.value !is IComparable) {
            return evalTypeError(first, "cannot call 'neq'  on ${first.unparse()} \n ${firstResult.value!!.unparse()}")
        }
        val secondResult = second.eval(context)
        if (!secondResult.success) {
            return secondResult
        }
        val comparision = (firstResult.value as IComparable).neq(secondResult.value!!)
        if (comparision.success) {
            return CoreResult(true, comparision.value!!, null)
        }
        return CoreResult(false, null, comparision.error)
    }

    override fun unparse(): String {
        return "[neq, ${first.unparse()}, ${second.unparse()}]"
    }

}

data class ELt(val first: Expression, val second: Expression) : Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        val firstResult = first.eval(context)
        if (!firstResult.success) {
            return firstResult
        }
        if (firstResult.value !is IOrdered) {
            return evalTypeError(first, "cannot call 'lt'  on ${first.unparse()} \n ${firstResult.value!!.unparse()}")
        }
        val secondResult = second.eval(context)
        if (!secondResult.success) {
            return secondResult
        }
        val comparision = (firstResult.value as IOrdered).lt(secondResult.value!!)
        if (comparision.success) {
            return CoreResult(true, comparision.value!!, null)
        }
        return CoreResult(false, null, comparision.error)
    }

    override fun unparse(): String {
        return "[lt, ${first.unparse()}, ${second.unparse()}]"
    }

}

data class ELte(val first: Expression, val second: Expression) : Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        val firstResult = first.eval(context)
        if (!firstResult.success) {
            return firstResult
        }
        if (firstResult.value !is IOrdered || firstResult.value !is IComparable) {
            return evalTypeError(first, "cannot call 'lte'  on ${first.unparse()} \n ${firstResult.value!!.unparse()}")
        }
        val secondResult = second.eval(context)
        if (!secondResult.success) {
            return secondResult
        }
        val comparision = (firstResult.value as IOrdered).lte(secondResult.value!!)
        if (comparision.success) {
            return CoreResult(true, comparision.value!!, null)
        }
        return CoreResult(false, null, comparision.error)
    }

    override fun unparse(): String {
        return "[lte, ${first.unparse()}, ${second.unparse()}]"
    }

}

data class EGt(val first: Expression, val second: Expression) : Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        val firstResult = first.eval(context)
        if (!firstResult.success) {
            return firstResult
        }
        if (firstResult.value !is IOrdered) {
            return evalTypeError(first, "cannot call 'gt'  on ${first.unparse()} \n ${firstResult.value!!.unparse()}")
        }
        val secondResult = second.eval(context)
        if (!secondResult.success) {
            return secondResult
        }
        val comparision = (firstResult.value as IOrdered).gt(secondResult.value!!)
        if (comparision.success) {
            return CoreResult(true, comparision.value!!, null)
        }
        return CoreResult(false, null, comparision.error)
    }

    override fun unparse(): String {
        return "[gt, ${first.unparse()}, ${second.unparse()}]"
    }

}

data class EGte(val first: Expression, val second: Expression) : Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        val firstResult = first.eval(context)
        if (!firstResult.success) {
            return firstResult
        }
        if (firstResult.value !is IOrdered || firstResult.value !is IComparable) {
            return evalTypeError(first, "cannot call 'gte'  on ${first.unparse()} \n ${firstResult.value!!.unparse()}")
        }
        val secondResult = second.eval(context)
        if (!secondResult.success) {
            return secondResult
        }
        val comparision = (firstResult.value as IOrdered).gte(secondResult.value!!)
        if (comparision.success) {
            return CoreResult(true, comparision.value!!, null)
        }
        return CoreResult(false, null, comparision.error)
    }

    override fun unparse(): String {
        return "[gte, ${first.unparse()}, ${second.unparse()}]"
    }

}

fun binaryNumeric(name: String): EPrimitive {
    return EPrimitive(name) { params: List<Expression>, context: Context -> evalBinaryNumeric(name, params, context) }
}

fun binaryBool(name: String): EPrimitive {
    return EPrimitive(name) { params: List<Expression>, context: Context -> evalBinaryBool(name, params, context) }
}

fun unary(name: String): EPrimitive {
    return EPrimitive(name) { params: List<Expression>, context: Context -> unaryPrimitive(name, params, context) }
}

val unaryPrimitives: HashMap<String, (Expression) -> CoreResult<Expression>> = hashMapOf(
    "is_int" to { a: Expression -> evalSuccess(EBool(a is EInt)) },
    "is_float" to { a: Expression -> evalSuccess(EBool(a is EFloat)) },
    "is_bool" to { a: Expression -> evalSuccess(EBool(a is EBool)) },
    "not" to { a: Expression ->
        if (a !is EBool)
            evalTypeError(a, "'not' applicable only to booleans, got '${a.unparse()}'")
        else
            evalSuccess(EBool(!(a as EBool).value))
    },
    "neg" to { a: Expression ->
        if (a !is EInt && a !is EFloat)
            evalTypeError(a, "'neg' applicable only to numeric values, got '${a.unparse()}'")
        else
            if (a is EInt) {
                evalSuccess(EInt((a as EInt).value * -1))
            } else {
                evalSuccess(EFloat((a as EFloat).value * -1))
            }

    }
)

fun unaryPrimitive(name: String, params: List<Expression>, context: Context): CoreResult<Expression> {
    if (params.size != 1) {
        return evalArgumentCountError(params, "'$name' expecting one argument, bot ${params.size}")
    }
    val evaluated = params[0]!!.eval(context)
    if (!evaluated.success) {
        return evaluated
    }
    val func = unaryPrimitives[name]!!
    return func(evaluated.value!!)

}

val primitives: HashMap<String, EPrimitive> = hashMapOf(
    "iadd" to binaryNumeric("iadd"),
    "imul" to binaryNumeric("imul"),
    "isub" to binaryNumeric("isub"),
    "idiv" to binaryNumeric("idiv"),
    "fadd" to binaryNumeric("fadd"),
    "fmul" to binaryNumeric("fmul"),
    "fsub" to binaryNumeric("fsub"),
    "fdiv" to binaryNumeric("fdiv"),
    "add" to binaryNumeric("add"),
    "mul" to binaryNumeric("mul"),
    "sub" to binaryNumeric("sub"),
    "div" to binaryNumeric("div"),
    "and" to binaryBool("and"),
    "or" to binaryBool("or"),
    "xor" to binaryBool("xor"),
    "not" to unary("not"),
    "is_int" to unary("is_int"),
    "is_float" to unary("is_float"),
    "is_bool" to unary("is_bool"),
    "neg" to unary("neg")


)
