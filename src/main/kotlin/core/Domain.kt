package core

interface Expression{

    fun eval(context: Context):CoreResult<Expression>
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

data class VariableNotFoundError(override val input: Any, override val message: String) :ICoreError
data class Environment(val bindings:HashMap<String, Expression>){
    fun addBinding( name:String, value:Expression){
        bindings[name] = value
    }
    fun isDefined(name:String): Boolean{
        return bindings.containsKey(name)
    }
    fun get(name:String): CoreResult<Expression>{
        if(bindings.containsKey(name)){
            return CoreResult(true, bindings[name]!!, null)
        }
        return CoreResult(false, null, VariableNotFoundError(name, "'$name' is not found in env: \n $this"))
    }
}

data class Context(val variables: Environment, val parent:Context? = null){
    fun expand():Context{
        return Context(Environment(hashMapOf()), this)
    }
    fun clone():Context{
        val res = hashMapOf<String, Expression>()
        res.putAll(variables.bindings)
        return Context(Environment(res), parent)
    }

    override fun toString(): String {

        return variables.bindings.keys.joinToString(",")
    }

}