import java.util.StringJoiner

interface Expression{
    fun eval():CoreResult<Expression>
    fun substitute(symbol:ESymbol, env:Environment):Expression
    fun unparse():String
}
fun evalSuccess(expression: Expression): CoreResult<Expression>{
    return CoreResult(true, expression, null)
}
data class OperationTypeMismatchError(
    override val input: Any,
    override val message: String,
) : ICoreError
fun evalTypeError(expression: Expression, error:String):CoreResult<Expression>{
    return CoreResult(false, null, OperationTypeMismatchError(expression, error))
}

data class EInt(val value:Int):Expression {
    override fun eval(): CoreResult<Expression> {
        return evalSuccess(this)
    }

    override fun substitute(symbol: ESymbol, env: Environment): Expression {
        return this
    }

    override fun unparse(): String {
        return value.toString()
    }
}

val zeroInt = EInt(0)
data class EMul(val left:Expression, val right:Expression): Expression {
    override fun eval(): CoreResult<Expression> {
        val leftResult = left.eval()
        if(!leftResult.success){
            return leftResult
        }

        if(leftResult.value !is EInt){
            return evalTypeError(left, "type error while evaluating left argument of 'mul'," +
                    " expected an integer value, but got `${leftResult.value!!.unparse()}`")
        }

        val left = leftResult.value!! as EInt
        if (left.value == 0){
            return evalSuccess(zeroInt)
        }

        val rightResult = right.eval()
        if(!rightResult.success){
            return rightResult
        }
        if(rightResult.value !is EInt){
            return evalTypeError(left, "type error while evaluating right argument of 'mul'," +
                    " expected an integer value, but got `${rightResult.value!!.unparse()}`")
        }
        val right = rightResult.value!! as EInt
        return evalSuccess(EInt(left.value * right.value))
    }


    override fun substitute(symbol: ESymbol, env: Environment): Expression {
        return EMul(left.substitute(symbol, env), right.substitute(symbol, env))
    }

    override fun unparse(): String {
        return "[mul, ${left.unparse()}, ${right.unparse()}]".format()
    }
}

data class EAdd(val left:Expression, val right: Expression): Expression {
    override fun eval(): CoreResult<Expression> {
        val leftResult = left.eval();
        if(!leftResult.success){
            return leftResult
        }
        if(leftResult.value !is EInt){
            return evalTypeError(left, "type error while evaluating left argument of 'add'," +
                    " expected an integer value, but got `${leftResult.value!!.unparse()}`")
        }

        val l = leftResult.value!! as EInt

        val rightResult = right.eval()
        if(!rightResult.success){
            return rightResult
        }
        if(rightResult.value !is EInt){
            return evalTypeError(left, "type error while evaluating right argument of 'add'," +
                    " expected an integer value, but got `${rightResult.value!!.unparse()}`")
        }
        val r = rightResult.value!! as EInt
        return evalSuccess( EInt(l.value + r.value))

    }

    override fun substitute(symbol: ESymbol, env: Environment): Expression {
        return EAdd(left.substitute(symbol, env), right.substitute(symbol,env))
    }

    override fun unparse(): String {
        return "[add, ${left.unparse()}, ${right.unparse()}]"
    }
}

 data class ESymbol(val name:String):Expression{
     //var evaluated = false

    override fun eval(): CoreResult<Expression> {

    return evalSuccess(this)
    //return substitute(this )

    }

    override fun substitute(symbol: ESymbol, env: Environment): Expression {
        if(symbol.name == name){

            val initialExpression = env.get(symbol)
       //     if(this.evaluated){
         //       return initialExpression
           // }
            val evaluatedSymbol = initialExpression.eval().value!!
            env.bindings[symbol] = evaluatedSymbol
            //this.evaluated = true
            return evaluatedSymbol
        }
        return this
    }

    override fun unparse(): String {
        return name
    }
}

data class EFunDef(val name:ESymbol, val argument:ESymbol, val body:Expression):Expression{

    override fun eval(): CoreResult<Expression> {
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

    override fun eval(): CoreResult< Expression> {
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