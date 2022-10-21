import org.yaml.snakeyaml.Yaml
interface IParserError{
    val input : Any
    val message: String
    val parent: IParserError?
}

data class UnsupportedExpression(
    override val input: Any,
    override val message: String,
    override val parent: IParserError?,
    ) : IParserError

data class YamlLoadError(
    override val input: Any,
    override val message: String,
    override val parent: IParserError?
    ) :IParserError


data class ParserResult(
    val success: Boolean,
    val expression: SugarExpression?,
    val error: IParserError? = null
)

fun parserSuccess(expression: SugarExpression) : ParserResult{
    return ParserResult(true, expression, null)
}

fun parserFailure(error: IParserError): ParserResult{
    return ParserResult(false, null, error)
}
fun parse(input:String):ParserResult{
    val yaml = Yaml()
    return try{
        val script = yaml.load<Any>(input)
        convert(script)
    }catch (e:Exception){
        parserFailure(YamlLoadError(input, e.message!!, null))
    }
}
fun convert(obj:Any):ParserResult{
   if(obj is Int){
       return  parserSuccess( SugarInt(obj))
   }
    if(obj is String){
        return parserSuccess(SugarSymbol(obj))
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
            return parserSuccess( SugarAdd(left.expression!!, right.expression!!))
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
            return parserSuccess( SugarMul(left.expression!!, right.expression!!))
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
            return parserSuccess( SugarSub(left.expression!!, right.expression!!))
        }

        if(operation =="neg"){
            val value = convert(obj[1])
            if(!value.success){
                return value
            }
            return parserSuccess(SugarNeg(value.expression!!))
        }
    }

    return parserFailure(UnsupportedExpression( obj,
        "unsupported object `$obj` during parsing",null))
}