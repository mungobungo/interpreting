package parser

import core.CoreResult
import core.ICoreError
import sugared.*
import org.yaml.snakeyaml.Yaml


data class UnsupportedPrimitiveError(
    override val input: Any,
    override val message: String,
    ) : ICoreError

data class YamlLoadError(
    override val input: Any,
    override val message: String,

    ) :ICoreError


data class UnsupportedBinaryOperation(
    override val input: Any,
    override val message: String,

    ) :ICoreError

fun parserSuccess(expression: SugarExpression) : CoreResult<SugarExpression> {
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

val binaryPrimitives = hashSetOf<String>("mul","imul", "add", "iadd", "div", "idiv", "sub", "isub")
fun convert(obj:Any):CoreResult<SugarExpression>{
   if(obj is Int){
       return  parserSuccess( SugarInt(obj))
   }
    if(obj is Double){
        return parserSuccess(SugarFloat(obj))
    }
    if(obj is Float){
        return parserSuccess(SugarFloat(obj.toDouble()))
    }
    if(obj is Boolean){
        return parserSuccess(SugarBool(obj))
    }

    if(obj is ArrayList<*>){
        val operation= obj[0]
        if(operation is String &&  obj.count() == 3 && operation in binaryPrimitives){
            return parseBinaryAction(operation, obj)
        }

        if(operation =="neg" || operation == "ineg"){
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

private fun parseBinaryAction(operation:String, obj: ArrayList<*>): CoreResult<SugarExpression> {

    val left = convert(obj[1])
    if (!left.success) {
        return left
    }

    val right = convert(obj[2])
    if (!right.success) {
        return right
    }

    val l = left.value!!
    val r = right.value!!
    when(operation){
        "add" -> return parserSuccess(SugarAdd(l,r))
        "iadd"  -> return parserSuccess(SugarIAdd(l,r))
        "mul" -> return parserSuccess(SugarMul(l,r))
        "imul" -> return parserSuccess(SugarIMul(l,r))
        "div" -> return parserSuccess(SugarDiv(l,r))
        "idiv" -> return parserSuccess(SugarIDiv(l,r))
        "sub" -> return parserSuccess(SugarSub(l,r))
        "isub"-> return parserSuccess(SugarISub(l,r))

    }
    return CoreResult(false, null, UnsupportedBinaryOperation(operation, "$operation is not defined as binary operation"))
}