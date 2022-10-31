package parser

import core.*
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

data class ParsingError(override val input: Any, override val message: String) :ICoreError
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
val unaryPrimitives = hashSetOf<String>("neg", "ineg", "is_int", "is_bool", "is_float", "not")
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
    if(obj is String){
        return parserSuccess(SugarSymbol(obj))
    }

    if(obj is ArrayList<*>){
        val operation= obj[0]
        if(operation == "ones"){
            return parserSuccess(SugarOnes(obj[1] as Int))
        }
        if(operation =="list"){
            val elems = obj.takeLast(obj.size -1)
            val expressions = elems.map { convert(it) }
            if(expressions.any{ !it.success }){
                val broken = expressions.filter { !it.success }.map { it.error!! }.joinToString { "\n" }
                return parserFailure(ParsingError(obj, "error during parsing 'list' $obj\n $broken" ))
            }
            return parserSuccess( SugarList(expressions.map { it.value!! }))
        }
        if(operation =="do"){
            val elems = obj.takeLast(obj.size -1)
            val expressions = elems.map { convert(it) }
            if(expressions.any{ !it.success }){
                val broken = expressions.filter { !it.success }.map { it.error!! }.joinToString { "\n" }
                return parserFailure(ParsingError(obj, "error during parsing 'do' $obj\n $broken" ))
            }
            return parserSuccess( SugarDo(expressions.map { it.value!! }))
        }
        if(operation == "let"){
            if(obj.size != 4){
                return parserFailure(ParsingError(obj, "incorrect number of parameters for 'let', \n expected 3, got ${obj.size}  in \n$obj"))
            }
            val name = obj[1]

            if(name !is String){
                return parserFailure(ParsingError(name, "name inside of 'let' should be a string, got \n $name"))
            }
            val value= convert(obj[2])
            if(!value.success){
                return value
            }
            val body = convert(obj[3])
            if(!body.success){
                return body
            }
            return parserSuccess(SugarLet(name, value.value!!, body.value!!))
        }
        if(operation == "let*"){
            if(obj.size <4 ){

                return parserFailure(ParsingError(obj, "incorrect number of parameters for 'let*', \n expected at least one binding  in \n$obj"))
            }
            if(obj.size %2 != 0){
                return parserFailure(ParsingError(obj, "incorrect number of parameters for 'let*', \n expected even number of bindings, got ${obj.size}  in \n$obj"))
            }

            val bindings = mutableListOf<Pair<String, SugarExpression>>()
            for(i in 0 .. -1+(obj.size-2)/2){

                val name = obj[i*2+1]

                if(name !is String){
                    return parserFailure(ParsingError(name, "name inside of 'let*' should be a string, got \n $name"))
                }
                val value= convert(obj[i*2+2])
                if(!value.success){
                    return value
                }
                bindings.add(Pair(name, value.value!!))
            }
            val body = convert(obj.last())
            if(!body.success){
                return body
            }
            return parserSuccess(SugarLetStar(bindings, body.value!!))
        }
        if(operation is String && operation in primitives){

            val elems = obj.takeLast(obj.size -1)
            val expressions = elems.map { convert(it) }
            if(expressions.any{ !it.success }){
                val broken = expressions.filter { !it.success }.map { it.error!! }.joinToString { "\n" }
                return parserFailure(ParsingError(obj, "error during parsing $operation $obj\n $broken" ))
            }
            return parserSuccess(SugarPrimitive(operation, expressions.map { it.value!!.desugar() }))
        }
        if(operation is String &&  obj.count() == 3 && (
                    operation in binaryFloatBoolPrimitives
                    || operation in binaryIntBoolPrimitives
                    || operation in binaryBoolPrimitives
                    || operation == "setvar")){
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
        "fadd"  -> return parserSuccess(SugarFAdd(l,r))
        "fmul" -> return parserSuccess(SugarFMul(l,r))
        "fdiv"  -> return parserSuccess(SugarFDiv(l,r))
        "fsub"  -> return parserSuccess(SugarFSub(l,r))
        "lt" -> return parserSuccess(SugarNumericLt(l,r))
        "lte" -> return parserSuccess(SugarNumericLte(l,r))
        "gt" -> return parserSuccess(SugarNumericGt(l,r))
        "gte" -> return parserSuccess(SugarNumericGte(l,r))
        "eq" -> return parserSuccess(SugarNumericEq(l,r))
        "neq" -> return parserSuccess(SugarNumericNeq(l,r))
        "and" -> return parserSuccess(SugarAnd(l,r))
        "or" -> return parserSuccess(SugarOr(l,r))
        "xor" -> return parserSuccess(SugarXor(l,r))
    }
    if(operation == "setvar"){
    if(l !is SugarSymbol){
     return CoreResult(false, null, ParsingError(l, "setvar expects a string as variable name, but got \n $l"))
    }
    return parserSuccess(SugarSetVar((l as SugarSymbol).name, r))
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
        "is_int" -> return parserSuccess(SugarIsInt(v))
        "is_bool" -> return parserSuccess(SugarIsBool(v))
        "is_float" -> return parserSuccess(SugarIsFloat(v))
        "not" -> return parserSuccess(SugarNot(v))
    }
    return CoreResult(false, null, UnsupportedUnaryOperation(op, "$operation is not not defined as unary operation"))
}