package core

import java.util.Objects
import kotlin.math.abs

val binaryFloatPrimitives = hashMapOf(
    "fadd" to {a:Double, b:Double -> a +b},
    "add" to {a:Double, b: Double-> a+b},
    "fmul" to {a:Double, b:Double -> a *b},
    "mul" to {a:Double, b:Double -> a*b},
    "fsub" to {a:Double, b:Double -> a -b},
    "sub" to {a:Double, b:Double -> a-b},
    "fdiv" to {a:Double, b:Double -> a /b},
    "div" to {a:Double, b:Double -> a/b},
)
data class EBinaryFloatOp(val operationName:String, val left:Expression, val right: Expression): Expression {
    override fun eval(context:Context): CoreResult<Expression> {

        return evalBinaryFloat(operationName, left, right, context)
    }
    override fun unparse(): String {
        return "[$operationName, ${left.unparse()}, ${right.unparse()}]"
    }
}
fun evalBinaryFloat(operationName:String, leftExpression:Expression, rightExpression:Expression,
                      context: Context):CoreResult<Expression>{

    val leftResult = leftExpression.eval(context)
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EFloat && leftResult.value !is EInt){
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected a float  or integer value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!!

    val rightResult = rightExpression.eval(context)
    if(!rightResult.success){
        return rightResult
    }
    if(rightResult.value !is EInt && rightResult.value !is EFloat){
        return evalTypeError(left, "type error while evaluating right argument of '${operationName}'," +
                " expected a float or integer value, but got `${rightResult.value!!.unparse()}`")
    }
    val right = rightResult.value!!

    val op = binaryFloatPrimitives[operationName]!!
    if(left is EInt && right is EInt){
        return evalSuccess(EFloat(op(left.value.toDouble(), right.value.toDouble())))
    }
    if(left is EInt && right is EFloat){
        return evalSuccess(EFloat(op(left.value.toDouble(), right.value)))
    }

    if(left is EFloat && right is EInt){
        return evalSuccess(EFloat(op(left.value, right.value.toDouble())))
    }

    return evalSuccess(EFloat(op((left as EFloat).value, (right as EFloat).value)))
}

val binaryFloatBoolPrimitives = hashMapOf(
    "lt" to {a:Double, b:Double -> a <b},
    "lte" to {a:Double, b: Double-> a<=b},
    "gt" to {a:Double, b:Double -> a >b},
    "gte" to {a:Double, b:Double -> a>=b},
    "eq" to {a:Double, b:Double -> abs(a -b) <0.0000001 },
    "neq" to {a:Double, b:Double -> abs(a-b) >0.0000001},
)
fun evalBinaryFloatBool(operationName:String, leftExpression:Expression, rightExpression:Expression,
                        context: Context
):CoreResult<Expression>{

    val leftResult = leftExpression.eval(context)
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EFloat && leftResult.value !is EInt){
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected a float  or integer value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!!

    val rightResult = rightExpression.eval(context)
    if(!rightResult.success){
        return rightResult
    }
    if(rightResult.value !is EInt && rightResult.value !is EFloat){
        return evalTypeError(left, "type error while evaluating right argument of '${operationName}'," +
                " expected a float or integer value, but got `${rightResult.value!!.unparse()}`")
    }
    val right = rightResult.value!!

    val op = binaryFloatBoolPrimitives[operationName]!!
    if(left is EInt && right is EInt){
        return evalSuccess(EBool(op(left.value.toDouble(), right.value.toDouble())))
    }
    if(left is EInt && right is EFloat){
        return evalSuccess(EBool(op(left.value.toDouble(), right.value)))
    }

    if(left is EFloat && right is EInt){
        return evalSuccess(EBool(op(left.value, right.value.toDouble())))
    }

    return evalSuccess(EBool(op((left as EFloat).value, (right as EFloat).value)))
}



