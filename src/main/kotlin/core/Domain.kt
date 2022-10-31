package core

import javax.swing.Icon

interface Expression{
    fun eval(context: Context):CoreResult<Expression>
    fun unparse():String
}

interface IFunction : Expression{
    fun call(params:List<Expression>, context: Context): CoreResult<Expression>
}

interface IComparable: Expression{
    fun eq(other:Expression) : CoreResult<EBool>
    fun neq(other:Expression) : CoreResult<EBool>
}

interface IOrdered : Expression{
    fun lt(other: Expression) : CoreResult<EBool>
    fun lte(other: Expression) : CoreResult<EBool>
    fun gt(other:Expression) :CoreResult<EBool>
    fun gte(other:Expression) :CoreResult<EBool>
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

}


val primitiveFunctions: HashMap<String, Expression> = hashMapOf(
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
val defaultContext = Context(Environment( primitiveFunctions), null)