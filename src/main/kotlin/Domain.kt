interface Expression
data class EInt(val value:Int):Expression
data class EMul(val a:EInt, val b:EInt): Expression
data class EAdd(val a:EInt, val b:EInt): Expression

fun eval( expression:Expression): Expression{
    if(expression is EAdd){
        return EInt(expression.a.value + expression.b.value)
    }
    return expression
}
