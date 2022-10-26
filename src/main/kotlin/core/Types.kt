package core


data class EInt(val value:Int):Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        return evalSuccess(this)
    }

    override fun unparse(): String {
        return value.toString()
    }
}

data class EFloat(val value:Double):Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        return evalSuccess(this)
    }

    override fun unparse(): String {
        return value.toString()
    }
}


data class EBool(val value:Boolean):Expression {
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

        if(context.variables.bindings.containsKey(name)){
            return evalSuccess(context.variables.bindings[name]!!)
        }
        return CoreResult<Expression>(false, null,
            VariableNotFoundError(name, "variable '$name' not found in context:\n$context"))
    }
    override fun unparse(): String {
        return name
    }
}
