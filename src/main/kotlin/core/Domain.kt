package core

interface Expression{
    fun eval():CoreResult<Expression>
    fun unparse():String
}


data class CoreResult<T>(
    val success: Boolean,
    val value: T?,
    val error: ICoreError?

)


interface ICoreError{
    val input : Any
    val message: String

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