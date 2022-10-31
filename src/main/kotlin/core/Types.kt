package core


data class EInt(val value:Int):Expression, IComparable, IOrdered {
    override fun eq(other: Expression): CoreResult<EBool> {

       if(other !is EInt && other !is EFloat){
           return CoreResult(true, EBool(false), null)
       }
       var otherVal = 0
       if(other is EFloat){
           otherVal = other.value.toInt()
       }
        if(other is EInt){
            otherVal = other.value
        }
        return CoreResult(true, EBool(otherVal == value), null)
    }

    override fun neq(other: Expression): CoreResult<EBool> {

        if(other !is EInt && other !is EFloat){
            return CoreResult(true, EBool(false), null)
        }
        var otherVal = 0
        if(other is EFloat){
            otherVal = other.value.toInt()
        }
        if(other is EInt){
            otherVal = other.value
        }
        return CoreResult(true, EBool(otherVal != value), null)
    }

    override fun lt(other: Expression): CoreResult<EBool> {
        if(other !is EInt && other !is EFloat){
            return CoreResult(true, EBool(false), null)
        }
        var otherVal = 0
        if(other is EFloat){
            otherVal = other.value.toInt()
        }
        if(other is EInt){
            otherVal = other.value
        }
        return CoreResult(true, EBool(otherVal < value), null)
    }

    override fun lte(other: Expression): CoreResult<EBool> {
        if(other !is EInt && other !is EFloat){
            return CoreResult(true, EBool(false), null)
        }
        var otherVal = 0
        if(other is EFloat){
            otherVal = other.value.toInt()
        }
        if(other is EInt){
            otherVal = other.value
        }
        return CoreResult(true, EBool(otherVal <= value), null)
    }

    override fun gt(other: Expression): CoreResult<EBool> {
        if(other !is EInt && other !is EFloat){
            return CoreResult(true, EBool(false), null)
        }
        var otherVal = 0
        if(other is EFloat){
            otherVal = other.value.toInt()
        }
        if(other is EInt){
            otherVal = other.value
        }
        return CoreResult(true, EBool(otherVal > value), null)
    }

    override fun gte(other: Expression): CoreResult<EBool> {
        if(other !is EInt && other !is EFloat){
            return CoreResult(true, EBool(false), null)
        }
        var otherVal = 0
        if(other is EFloat){
            otherVal = other.value.toInt()
        }
        if(other is EInt){
            otherVal = other.value
        }
        return CoreResult(true, EBool(otherVal >= value), null)
    }

    override fun eval(context: Context): CoreResult<Expression> {
        return evalSuccess(this)
    }

    override fun unparse(): String {
        return value.toString()
    }
}

data class EFloat(val value:Double):Expression, IOrdered, IComparable {
    override fun eq(other: Expression): CoreResult<EBool> {

        if(other !is EInt && other !is EFloat){
            return CoreResult(true, EBool(false), null)
        }
        var otherVal = 0.0
        if(other is EFloat){
            otherVal = other.value
        }
        if(other is EInt){
            otherVal = other.value.toDouble()
        }
        return CoreResult(true, EBool(otherVal == value), null)
    }

    override fun neq(other: Expression): CoreResult<EBool> {

        if(other !is EInt && other !is EFloat){
            return CoreResult(true, EBool(false), null)
        }
        var otherVal = 0.0
        if(other is EFloat){
            otherVal = other.value
        }
        if(other is EInt){
            otherVal = other.value.toDouble()
        }
        return CoreResult(true, EBool(otherVal != value), null)
    }

    override fun lt(other: Expression): CoreResult<EBool> {
        if(other !is EInt && other !is EFloat){
            return CoreResult(true, EBool(false), null)
        }
        var otherVal = 0.0
        if(other is EFloat){
            otherVal = other.value
        }
        if(other is EInt){
            otherVal = other.value.toDouble()
        }
        return CoreResult(true, EBool(otherVal < value), null)
    }