val binaryIntPrimitives = hashMapOf(
    "iadd" to {a:Int, b:Int -> a +b},
    "add" to {a:Int, b:Int -> a+b},
    "imul" to {a:Int, b:Int -> a *b},
    "mul" to {a:Int, b:Int -> a*b},
    "isub" to {a:Int, b:Int -> a -b},
    "sub" to {a:Int, b:Int -> a-b},
    "idiv" to {a:Int, b:Int -> a /b},
    "div" to {a:Int, b:Int -> a/b},
    )
data class EBinaryIntegerOp(val operationName: String, val left:Expression, val right:Expression): Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        return evalBinaryInteger(operationName, left, right,context)
    }

    override fun unparse(): String {
        return "[$operationName, ${left.unparse()}, ${right.unparse()}]".format()
    }
}
fun evalBinaryInteger(operationName:String, leftExpression:Expression, rightExpression:Expression,
                        context:Context
                      ):CoreResult<Expression>{

    val leftResult = leftExpression.eval(context)
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EInt){
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected an integer value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!! as EInt

    val rightResult = rightExpression.eval(context)
    if(!rightResult.success){
        return rightResult
    }
    if(rightResult.value !is EInt){
        return evalTypeError(left, "type error while evaluating right argument of '${operationName}'," +
                " expected an integer value, but got `${rightResult.value!!.unparse()}`")
    }
    val right = rightResult.value!! as EInt

    val op = binaryIntPrimitives[operationName]!!
    if((operationName == "div" || operationName =="idiv")  && right.value == 0){
        return CoreResult(false, null,
            DivisionByZeroError(leftExpression, "division by zero")
        )
    }
    return evalSuccess(EInt(op(left.value, right.value)))
}

val binaryIntBoolPrimitives = hashMapOf(
    "lt" to {a:Int, b:Int -> a < b},
    "lte" to {a:Int, b:Int -> a<=b},
    "gt" to {a:Int, b:Int -> a >b},
    "gte" to {a:Int, b:Int -> a>=b},
    "eq" to {a:Int, b:Int -> a ==b},
    "neq" to {a:Int, b:Int -> a != b},
)
fun evalBinaryIntegerBool(operationName:String, leftExpression:Expression, rightExpression:Expression,
context: Context
                          ):CoreResult<Expression>{

    val leftResult = leftExpression.eval(context)
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EInt){
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected an integer value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!! as EInt

    val rightResult = rightExpression.eval(context)
    if(!rightResult.success){
        return rightResult
    }
    if(rightResult.value !is EInt){
        return evalTypeError(left, "type error while evaluating right argument of '${operationName}'," +
                " expected an integer value, but got `${rightResult.value!!.unparse()}`")
    }
    val right = rightResult.value!! as EInt

    val op = binaryIntBoolPrimitives[operationName]!!
    return evalSuccess(EBool(op(left.value, right.value)))
}
data class EBinaryNumericOp(val operationName:String, val left:Expression, val right:Expression): Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        return evalBinaryNumeric(operationName, left, right,context)
    }

    override fun unparse(): String {
        return "[$operationName, ${left.unparse()}, ${right.unparse()}]".format()
    }
}
fun evalBinaryNumeric(operationName:String, leftExpression:Expression, rightExpression:Expression,
context: Context):CoreResult<Expression>{
    val leftResult = leftExpression.eval(context)
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EFloat && leftResult.value !is EInt) {
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected a numeric value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!!

    val rightResult = rightExpression.eval(context)
    if(!rightResult.success){
        return rightResult
    }
    if(rightResult.value !is EInt && rightResult.value !is EFloat){
        return evalTypeError(left, "type error while evaluating right argument of '${operationName}'," +
                " expected a numeric value, but got `${rightResult.value!!.unparse()}`")
    }
    val right = rightResult.value!!

    if(left is EInt && right is EInt){
        return evalBinaryInteger(operationName, left, right, context)
    }
    if(left is EFloat && right is EInt){
        return evalBinaryFloat(operationName, left, EFloat((right).value.toDouble()), context)
    }
    if(left is EInt && right is EFloat){
        return evalBinaryFloat(operationName, EFloat(left.value.toDouble()), right, context)
    }

    return evalBinaryFloat(operationName, left, right,context)
}


