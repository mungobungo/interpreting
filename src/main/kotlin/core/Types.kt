package core


data class EInt(val value:Int):Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        return evalSuccess(this)
    }

    override fun unparse(): String {
        return value.toString()
    }

    override fun type(): StrongType {
       return TInt()
    }
}

data class EFloat(val value:Double):Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        return evalSuccess(this)
    }

    override fun unparse(): String {
        return value.toString()
    }

    override fun type(): StrongType {
       return TFloat()
    }
}


data class EBool(val value:Boolean):Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        return evalSuccess(this)
    }

    override fun unparse(): String {
        return value.toString()
    }

    override fun type(): StrongType {
       return TBool()
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

    override fun type(): StrongType {
       return TInvalidType("esymbol is not supported in typing")
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

    override fun type(): StrongType {
       return TInvalidType("list is not supported")
    }

}
