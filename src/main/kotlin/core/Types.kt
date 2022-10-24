package core


data class EInt(val value:Int):Expression {
    override fun eval(): CoreResult<Expression> {
        return evalSuccess(this)
    }

    override fun unparse(): String {
        return value.toString()
    }
}

data class EFloat(val value:Double):Expression {
    override fun eval(): CoreResult<Expression> {
        return evalSuccess(this)
    }

    override fun unparse(): String {
        return value.toString()
    }
}


data class EBool(val value:Boolean):Expression {
    override fun eval(): CoreResult<Expression> {
        return evalSuccess(this)
    }

    override fun unparse(): String {
        return value.toString()
    }
}
val zeroInt = EInt(0)
data class ESymbol(val name:String):Expression{
    //var evaluated = false

    override fun eval(): CoreResult<Expression> {

        return evalSuccess(this)
        //return substitute(this )

    }


    override fun unparse(): String {
        return name
    }
}