data class EBinaryNumericBoolOp(val operationName:String, val left:Expression, val right:Expression): Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        return evalBinaryNumericBool(operationName, left, right, context)
    }

    override fun unparse(): String {
        return "[$operationName, ${left.unparse()}, ${right.unparse()}]".format()
    }
}
fun evalBinaryNumericBool(operationName:String, leftExpression:Expression, rightExpression:Expression,
context: Context):CoreResult<Expression>{
    val leftResult = leftExpression.eval(context)
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EFloat && leftResult.value !is EInt) {
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected a numeric value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!!

    val rightResult = rightExpression.eval(context)
    if(!rightResult.success){
        return rightResult
    }
    if(rightResult.value !is EInt && rightResult.value !is EFloat){
        return evalTypeError(left, "type error while evaluating right argument of '${operationName}'," +
                " expected a numeric value, but got `${rightResult.value!!.unparse()}`")
    }
    val right = rightResult.value!!

    if(left is EInt && right is EInt){
        return evalBinaryIntegerBool(operationName, left, right, context)
    }
    if(left is EFloat && right is EInt){
        return evalBinaryFloatBool(operationName, left, EFloat((right).value.toDouble()), context)
    }
    if(left is EInt && right is EFloat){
        return evalBinaryFloatBool(operationName, EFloat(left.value.toDouble()), right,context)
    }

    return evalBinaryFloatBool(operationName, left, right, context)
}


data class EIsInt(val v:Expression):Expression{
    override fun eval(context: Context): CoreResult<Expression> {
        return  evalSuccess(EBool( v is EInt))
    }

    override fun unparse(): String {
        return "[is_int, ${v.unparse()}]"
    }

}


data class EIsFloat(val v:Expression):Expression{
    override fun eval(context: Context): CoreResult<Expression> {
        return  evalSuccess(EBool( v is EFloat))
    }

    override fun unparse(): String {
        return "[is_float, ${v.unparse()}]"
    }

}
data class EIsBool(val v:Expression):Expression{
    override fun eval(context: Context): CoreResult<Expression> {
        return  evalSuccess(EBool( v is EBool))
    }

    override fun unparse(): String {
        return "[is_bool, ${v.unparse()}]"
    }

}
data class ENot(val v:Expression):Expression{
    override fun eval(context: Context): CoreResult<Expression> {
        val evaluated = v.eval(context)
        if(!evaluated.success){
            return evaluated
        }
        val res = evaluated.value!!
        if(res !is EBool){

            return evalTypeError(v, "type error while evaluating  argument of 'not'," +
                    " expected a boolean value, but got `${res.unparse()}`")
        }
        return  evalSuccess(EBool( !res.value))
    }

    override fun unparse(): String {
        return "[not, ${v.unparse()}]"
    }

}
val binaryBoolPrimitives = hashMapOf(
    "and" to {a:Boolean, b:Boolean -> a &&b},
    "or" to {a:Boolean, b:Boolean -> a||b},
    "xor" to {a:Boolean, b:Boolean -> a xor b},
)
data class EBinaryBoolOp(val operationName: String, val left:Expression, val right:Expression): Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        return evalBinaryBool(operationName, left, right, context)
    }

    override fun unparse(): String {
        return "[$operationName, ${left.unparse()}, ${right.unparse()}]".format()
    }
}
fun evalBinaryBool(operationName:String, leftExpression:Expression, rightExpression:Expression,
context: Context):CoreResult<Expression>{

    val leftResult = leftExpression.eval(context)
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EBool){
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected a boolean value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!! as EBool

    val rightResult = rightExpression.eval(context)
    if(!rightResult.success){
        return rightResult
    }
    if(rightResult.value !is EBool){
        return evalTypeError(left, "type error while evaluating right argument of '${operationName}'," +
                " expected a boolean value, but got `${rightResult.value!!.unparse()}`")
    }
    val right = rightResult.value!! as EBool

    val op = binaryBoolPrimitives[operationName]!!
    return evalSuccess(EBool(op(left.value, right.value)))
}

