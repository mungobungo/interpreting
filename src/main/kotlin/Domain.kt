interface Expression
data class EInt(val v:Int):Expression
data class EMul(val a:EInt, val b:EInt): Expression
data class EAdd(val a:EInt, val b:EInt): Expression

fun eval( e:Expression): Expression{
    return e
}
