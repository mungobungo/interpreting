package core


data class EInt(val value:Int):Expression, IComparable, IOrdered {
    override fun eq(other: Expression, context: Context): CoreResult<EBool> {

       if(other !is EInt && other !is EFloat){
           return CoreResult(true, context,EBool(false), null)
       }
       var otherVal = 0
       if(other is EFloat){
           otherVal = other.value.toInt()
       }
        if(other is EInt){
            otherVal = other.value
        }
        return CoreResult(true,context, EBool(otherVal == value), null)
    }

    override fun neq(other: Expression, context: Context): CoreResult<EBool> {

        if(other !is EInt && other !is EFloat){
            return CoreResult(true,context, EBool(false), null)
        }
        var otherVal = 0
        if(other is EFloat){
            otherVal = other.value.toInt()
        }
        if(other is EInt){
            otherVal = other.value
        }
        return CoreResult(true,context, EBool(otherVal != value), null)
    }

    override fun lt(other: Expression, context: Context): CoreResult<EBool> {
        if(other !is EInt && other !is EFloat){
            return CoreResult(true, context, EBool(false), null)
        }
        var otherVal = 0
        if(other is EFloat){
            otherVal = other.value.toInt()
        }
        if(other is EInt){
            otherVal = other.value
        }
        return CoreResult(true,context,  EBool( value < otherVal), null)
    }

    override fun lte(other: Expression, context: Context): CoreResult<EBool> {
        if(other !is EInt && other !is EFloat){
            return CoreResult(true, context, EBool(false), null)
        }
        var otherVal = 0
        if(other is EFloat){
            otherVal = other.value.toInt()
        }
        if(other is EInt){
            otherVal = other.value
        }
        return CoreResult(true,context, EBool(value <= otherVal), null)
    }

    override fun gt(other: Expression, context: Context): CoreResult<EBool> {
        if(other !is EInt && other !is EFloat){
            return CoreResult(true,context, EBool(false), null)
        }
        var otherVal = 0
        if(other is EFloat){
            otherVal = other.value.toInt()
        }
        if(other is EInt){
            otherVal = other.value
        }
        return CoreResult(true,context, EBool( value > otherVal), null)
    }

    override fun gte(other: Expression, context: Context): CoreResult<EBool> {
        if(other !is EInt && other !is EFloat){
            return CoreResult(true, context, EBool(false), null)
        }
        var otherVal = 0
        if(other is EFloat){
            otherVal = other.value.toInt()
        }
        if(other is EInt){
            otherVal = other.value
        }
        return CoreResult(true,context,  EBool( value >=  otherVal), null)
    }

    override fun eval(context: Context): CoreResult<Expression> {
        return evalSuccess(this, context)

    }

    override fun unparse(): String {
        return value.toString()
    }
}

data class EFloat(val value:Double):Expression, IOrdered, IComparable {
    override fun eq(other: Expression, context: Context): CoreResult<EBool> {

        if(other !is EInt && other !is EFloat){
            return CoreResult(true,context, EBool(false), null)
        }
        var otherVal = 0.0
        if(other is EFloat){
            otherVal = other.value
        }
        if(other is EInt){
            otherVal = other.value.toDouble()
        }
        return CoreResult(true,context, EBool(otherVal == value), null)
    }

    override fun neq(other: Expression, context: Context): CoreResult<EBool> {

        if(other !is EInt && other !is EFloat){
            return CoreResult(true,context, EBool(false), null)
        }
        var otherVal = 0.0
        if(other is EFloat){
            otherVal = other.value
        }
        if(other is EInt){
            otherVal = other.value.toDouble()
        }
        return CoreResult(true,context, EBool(otherVal != value), null)
    }

    override fun lt(other: Expression,context: Context): CoreResult<EBool> {
        if(other !is EInt && other !is EFloat){
            return CoreResult(true,context, EBool(false), null)
        }
        var otherVal = 0.0
        if(other is EFloat){
            otherVal = other.value
        }
        if(other is EInt){
            otherVal = other.value.toDouble()
        }
        return CoreResult(true,context, EBool( value < otherVal), null)
    }