data class ESetVar(val name:String, val variableValue:Expression):Expression{
    override fun eval(context: Context): CoreResult<Expression>{
        val valueResult = variableValue.eval(context)
        if(!valueResult.success){
            return valueResult
        }
        context.variables.bindings[name] = valueResult.value!!
        return evalSuccess( valueResult.value!!)
    }

    override fun unparse(): String {
        return "[setvar, $name, ${variableValue.unparse()}]"
    }

}
data class EDo(val expressions:List<Expression>): Expression{
    override fun eval(context: Context): CoreResult<Expression> {
        val localContext = context.expand()
        val res = EList(expressions).eval(localContext)
        if(!res.success){
            return res
        }
        val computed = res.value as EList
        return evalSuccess( computed.elems.last())
    }

    override fun unparse(): String {

        return "[do, ${expressions.joinToString(", ") { it.unparse() }}]"
    }
}



data class ELambdaRef(val argumentNames:List<String>, val body: List<Expression>, val closure:Context): Expression{
    override fun eval(context: Context): CoreResult<Expression> {
        return evalSuccess(this)
    }

    override fun unparse(): String {
        return "[lambda_ref, ${argumentNames.joinToString(",")}, ...}]"
    }

}
data class ELambdaDefinition(val argumentNames: List<String>, val body:List<Expression>):Expression{
    override fun eval(context: Context): CoreResult<Expression> {
       return evalSuccess( ELambdaRef(argumentNames, body, context.clone()))
    }

    override fun unparse(): String {
        return "[lambda , [${argumentNames.joinToString(",") }], ${body.joinToString(","){it.unparse()}}]"
    }
}

data class ECall(val func:Expression, val args:List<Expression>):Expression{
    override fun eval(context: Context): CoreResult<Expression> {
        val ff = func.eval(context)
        if(!ff.success){
            return ff
        }
        if(ff.value !is ELambdaRef){
            return evalTypeError(this, "call error: function argument should be lambda ref ,but got ${ff.value!!.unparse()}")
        }
        val f = ff.value
        if(f.argumentNames.size != args.size){
            return evalTypeError(this, "call error: mismatch number of parameters, expected ${f.argumentNames.size}, but gon ${args.size} in \n${func.unparse()}")
        }
        val newContext = f.closure
        for( arg in f.argumentNames.zip(args)){
            val argValue =  arg.second.eval(context) //context.variables.get(f.variableName)
            if(! argValue.success){
                return evalTypeError(this, "argument ${arg.first} cannot be evaluated during the call of ")
            }
            newContext.variables.bindings[arg.first] = argValue.value!!
        }


        val res = mutableListOf<Expression>()
        for(e:Expression in  f.body){
            val eResult = e.eval(newContext)
            if(!eResult.success){
                return eResult
            }
            res.add(eResult.value!!)
        }
        return evalSuccess(res.last())

    }

    override fun unparse(): String {
        return "[call, ${func.unparse()}, [${args.joinToString(","){it.unparse()}}]]"
    }

}

data class EIf(val condition:Expression, val mainBranch: Expression, val alternativeBranch:Expression):Expression{
    override fun eval(context: Context): CoreResult<Expression> {
        val cond = condition.eval(context)
        if(!cond.success){
            return evalTypeError(this, "'if' cannot evaluate condition, ${condition.unparse()}")
        }
        if(cond.value !is EBool){
            return evalTypeError(this, "'if' condition should be boolean, but got ${cond.value}")
        }
        if(cond.value.value){
            val evaluatedMain = mainBranch.eval(context)
            if(!evaluatedMain.success){
                return evaluatedMain
            }
            return  evalSuccess(evaluatedMain.value!!)
        }else{
            val evaluatedAlternative = alternativeBranch.eval(context)
            if(!evaluatedAlternative.success){
                return evaluatedAlternative
            }
            return evalSuccess(evaluatedAlternative.value!!)
        }
    }

    override fun unparse(): String {
        return "[if, ${mainBranch.unparse()}, ${alternativeBranch.unparse()}]"
    }

}


