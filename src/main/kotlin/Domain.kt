interface Expression{
    fun eval():Expression
}
data class EInt(val value:Int):Expression {
    override fun eval(): Expression {
        return this
    }
}

val zeroInt = EInt(0)
data class EMul(val a:Expression, val b:Expression): Expression {
    override fun eval(): Expression {
        val left = a.eval() as EInt
        if (left.value == 0){
            return zeroInt
        }
        val right = b.eval() as EInt
        return EInt(left.value * right.value)
    }
}

data class EAdd(val a:Expression, val b:Expression): Expression {
    override fun eval(): Expression {
        val left = a.eval() as EInt
        val right = b.eval() as EInt
        return EInt(left.value + right.value)

    }
}

