package sugared

import core.*

interface SugarExpression{
    fun desugar(): Expression
}

data class SugarInt(val v:Int):SugarExpression{
    override fun desugar(): Expression {
        return EInt(v)
    }
}


data class SugarFloat(val v:Double):SugarExpression{
    override fun desugar(): Expression {
        return EFloat(v)
    }
}


data class SugarBool(val v:Boolean):SugarExpression{
    override fun desugar(): Expression {
        return EBool(v)
    }
}


data class SugarAdd(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
       return EBinaryNumericOp("add", left.desugar(), right.desugar())
    }

}

data class SugarFAdd(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return EBinaryFloatOp("fadd", left.desugar(), right.desugar())
    }

}
data class SugarIAdd(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return EIntAdd(left.desugar(), right.desugar())
    }

}
data class SugarMul(val left:SugarExpression, val right:SugarExpression): SugarExpression{
    override fun desugar(): Expression {
       return EBinaryNumericOp("mul", left.desugar(), right.desugar())
    }
}
data class SugarIMul(val left:SugarExpression, val right:SugarExpression): SugarExpression{
    override fun desugar(): Expression {
        return EIntMul(left.desugar(), right.desugar())
    }
}
data class SugarFMul(val left:SugarExpression, val right:SugarExpression): SugarExpression{
    override fun desugar(): Expression {
        return EBinaryFloatOp("fmul",left.desugar(), right.desugar())
    }
}
data class SugarDiv(val left:SugarExpression, val right:SugarExpression): SugarExpression{
    override fun desugar(): Expression {
        return EBinaryNumericOp("div",left.desugar(), right.desugar())
    }
}
data class SugarFDiv(val left:SugarExpression, val right:SugarExpression): SugarExpression{
    override fun desugar(): Expression {
        return EBinaryFloatOp("fdiv", left.desugar(), right.desugar())
    }
}
data class SugarIDiv(val left:SugarExpression, val right:SugarExpression): SugarExpression{
    override fun desugar(): Expression {
        return EIntDiv(left.desugar(), right.desugar())
    }
}
data class SugarSub(val left:SugarExpression, val right: SugarExpression):SugarExpression{
    override fun desugar(): Expression {
       return EBinaryNumericOp("sub",left.desugar(), right.desugar())
    }
}

data class SugarFSub(val left:SugarExpression, val right: SugarExpression):SugarExpression{
    override fun desugar(): Expression {
        return EBinaryFloatOp("fsub", left.desugar(),  right.desugar() )
    }
}
data class SugarISub(val left:SugarExpression, val right: SugarExpression):SugarExpression{
    override fun desugar(): Expression {
        return EIntAdd(left.desugar(),  EIntMul(EInt(-1), right.desugar() ))
    }
}
data class SugarNeg(val value:SugarExpression):SugarExpression{
    override fun desugar(): Expression {
       return EBinaryNumericOp("mul", EInt(-1), value.desugar())
    }

}

data class SugarINeg(val value:SugarExpression):SugarExpression{
    override fun desugar(): Expression {
        return EIntMul(EInt(-1), value.desugar())
    }

}
data class SugarIsInt(val value:SugarExpression):SugarExpression{
    override fun desugar(): Expression {
       return EIsInt(value.desugar())
    }

}


data class SugarIsBool(val value:SugarExpression):SugarExpression{
    override fun desugar(): Expression {
        return EIsBool(value.desugar())
    }

}
data class SugarIsFloat(val value:SugarExpression):SugarExpression{
    override fun desugar(): Expression {
        return EIsFloat(value.desugar())
    }

}
data class SugarSymbol(val name:String):SugarExpression{
    override fun desugar(): Expression {
       return ESymbol(name)
    }

}