    override fun lte(other: Expression): CoreResult<EBool> {
        if(other !is EInt && other !is EFloat){
            return CoreResult(true, EBool(false), null)
        }
        var otherVal = 0.0
        if(other is EFloat){
            otherVal = other.value
        }
        if(other is EInt){
            otherVal = other.value.toDouble()
        }
        return CoreResult(true, EBool(otherVal <= value), null)
    }

    override fun gt(other: Expression): CoreResult<EBool> {
        if(other !is EInt && other !is EFloat){
            return CoreResult(true, EBool(false), null)
        }
        var otherVal = 0.0
        if(other is EFloat){
            otherVal = other.value
        }
        if(other is EInt){
            otherVal = other.value.toDouble()
        }
        return CoreResult(true, EBool(otherVal > value), null)
    }

    override fun gte(other: Expression): CoreResult<EBool> {
        if(other !is EInt && other !is EFloat){
            return CoreResult(true, EBool(false), null)
        }
        var otherVal = 0.0
        if(other is EFloat){
            otherVal = other.value
        }
        if(other is EInt){
            otherVal = other.value.toDouble()
        }
        return CoreResult(true, EBool(otherVal >= value), null)
    }
    override fun eval(context: Context): CoreResult<Expression> {
        return evalSuccess(this)
    }

    override fun unparse(): String {
        return value.toString()
    }
}


data class EBool(val value:Boolean):Expression, IComparable {
    override fun eq(other: Expression): CoreResult<EBool> {
        if(other !is EBool){
            return CoreResult(true, EBool(false), null)
        }
        return CoreResult(true, EBool(other.value == value), null)
    }

    override fun neq(other: Expression): CoreResult<EBool> {
        if(other !is EBool){
            return CoreResult(true, EBool(false), null)
        }
        return CoreResult(true, EBool(other.value != value), null)
    }

    override fun eval(context: Context): CoreResult<Expression> {
        return evalSuccess(this)
    }

    override fun unparse(): String {
        return value.toString()
    }
}
data class ESymbol(val name:String):Expression{
    //var evaluated = false

    override fun eval(context: Context): CoreResult<Expression> {

        var currentContext:Context? = context
        while (currentContext != null) {
            if (currentContext.variables.bindings.containsKey(name)) {
                return evalSuccess(currentContext.variables.bindings[name]!!)
            }
            currentContext = currentContext.parent
        }
        return CoreResult<Expression>(false, null,
            VariableNotFoundError(name, "variable '$name' not found in context:\n$context"))
    }
    override fun unparse(): String {
        return name
    }
}

data class EList(val elems: List<Expression>):Expression{
    override fun eval(context: Context): CoreResult<Expression> {

        val res = mutableListOf<Expression>()
        for(e:Expression in elems){
            val eResult = e.eval(context)
            if(!eResult.success){
                return eResult
            }
            res.add(eResult.value!!)
        }
        return evalSuccess(EList(res))

    }

    override fun unparse(): String {
        return "[list, ${elems.joinToString(", ") { it.unparse() }}]"
    }
}

data class EPrimitive(val name:String, val implementation:((List<Expression>, Context) -> CoreResult<Expression>)) : IFunction{
    override fun call(params: List<Expression>, context: Context): CoreResult<Expression> {
       return implementation(params, context)
    }

    override fun eval(context: Context): CoreResult<Expression> {
        return evalSuccess(this)
    }

    override fun unparse(): String {
        return name
    }
}

data class ECall(val func:Expression, val params:List<Expression>) : Expression{
    override fun eval(context: Context): CoreResult<Expression> {
        if(func !is EPrimitive){
            return evalTypeError(func, "function during call should be a function, but got ${func.unparse()}")
        }
        val primitive = func as IFunction

        return func.call(params, context)
    }

    override fun unparse(): String {

        return "[${func.unparse()}, ${params.joinToString(", ") { it.unparse() }}]"
    }

}

