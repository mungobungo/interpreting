import org.yaml.snakeyaml.Yaml
interface ICoreError{
    val input : Any
    val message: String

}

data class UnsupportedPrimitiveError(
    override val input: Any,
    override val message: String,
    ) : ICoreError

data class YamlLoadError(
    override val input: Any,
    override val message: String,

    ) :ICoreError


data class CoreResult<T>(
    val success: Boolean,
    val value: T?,
    val error: ICoreError?

)

fun parserSuccess(expression: SugarExpression) : CoreResult<SugarExpression>{
    return CoreResult(true, expression, null)
}

fun parserFailure(error: ICoreError): CoreResult<SugarExpression>{
    return CoreResult(false, null, error)
}
fun parse(input:String):CoreResult<SugarExpression>{
    val yaml = Yaml()
    return try{
        val script = yaml.load<Any>(input)
        convert(script)
    }catch (e:Exception){
        parserFailure(YamlLoadError(input, e.message!!))
    }
}
fun convert(obj:Any):CoreResult<SugarExpression>{
   if(obj is Int){
       return  parserSuccess( SugarInt(obj))
   }
    if(obj is ArrayList<*>){
        val operation= obj[0]
        if(operation == "add"){
            val left = convert(obj[1])
            if(!left.success){
                return left
            }
            val right = convert(obj[2])
            if(!right.success){
                return right
            }
            return parserSuccess( SugarAdd(left.value!!, right.value!!))
        }
        if(operation =="mul"){
            val left = convert(obj[1])
            if(!left.success){
                return left
            }
            val right = convert(obj[2])
            if(!right.success){
                return right
            }
            return parserSuccess( SugarMul(left.value!!, right.value!!))
        }
        if(operation =="sub"){

            val left = convert(obj[1])
            if(!left.success){
                return left
            }
            val right = convert(obj[2])
            if(!right.success){
                return right
            }
            return parserSuccess( SugarSub(left.value!!, right.value!!))
        }

        if(operation =="neg"){
            val value = convert(obj[1])
            if(!value.success){
                return value
            }
            return parserSuccess(SugarNeg(value.value!!))
        }
    }

    return parserFailure(UnsupportedPrimitiveError( obj,
        "unsupported primitive `$obj` during parsing"))
}