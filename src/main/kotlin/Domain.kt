interface Expression
data class EInt(val value:Int):Expression
data class EMul(val a:Expression, val b:Expression): Expression
data class EAdd(val a:Expression, val b:Expression): Expression

fun eval( expression:Expression): Expression{
    if(expression is EAdd){
        val left: EInt = eval(expression.a) as EInt
        val right: EInt  = eval(expression.b) as EInt
        return EInt(left.value + right.value)
    }
    if(expression is EMul){
        val left: EInt = eval(expression.a) as EInt
        val right: EInt  = eval(expression.b) as EInt
        return EInt(left.value * right.value)
    }
    return expression
}