    override fun lte(other: Expression, context: Context): CoreResult<EBool> {
        if(other !is EInt && other !is EFloat){
            return CoreResult(true, context, EBool(false), null)
        }
        var otherVal = 0.0
        if(other is EFloat){
            otherVal = other.value
        }
        if(other is EInt){
            otherVal = other.value.toDouble()
        }
        return CoreResult(true,context, EBool( value <= otherVal), null)
    }

    override fun gt(other: Expression, context: Context): CoreResult<EBool> {
        if(other !is EInt && other !is EFloat){
            return CoreResult(true,context,  EBool(false), null)
        }
        var otherVal = 0.0
        if(other is EFloat){
            otherVal = other.value
        }
        if(other is EInt){
            otherVal = other.value.toDouble()
        }
        return CoreResult(true,context, EBool( value > otherVal), null)
    }

    override fun gte(other: Expression, context: Context): CoreResult<EBool> {
        if(other !is EInt && other !is EFloat){
            return CoreResult(true,context, EBool(false), null)
        }
        var otherVal = 0.0
        if(other is EFloat){
            otherVal = other.value
        }
        if(other is EInt){
            otherVal = other.value.toDouble()
        }
        return CoreResult(true,context, EBool( value>=otherVal), null)
    }
    override fun eval(context: Context): CoreResult<Expression> {
        return evalSuccess(this, context)
    }

    override fun unparse(): String {
        return value.toString()
    }
}


data class EBool(val value:Boolean):Expression, IComparable {
    override fun eq(other: Expression, context: Context): CoreResult<EBool> {
        if(other !is EBool){
            return CoreResult(true,context, EBool(false), null)
        }
        return CoreResult(true,context, EBool(other.value == value), null)
    }

    override fun neq(other: Expression, context: Context): CoreResult<EBool> {
        if(other !is EBool){
            return CoreResult(true,context, EBool(false), null)
        }
        return CoreResult(true,context, EBool(other.value != value), null)
    }

    override fun eval(context: Context): CoreResult<Expression> {
        return evalSuccess(this, context)
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
                return evalSuccess(currentContext.variables.bindings[name]!!, context)
            }
            currentContext = currentContext.parent
        }
        return CoreResult<Expression>(false,context, null,
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
        return evalSuccess(EList(res),context)

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
        return evalSuccess(this, context)
    }

    override fun unparse(): String {
        return name
    }

    override fun toString(): String {
        return "$name::primitive"
    }
}

data class ECall(val func:Expression, val params:List<Expression>) : Expression{
    override fun eval(context: Context): CoreResult<Expression> {
        var funcRef = func
        if(func is ESymbol){ // if it is a symbol, we would try to find it in the env
            val f = func.eval(context)
            if(!f.success){
                return evalTypeError(func, context,"function $func is not found in the context, $context")
            }
            funcRef = f.value!!
        }
        if(funcRef !is EPrimitive && funcRef !is ELambda){
            return evalTypeError(funcRef,  context,"function during call should be a primitive or a lambda, but got ${funcRef.unparse()}")
        }


        return (funcRef as IFunction).call(params, context)
    }

    override fun unparse(): String {

        return "[${func.unparse()}, ${params.joinToString(", ") { it.unparse() }}]"
    }

}

data class ELambda(val paramNames: List<String>, val body:Expression, val closure:Context) : Expression, IFunction{
    override fun call(params: List<Expression>, context: Context): CoreResult<Expression> {
        if(params.size != paramNames.size){
            return evalArgumentCountError(params, context,"argument count does not match number of parameters, ${params.size} != ${paramNames.size} in ${this.unparse()}")
        }
        val expandedContext = context.clone()
        for(v in paramNames.zip(params)){

            expandedContext.variables.addBinding(v.first, v.second)
        }

        return  body.eval(expandedContext)
    }

    override fun eval(context: Context): CoreResult<Expression> {
        return evalSuccess(ELambda(paramNames, body, context), context
        )
    }

    override fun unparse(): String {
        return "[lambda, ${paramNames.joinToString(",") { it }}, ${body.unparse()}]"
    }

}

