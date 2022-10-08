interface Expression{
    fun eval(env:Environment):Expression
    fun substitute(symbol:ESymbol, env:Environment):Expression
    fun unparse():String
}
data class EInt(val value:Int):Expression {
    override fun eval(env:Environment): Expression {
        return this
    }

    override fun substitute(symbol: ESymbol, env: Environment): Expression {
        return this
    }

    override fun unparse(): String {
        return value.toString()
    }
}

val zeroInt = EInt(0)
data class EMul(val a:Expression, val b:Expression): Expression {
    override fun eval(env:Environment): Expression {
        val left = a.eval(env) as EInt
        if (left.value == 0){
            return zeroInt
        }
        val right = b.eval(env) as EInt
        return EInt(left.value * right.value)
    }


    override fun substitute(symbol: ESymbol, env: Environment): Expression {
        return EMul(a.substitute(symbol, env), b.substitute(symbol, env))
    }

    override fun unparse(): String {
        return "[mul, ${a.unparse()}, ${b.unparse()}]".format()
    }
}

data class EAdd(val a:Expression, val b:Expression): Expression {
    override fun eval(env:Environment): Expression {
        val leftExpression = a.eval(env)
        val left = leftExpression as EInt
        val right = b.eval(env) as EInt
        return EInt(left.value + right.value)

    }

    override fun substitute(symbol: ESymbol, env: Environment): Expression {
        return EAdd(a.substitute(symbol, env), b.substitute(symbol,env))
    }

    override fun unparse(): String {
        return "[add, ${a.unparse()}, ${b.unparse()}]"
    }
}

 data class ESymbol(val name:String):Expression{
     var evaluated = false

    override fun eval(env: Environment): Expression {
        return substitute(this, env)
    }

    override fun substitute(symbol: ESymbol, env: Environment): Expression {
        if(symbol.name == name){

            val initialExpression = env.get(symbol)
            if(this.evaluated){
                return initialExpression
            }
            val evaluatedSymbol = initialExpression.eval(env)
            env.bindings[symbol] = evaluatedSymbol
            this.evaluated = true
            return evaluatedSymbol
        }
        return this
    }

    override fun unparse(): String {
        return name
    }
}

data class EFunDef(val name:ESymbol, val argument:ESymbol, val body:Expression):Expression{

    override fun eval(env: Environment): Expression {
        TODO("Not yet implemented")
    }

    override fun substitute(symbol: ESymbol, env: Environment): Expression {
        TODO("Not yet implemented")
    }

    override fun unparse(): String {
        return "[fun, [${name.unparse()}, ${argument.unparse()}], ${body.unparse()}]"
    }
}

data class EFunCall(val name:ESymbol, val argument:Expression):Expression{

    override fun eval(env: Environment): Expression {
        TODO("Not yet implemented")
    }

    override fun substitute(symbol: ESymbol, env: Environment): Expression {
        TODO("Not yet implemented")
    }

    override fun unparse(): String {
        return "[${name.unparse()}, ${argument.unparse()}]"
    }

}

data class Environment(val bindings:HashMap<ESymbol, Expression>){
    fun addBinding( name:ESymbol,  value:Expression){
        bindings[name] = value
    }
    fun isDefined(name:ESymbol): Boolean{
        return bindings.containsKey(name)
    }
    fun get(name:ESymbol):Expression{
        return bindings[name]!!
    }
}