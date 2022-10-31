package core



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
