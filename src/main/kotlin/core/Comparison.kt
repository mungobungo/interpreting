package core

data class EEq(val first: Expression, val second: Expression) : Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        val firstResult = first.eval(context)
        if (!firstResult.success) {
            return firstResult
        }
        if (firstResult.value !is IComparable) {
            return evalTypeError(first, context, "cannot call 'eq'  on ${first.unparse()} \n ${firstResult.value!!.unparse()}")
        }
        val secondResult = second.eval(context)
        if (!secondResult.success) {
            return secondResult
        }
        val comparision = (firstResult.value as IComparable).eq(secondResult.value!!, context)
        if (comparision.success) {
            return CoreResult(true, context, comparision.value!!, null)
        }
        return CoreResult(false, context, null, comparision.error)
    }

    override fun unparse(): String {
        return "[eq, ${first.unparse()}, ${second.unparse()}]"
    }

}

data class ENeq(val first: Expression, val second: Expression) : Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        val firstResult = first.eval(context)
        if (!firstResult.success) {
            return firstResult
        }
        if (firstResult.value !is IComparable) {
            return evalTypeError(first, context, "cannot call 'neq'  on ${first.unparse()} \n ${firstResult.value!!.unparse()}")
        }
        val secondResult = second.eval(context)
        if (!secondResult.success) {
            return secondResult
        }
        val comparision = (firstResult.value as IComparable).neq(secondResult.value!!, context)
        if (comparision.success) {
            return CoreResult(true, context, comparision.value!!, null)
        }
        return CoreResult(false, context,null, comparision.error)
    }

    override fun unparse(): String {
        return "[neq, ${first.unparse()}, ${second.unparse()}]"
    }

}

data class ELt(val first: Expression, val second: Expression) : Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        val firstResult = first.eval(context)
        if (!firstResult.success) {
            return firstResult
        }
        if (firstResult.value !is IOrdered) {
            return evalTypeError(first, context,"cannot call 'lt'  on ${first.unparse()} \n ${firstResult.value!!.unparse()}")
        }
        val secondResult = second.eval(context)
        if (!secondResult.success) {
            return secondResult
        }
        val comparision = (firstResult.value as IOrdered).lt(secondResult.value!!, context)
        if (comparision.success) {
            return CoreResult(true, context,comparision.value!!, null)
        }
        return CoreResult(false, context,null, comparision.error)
    }

    override fun unparse(): String {
        return "[lt, ${first.unparse()}, ${second.unparse()}]"
    }

}

data class ELte(val first: Expression, val second: Expression) : Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        val firstResult = first.eval(context)
        if (!firstResult.success) {
            return firstResult
        }
        if (firstResult.value !is IOrdered || firstResult.value !is IComparable) {
            return evalTypeError(first, context,"cannot call 'lte'  on ${first.unparse()} \n ${firstResult.value!!.unparse()}")
        }
        val secondResult = second.eval(context)
        if (!secondResult.success) {
            return secondResult
        }
        val comparision = (firstResult.value as IOrdered).lte(secondResult.value!!,context)
        if (comparision.success) {
            return CoreResult(true,context, comparision.value!!, null)
        }
        return CoreResult(false, context,null, comparision.error)
    }

    override fun unparse(): String {
        return "[lte, ${first.unparse()}, ${second.unparse()}]"
    }

}

data class EGt(val first: Expression, val second: Expression) : Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        val firstResult = first.eval(context)
        if (!firstResult.success) {
            return firstResult
        }
        if (firstResult.value !is IOrdered) {
            return evalTypeError(first, context,"cannot call 'gt'  on ${first.unparse()} \n ${firstResult.value!!.unparse()}")
        }
        val secondResult = second.eval(context)
        if (!secondResult.success) {
            return secondResult
        }
        val comparision = (firstResult.value as IOrdered).gt(secondResult.value!!, context)
        if (comparision.success) {
            return CoreResult(true,context,  comparision.value!!, null)
        }
        return CoreResult(false, context,null, comparision.error)
    }

    override fun unparse(): String {
        return "[gt, ${first.unparse()}, ${second.unparse()}]"
    }

}

data class EGte(val first: Expression, val second: Expression) : Expression {
    override fun eval(context: Context): CoreResult<Expression> {
        val firstResult = first.eval(context)
        if (!firstResult.success) {
            return firstResult
        }
        if (firstResult.value !is IOrdered || firstResult.value !is IComparable) {
            return evalTypeError(first, context,"cannot call 'gte'  on ${first.unparse()} \n ${firstResult.value!!.unparse()}")
        }
        val secondResult = second.eval(context)
        if (!secondResult.success) {
            return secondResult
        }
        val comparision = (firstResult.value as IOrdered).gte(secondResult.value!!,context)
        if (comparision.success) {
            return CoreResult(true, context,comparision.value!!, null)
        }
        return CoreResult(false, context,null, comparision.error)
    }

    override fun unparse(): String {
        return "[gte, ${first.unparse()}, ${second.unparse()}]"
    }

}
