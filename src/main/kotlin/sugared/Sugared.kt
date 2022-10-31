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



data class SugarSymbol(val name:String):SugarExpression{
    override fun desugar(): Expression {
       return ESymbol(name)
    }

}
data class SugarLt(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return ELt(left.desugar(), right.desugar())
    }
}
data class SugarLte(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return ELte(left.desugar(), right.desugar())
    }
}


data class SugarGt(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return EGt(left.desugar(), right.desugar())
    }
}
data class SugarGte(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return EGte(left.desugar(), right.desugar())
    }
}
data class SugarEq(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return EEq(left.desugar(), right.desugar())
    }
}


data class SugarNeq(val left:SugarExpression, val right:SugarExpression) : SugarExpression{
    override fun desugar(): Expression {
        return ENeq(left.desugar(), right.desugar())
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

data class SugarPrimitive(val name:String, val params:List<SugarExpression>) :SugarExpression{
    override fun desugar(): Expression {
        return  ECall( primitives[name]!!, params.map{it.desugar()})
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
