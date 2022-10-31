package core



data class ESetVar(val name: String, val variableValue: Expression) : Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        val valueResult = variableValue.eval(context)
        if (!valueResult.success) {
            return valueResult
        }
        // Critical for the evaluation of the closures
        // [setvar, myc, [let, c, 0, [lambda, [do, [setvar, c, [add, c, 1]], c]]]]
        val expanded = valueResult.context.clone()
        expanded.variables.bindings[name] = valueResult.value!!
        return evalSuccess(valueResult.value!!, expanded)
    }

    override fun unparse(): String {
        return "[setvar, $name, ${variableValue.unparse()}]"
    }

}

data class EDo(val expressions: List<Expression>) : Expression {
    override fun eval(context: Context): CoreResult<Expression> {

        var res: Expression?= null

        var latestContext = context.clone()

        for(e:Expression in expressions){
            val eResult = e.eval(latestContext)
            if(!eResult.success){
                return eResult
            }
            latestContext = eResult.context
            res = eResult.value!!
        }
        return evalSuccess(res!!,latestContext)

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

val unaryPrimitives: HashMap<String, (Expression, Context) -> CoreResult<Expression>> = hashMapOf(
    "is_int" to { a: Expression, c:Context -> evalSuccess(EBool(a is EInt),c) },
    "is_float" to { a: Expression , c:Context -> evalSuccess(EBool(a is EFloat),c) },
    "is_bool" to { a: Expression , c:Context -> evalSuccess(EBool(a is EBool),c) },
    "not" to { a: Expression , c:Context ->
        if (a !is EBool)
            evalTypeError(a, c,"'not' applicable only to booleans, got '${a.unparse()}'")
        else
            evalSuccess(EBool(!(a as EBool).value),c)
    },
    "neg" to { a: Expression , c:Context ->
        if (a !is EInt && a !is EFloat)
            evalTypeError(a, c,"'neg' applicable only to numeric values, got '${a.unparse()}'")
        else
            if (a is EInt) {
                evalSuccess(EInt((a as EInt).value * -1),c)
            } else {
                evalSuccess(EFloat((a as EFloat).value * -1),c)
            }

    }
)

fun unaryPrimitive(name: String, params: List<Expression>, context: Context): CoreResult<Expression> {
    if (params.size != 1) {
        return evalArgumentCountError(params, context,"'$name' expecting one argument, bot ${params.size}")
    }
    val evaluated = params[0]!!.eval(context)
    if (!evaluated.success) {
        return evaluated
    }
    val func = unaryPrimitives[name]!!
    return func(evaluated.value!!, context)

}

