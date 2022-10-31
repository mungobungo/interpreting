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


data class SugarFAdd(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return EBinaryFloatOp("fadd", left.desugar(), right.desugar())
    }
}
data class SugarFMul(val left:SugarExpression, val right:SugarExpression): SugarExpression{
    override fun desugar(): Expression {
        return EBinaryFloatOp("fmul",left.desugar(), right.desugar())
    }
}
data class SugarFDiv(val left:SugarExpression, val right:SugarExpression): SugarExpression{
    override fun desugar(): Expression {
        return EBinaryFloatOp("fdiv", left.desugar(), right.desugar())
    }
}

data class SugarFSub(val left:SugarExpression, val right: SugarExpression):SugarExpression{
    override fun desugar(): Expression {
        return EBinaryFloatOp("fsub", left.desugar(),  right.desugar() )
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
data class SugarNumericLt(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return EBinaryNumericBoolOp("lt",left.desugar(), right.desugar())
    }
}
data class SugarNumericLte(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return EBinaryNumericBoolOp("lte",left.desugar(), right.desugar())
    }
}


data class SugarNumericGt(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return EBinaryNumericBoolOp("gt",left.desugar(), right.desugar())
    }
}
data class SugarNumericGte(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return EBinaryNumericBoolOp("gte",left.desugar(), right.desugar())
    }
}
data class SugarNumericEq(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return EBinaryNumericBoolOp("eq",left.desugar(), right.desugar())
    }
}


data class SugarNumericNeq(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return EBinaryNumericBoolOp("neq",left.desugar(), right.desugar())
    }
}
data class SugarAnd(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return EBinaryBoolOp("and",left.desugar(), right.desugar())
    }
}
data class SugarOr(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return EBinaryBoolOp("or",left.desugar(), right.desugar())
    }
}
data class SugarXor(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return EBinaryBoolOp("xor",left.desugar(), right.desugar())
    }
}


data class SugarNot(val left:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return ENot(left.desugar())
    }
}

data class SugarOnes(val number:Int): SugarExpression{
    override fun desugar(): Expression {
        var initial = ECall(
            core.primitives["add"]!!, listOf( EInt(1), EInt(1)))
        for (a in 1..number ){

            initial = ECall(primitives["add"]!!, listOf(initial, EInt(1)))
        }
        return initial
    }
}

data class SugarSetVar(val name:String, val value:SugarExpression): SugarExpression{
    override fun desugar(): Expression {
       return ESetVar(name, value.desugar())
    }
}

data class SugarList(val expressions: List<SugarExpression>): SugarExpression{
    override fun desugar(): Expression {

        return EList(expressions.map { it.desugar() })
    }

}
data class SugarDo(val expressions:List<SugarExpression>):SugarExpression{
    override fun desugar(): Expression {
        return EDo(expressions.map { it.desugar() })
    }
}

data class SugarPrimitive(val name:String, val params:List<Expression>) :SugarExpression{
    override fun desugar(): Expression {
        return  ECall( primitives[name]!!, params)
    }

}

data class SugarLet(val name:String, val value:SugarExpression, val body:SugarExpression):SugarExpression{
    override fun desugar(): Expression {
       return EDo(listOf(ESetVar(name, value.desugar()), body.desugar()))
    }

}

data class SugarLetStar(val bindings:List<Pair<String, SugarExpression>>, val body:SugarExpression):SugarExpression{
    override fun desugar(): Expression {

       val reversed = bindings.reversed()
        var initial = SugarLet(reversed[0].first, reversed[0].second, body)
       for(i in 1 until reversed.size){
           initial = SugarLet(reversed[i].first, reversed[i].second, initial)
       }
        return initial.desugar()
    }

}
