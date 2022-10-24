package parser

import core.CoreResult
import core.ICoreError
import core.binaryFloatPrimitives
import core.binaryIntPrimitives
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
data class UnsupportedUnaryOperation(
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
val unaryPrimitives = hashSetOf<String>("neg", "ineg", "is_int", "is_bool", "is_float")
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
        if(operation is String &&  obj.count() == 3 && (operation in binaryIntPrimitives.keys || operation in binaryFloatPrimitives)){
            return parseBinaryAction(operation, obj)
        }

        if(operation is String && obj.count() == 2 && operation in unaryPrimitives){
            return parseUnaryAction(operation, obj)
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
        "fadd"  -> return parserSuccess(SugarFAdd(l,r))
        "mul" -> return parserSuccess(SugarMul(l,r))
        "imul" -> return parserSuccess(SugarIMul(l,r))
        "fmul" -> return parserSuccess(SugarFMul(l,r))
        "div" -> return parserSuccess(SugarDiv(l,r))
        "idiv" -> return parserSuccess(SugarIDiv(l,r))
        "sub" -> return parserSuccess(SugarSub(l,r))
        "isub"-> return parserSuccess(SugarISub(l,r))

    }
    return CoreResult(false, null, UnsupportedBinaryOperation(operation, "$operation is not defined as binary operation"))
}

private fun parseUnaryAction(operation:String, obj: ArrayList<*>): CoreResult<SugarExpression>{
    val op = convert(obj[1])
    if(!op.success){
        return op
    }

    val v = op.value!!
    when(operation){
        "neg" -> return parserSuccess(SugarNeg(v))
        "ineg" -> return parserSuccess(SugarINeg(v))
        "is_int" -> return parserSuccess(SugarIsInt(v))
        "is_bool" -> return parserSuccess(SugarIsBool(v))
        "is_float" -> return parserSuccess(SugarIsFloat(v))
    }
    return CoreResult(false, null, UnsupportedUnaryOperation(op, "$operation is not not defined as unary operation"))
}