data class EFunRecDefinition(val name:String,
                             val argumentNames: List<String>, val body:List<Expression>):Expression{
    override fun eval(context: Context): CoreResult<Expression> {
        val c = context.clone()
        val lambdaRef = ELambdaRef(argumentNames, body, c)
        c.variables.bindings[name] = lambdaRef

        return ESetVar(name, lambdaRef).eval(context)
    }

    override fun unparse(): String {
        return "[lambda , [${argumentNames.joinToString(",") }], ${body.joinToString(","){it.unparse()}}]"
    }
}

data class EDict(val values:HashMap<Expression, Expression>): Expression{
    override fun eval(context: Context): CoreResult<Expression> {
       return evalSuccess(this)
    }

    override fun unparse(): String{
        val items = values.entries.joinToString(", ") {  it.key.unparse() +":" + it.value.unparse()  }
        return "{$items}"
    }

}

data class EGet(val key: Expression, val obj: Expression) :Expression{
    override fun eval(context: Context): CoreResult<Expression> {


       val objv = obj.eval(context)
       if(!objv.success){
           return objv
       }
        if(objv.value !is EDict){
            return evalTypeError(this, "'get' accepts dictionary as second arugment, but got ${obj.unparse()}")
        }
        val dic = objv.value.values
        if(!dic.containsKey(key)){
            return evalTypeError(this, "'get' key ${key.unparse()}  is not found in ${objv.value.unparse()}")
        }
        val obj = dic[key]!!
        val evaluated = obj.eval(context)
        return evaluated
    }

    override fun unparse(): String {
        return "[get, $obj,  $key]"
    }

}

//[lambda, m, [add, m , 1]]
// [call, [lambda, m, [add, m, 1]], 100]
//[setvar, inc1, [lambda, m , [add, m, 1]]]
// [call, inc1, 12]
// [let, x, 10, [let, f, [lambda, z, [sub, x, z]], [call, f, 15]]]
// >> [let, x, 200, [let, f, [lambda, z, [sub, z, x]], [let, x, 100, [let, g, [lambda, z, [sub, z, x]], [sub, [call, f, 1], [call, g, 1]]]]]]
//-100
//parser: 3.377893ms, eval: 0.194912ms

// [setvar, mk_counter, [lambda, initial, [lambda, _, [do, [setvar, initial, [add, initial,1]], initial]]]]



//[setvar, mk_counter, [lambda, cnt, [lambda, _, [setvar, cnt, [add, cnt, 1]], cnt]]]
// [setvar, c1, [call, mk_counter, 15]]
// [call, c1, 10]
// [call, c1, 0]

// what if we create new counter?
//>> [setvar, c3, [call, mk_counter, 9]]
//[lambda_ref, _, [setvar, cnt, [add, cnt, 1]],cnt]
//parser: 2.908809ms, eval: 0.03952ms
//>> [call, c3, 0]
//10
//parser: 0.694687ms, eval: 0.033879ms
//>> [call, c1, 11]
//11

// intro into capturing variables
// [setvar, x, 10]
// [setvar, f1, [lambda, a, [add, a, x]]]
// [call, f1, 10]
// [call, f1, 15]
// [setvar, x, 111111]
// [call, f1, 15]
// [setvar, f2, [lambda, a, [add, a, x]]]
// [call, f2, 0]

// calls with multiple params
// [call, [lambda, [], 1], []]
// [call, [lambda, [x], x], [100]]
// [call, [lambda, [x], [add, x, 10]], [222]]
// [call, [lambda, [x, y], [add, x, y]], [2, 3]]
// [call, [lambda, [x,y,z], [add, x, y]], [2,3,100]]

// broken factorial
// [fun, fac, [x], [if, [eq, x, 1], 1, [mul, x, [call, fac, [[sub, x, 1]]]]]]

// working factorial
// [funrec, fac, [x], [if, [eq, x, 1], 1, [mul, x, [call, fac, [[sub, x, 1]]]]]]

//[funrec, odd, [x], [if, [eq, 1, x], true, [call, even, [sub, x, 1]]]]
//[funrec, even, [x], [if, [eq, 0, x], true, [call, odd, [sub, x, 1]]]]
