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

data class SugarAdd(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
       return EAdd(left.desugar(), right.desugar())
    }

}

data class SugarMul(val left:SugarExpression, val right:SugarExpression): SugarExpression{
    override fun desugar(): Expression {
       return EMul(left.desugar(), right.desugar())
    }
}
data class SugarDiv(val left:SugarExpression, val right:SugarExpression): SugarExpression{
    override fun desugar(): Expression {
        return EDiv(left.desugar(), right.desugar())
    }
}
data class SugarSub(val left:SugarExpression, val right: SugarExpression):SugarExpression{
    override fun desugar(): Expression {
       return EAdd(left.desugar(),  EMul(EInt(-1), right.desugar() ))
    }
}

data class SugarNeg(val value:SugarExpression):SugarExpression{
    override fun desugar(): Expression {
       return EMul(EInt(-1), value.desugar())
    }

}

data class SugarSymbol(val name:String):SugarExpression{
    override fun desugar(): Expression {
       return ESymbol(name)
    }

}