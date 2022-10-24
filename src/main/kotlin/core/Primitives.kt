package core


fun evalBinaryInteger(operationName:String, leftExpression:Expression, rightExpression:Expression,
                      operation:(Int, Int)-> Int):CoreResult<Expression>{

    val leftResult = leftExpression.eval()
    if(!leftResult.success){
        return leftResult
    }

    if(leftResult.value !is EInt){
        return evalTypeError(leftExpression, "type error while evaluating left argument of '${operationName}'," +
                " expected an integer value, but got `${leftResult.value!!.unparse()}`")
    }

    val left = leftResult.value!! as EInt

    val rightResult = rightExpression.eval()
    if(!rightResult.success){
        return rightResult
    }
    if(rightResult.value !is EInt){
        return evalTypeError(left, "type error while evaluating right argument of '${operationName}'," +
                " expected an integer value, but got `${rightResult.value!!.unparse()}`")
    }
    val right = rightResult.value!! as EInt

    return evalSuccess(EInt(operation(left.value, right.value)))
}

data class EIntMul(val left:Expression, val right:Expression): Expression {
    override fun eval(): CoreResult<Expression> {
        return evalBinaryInteger("imul", left, right, { a: Int, b: Int -> a * b })
    }

    override fun unparse(): String {
        return "[imul, ${left.unparse()}, ${right.unparse()}]".format()
    }
}

data class EMul(val left:Expression, val right:Expression): Expression {
    override fun eval(): CoreResult<Expression> {
        return evalBinaryInteger("mul", left, right, { a: Int, b: Int -> a * b })
    }

    override fun unparse(): String {
        return "[mul, ${left.unparse()}, ${right.unparse()}]".format()
    }
}
data class EIntAdd(val left:Expression, val right: Expression): Expression {
    override fun eval(): CoreResult<Expression> {

        return evalBinaryInteger("iadd", left, right, { a: Int, b: Int -> a + b })
    }


    override fun unparse(): String {
        return "[iadd, ${left.unparse()}, ${right.unparse()}]"
    }
}

data class EAdd(val left:Expression, val right: Expression): Expression {
    override fun eval(): CoreResult<Expression> {

        return evalBinaryInteger("add", left, right, { a: Int, b: Int -> a + b })
    }


    override fun unparse(): String {
        return "[add, ${left.unparse()}, ${right.unparse()}]"
    }
}
data class EIntDiv(val left:Expression, val right: Expression): Expression {
    override fun eval(): CoreResult<Expression> {
        val leftResult = left.eval();
        if(!leftResult.success){
            return leftResult
        }
        if(leftResult.value !is EInt){
            return evalTypeError(left, "type error while evaluating left argument of 'idiv'," +
                    " expected an integer value, but got `${leftResult.value!!.unparse()}`")
        }

        val l = leftResult.value!! as EInt

        val rightResult = right.eval()
        if(!rightResult.success){
            return rightResult
        }
        if(rightResult.value !is EInt){
            return evalTypeError(left, "type error while evaluating right argument of 'idiv'," +
                    " expected an integer value, but got `${rightResult.value!!.unparse()}`")
        }
        val r = rightResult.value!! as EInt

        if(r.value == 0){
            return CoreResult(false, null,
                DivisionByZeroError(this, "division by zero")
            )
        }
        return evalSuccess( EInt(l.value / r.value))

    }


    override fun unparse(): String {
        return "[idiv, ${left.unparse()}, ${right.unparse()}]"
    }
}
data class EDiv(val left:Expression, val right: Expression): Expression {
    override fun eval(): CoreResult<Expression> {
        val leftResult = left.eval();
        if(!leftResult.success){
            return leftResult
        }
        if(leftResult.value !is EInt){
            return evalTypeError(left, "type error while evaluating left argument of 'div'," +
                    " expected an integer value, but got `${leftResult.value!!.unparse()}`")
        }

        val l = leftResult.value!! as EInt

        val rightResult = right.eval()
        if(!rightResult.success){
            return rightResult
        }
        if(rightResult.value !is EInt){
            return evalTypeError(left, "type error while evaluating right argument of 'div'," +
                    " expected an integer value, but got `${rightResult.value!!.unparse()}`")
        }
        val r = rightResult.value!! as EInt

        if(r.value == 0){
            return CoreResult(false, null,
                DivisionByZeroError(this, "division by zero")
            )
        }
        return evalSuccess( EInt(l.value / r.value))

    }


    override fun unparse(): String {
        return "[div, ${left.unparse()}, ${right.unparse()}]"
    }
}
