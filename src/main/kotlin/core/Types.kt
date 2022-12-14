package core

interface StrongType{
    val type:String
    fun toScheme():TScheme
}

data class TypeCheckResult(val success:Boolean, val result:TScheme, val te: TypeEnv, val sub: Substitution = emptySub)
class Helper{
    companion object {
        var typeCount = 0

    fun newTypeVar(): TVar{
        typeCount += 1
        return TVar(type = "t$typeCount")
    }
    }
}
data class TypeEnv(val env:HashMap<String, TScheme>, val parent:TypeEnv?=null){
    fun expand(): TypeEnv{
        return TypeEnv(hashMapOf(), this)
    }
    fun isLocallyDefined(symbol:String):Pair<Boolean, TScheme>{
        val res = env[symbol]

        return if(res !=null){
            Pair(true, res)
        }else{
            Pair(false,TypeError("binding_not_found",ESymbol(symbol), "symbol `$symbol` not found in typeEnv $this").toScheme())
        }
    }
    fun isGloballyDefined(symbol: String):Pair<Boolean, TScheme>{
        var current: TypeEnv? = this
        val res = Pair(false,TypeError("binding_not_found",ESymbol(symbol), "symbol `$symbol` not found in typeEnv $this").toScheme())
        while(current!=null){
            val value = current!!.isLocallyDefined(symbol)
            if(value.first) {
                return value
            }
            current = current.parent
        }
        return res
    }
}

data class TInt(override val type:String = "int"): StrongType {
    override fun toScheme(): TScheme {
       return TScheme(listOf(), this)
    }

    override fun toString(): String {
       return "int"
    }
}

data class  TFloat(override val type:String="float"): StrongType {
    override fun toScheme(): TScheme {
       return TScheme(listOf(), this)
    }

    override fun toString(): String {
       return "float"
    }
}

data class  TBool(override val type:String="bool"): StrongType {
    override fun toScheme(): TScheme {
       return TScheme(listOf(), this)
    }

    override fun toString(): String {
       return "bool"
    }
}

data class TypeError(override val type:String ="type_error", val input: Expression,  val error:String):StrongType {
    override fun toScheme(): TScheme {
       return TScheme(listOf(), this)
    }
}

data class TVar(override val type:String= "tvar") :StrongType {
    override fun toScheme(): TScheme {
       return TScheme(listOf(type), this)
    }

    override fun toString(): String {
       return type
    }
}
// is_int :: a -> Bool

data class TScheme(val typeVars:List<String>, val type:StrongType){
    override fun toString(): String {
        if(typeVars.isEmpty()){
            return type.toString()
        }
       return "forall "+ typeVars.joinToString(","){it}  + "::" + type.toString()
    }
} // for all, a, b :  a -> b -> a
// Scheme without typeVars is considered to be just a type // forall () : Int == Int
data class TFunc(override val type:String ="fun",  val params:List<StrongType>, val result:StrongType):StrongType {
    override fun toScheme(): TScheme {
       return TScheme(listOf(), this)
    }

    override fun toString(): String {
       if(params.isEmpty()){
           return "() -> $result"
       }
        return "(${params.joinToString(" -> ")} -> $result)"
    }
}

// lambda x, y : x  :: TScheme([a, b], TFunc(params:[a, b], result:a)


// lambda x : [call, x, 1]  ::  TScheme([b], TFunc(params:Func(Int->b), result : b)
// we don't know type of x. But we know it is a function. and this function accepts int. and it returns yet unknown type b.


// [let, x, 5, x] :: int
// [let, id, [lambda, [x], x], [call, id, true]] :: Bool
// [let, id, [lambda, [x], x]] :: TScheme([a], TFunc(params:[a], result : a)) // forall a. :: a->a
fun typeOf(e: Expression, te: TypeEnv) : TypeCheckResult{
    when(e){
        is EInt -> return  TypeCheckResult(true, TInt().toScheme(), te)
        is EFloat -> return TypeCheckResult(true, TFloat().toScheme(), te)
        is EBool -> return  TypeCheckResult(true, TBool().toScheme(), te)
        is EBinaryIntegerOp -> return binaryIntegerOpType(e, te)
        is EBinaryFloatOp -> return binaryFloatOpType(e, te)
        is EBinaryNumericOp -> return binaryNumericOpType(e, te)
        is EBinaryBoolOp -> return binaryBoolOpType(e, te)
        is ESymbol -> return symbolType(e, te)
        is ESetVar -> return setVarType(e, te)
        is EDo -> return doType(e, te)
        is ELambdaDefinition -> return lambdaType(e, te)
        is ECall -> return callType(e, te)
        is EIf -> return ifType(e,te)
    }
    return TypeCheckResult(false,  TypeError("type_error", e, "unsupported expression ${e.unparse()}").toScheme(),te)
}

fun ifType(e: EIf, te: TypeEnv): TypeCheckResult {
    val typeCond = typeOf(e.condition, te)
    if(!typeCond.success){
        return typeCond
    }
    val condSub = unify(typeCond.result.type, TBool())
    if(!condSub.success){
        return TypeCheckResult(false, TypeError("if_condition_type_error", e, condSub.errorMessage).toScheme(),te)
    }
    val branch1 = typeOf(e.mainBranch, te)
    val branch2 = typeOf(e.alternativeBranch, te)
    if(!branch1.success){
        return branch1
    }
    if(!branch2.success){
        return branch2
    }
    val resSub = unify(branch1.result.type, branch2.result.type)
   if(!resSub.success){
       return TypeCheckResult(false, TypeError("if_branch_mismatch_error", e, resSub.errorMessage).toScheme(),te)
   }
    return TypeCheckResult(true, applySubstitution(resSub.sub, branch1.result.type).toScheme(), te)

}

data class Substitution(val subs:HashMap<String, StrongType>)

fun applySubstitution (sub:Substitution,  t:StrongType) : StrongType{
    if(t is TFunc){
        return TFunc("fun", t.params.map { applySubstitution(sub, it) }, applySubstitution(sub, t.result) )
    }
    if( t is TVar){
        if(sub.subs.containsKey(t.type)){
            return sub.subs[t.type]!!
        }
    }
    return t
}
fun applySchemeSubstitution (sub:Substitution,  s:TScheme) : TScheme{
    val schemeVars = s.typeVars.toSet()
    val freeSubs = hashMapOf<String,StrongType>()
    for(subVal in sub.subs ){
        if(!schemeVars.contains(subVal.key)){
            freeSubs[subVal.key] = subVal.value
        }
    }
    return TScheme(s.typeVars, applySubstitution(Substitution(freeSubs), s.type))
}

fun applyEnvSubstitution(sub:Substitution, te:TypeEnv) : TypeEnv{
    val res = hashMapOf<String, TScheme>()
    for(entry in te.env){
        res[entry.key] = applySchemeSubstitution(sub, entry.value)
    }
    return TypeEnv(res)
}

fun composeSub(sub1: Substitution, sub2: Substitution) : Substitution{
    val s3  = hashMapOf<String, StrongType>()

    // apply sub1 to all types in sub2 and  save them to the s3
    for(s in sub2.subs){
        //if(sub1.subs.containsKey(s.key)){
            s3[s.key] = applySubstitution(sub1, s.value)
       // } else{
          //  s3[s.key] = s.value
        //}
    }
    // take all the vals from sub1 that were not substituted, and add them to s3
    for(s in sub1.subs){
        if(!s3.containsKey(s.key)){
            s3[s.key] = s.value
        }
    }
    return Substitution(s3)
}

// would be nice to get substitutions from somewhere
// unify to the rescue.
val emptySub = Substitution(hashMapOf())
//unify - return the substitution that makes two types equal
data class UnificationResult(val success:Boolean, val sub:Substitution = emptySub, val error:Pair<StrongType, StrongType>? = null, val errorMessage:String = "Unification error:"){
    override fun toString(): String {
       if(success){
           return sub.toString()
       }
        return "$errorMessage Cannot unify ${error!!.first} and ${error.second}"
    }
}

fun freeVars(t:StrongType): HashSet<String>{
    if(t is TVar){
        return hashSetOf(t.type)
    }
    if(t is TFunc){
        val result = hashSetOf<String>()
        for(arg in t.params){
            result.addAll(freeVars(arg))
        }
        result.addAll(freeVars(t.result))
        return result
    }
    return hashSetOf()
}
fun unify(t1: StrongType, t2:StrongType) : UnificationResult{
    if(t1 is TInt && t2 is TInt){
        return UnificationResult(true)
    }
    if(t1 is TFloat && t2 is TFloat){
        return UnificationResult(true)
    }
    if(t1 is TBool && t2 is TBool){
        return UnificationResult(true)
    }
    if(t1 is TVar){
        if(t2 is TVar && t1.type == t2.type){
            return UnificationResult(true)
        }
        if(freeVars(t2).contains(t1.type)){
            return UnificationResult(false, emptySub, Pair(t1,t2), "Occurs check for `${t1.type}`:\n")
        }
        return UnificationResult(true, Substitution(hashMapOf(t1.type to t2)))
    }
    if(t2 is TVar){
        if(t1 is TVar && t1.type == t2.type){
            return UnificationResult(true)
        }
        if(freeVars(t1).contains(t2.type)){
            return UnificationResult(false, emptySub, Pair(t1,t2), "Occurs check for `${t2.type}`:\n")
        }
        return UnificationResult(true, Substitution(hashMapOf(t2.type to t1)))
    }

    if(t1 is TFunc && t2 is TFunc){
        if(t1.params.size != t2.params.size){
            return UnificationResult(false, emptySub, Pair(t1, t2), "Func argument count mismatch,\n${t1.params.size} arguments for $t1\n${t2.params.size} arguments for $t2\n")
        }
        var funArgumatnsSub = Substitution(hashMapOf())
        for(param in t1.params.zip(t2.params)){
            val argSub = unify(param.first, param.second)
            if(!argSub.success){
                return argSub
            }
            funArgumatnsSub = composeSub(argSub.sub, funArgumatnsSub)
        }
        val funReturnUni = unify(t1.result, t2.result)
        if(!funReturnUni.success){
            return funReturnUni
        }
       return UnificationResult(true, composeSub( funArgumatnsSub, funReturnUni.sub))
    }
    return UnificationResult(false, emptySub, Pair(t1, t2))
}
fun lambdaType(e: ELambdaDefinition, te: TypeEnv): TypeCheckResult {

   val localTypeEnv = te.expand()

    val argNames = e.argumentNames
    val argTypes = mutableListOf<TVar>()
    for(argName in argNames){
        val argType= Helper.newTypeVar()
        localTypeEnv.env[argName] = argType.toScheme()
        argTypes.add(argType)
    }
// [lambda, [a,b], 3]] :: forall a,b   Func([a,b] , int)

    // what about
    // [lambda, [a,b], [iadd, a, 4]] ??
    // a can only be integer, we need to capture this info somehow
    // To deal with it , we need to have a way of capturing this information
    // during the type inference
    // original type :: a -> b -> int
    // updated type :: int -> b -> int
    // the only way to satisfy [iadd, a, 4] is for a to have type int.

    for(exp in e.body){
        val type = typeOf(exp, localTypeEnv)
        if(!type.success){
            return type
        }
    }
    val resType = typeOf(e.body.last(), localTypeEnv)
    val subArgs = mutableListOf<StrongType>()
    for(arg in argTypes){
        subArgs.add(applySubstitution(resType.sub, arg))
    }
    return TypeCheckResult(true, TScheme(argTypes.map { it.type }, TFunc(params = subArgs, result = resType.result.type)), te)

}

fun callType(e: ECall, te: TypeEnv): TypeCheckResult {
    val localTypeEnv = te.expand()
    val tyres = Helper.newTypeVar()
    val funType = typeOf(e.func, localTypeEnv)
    if(!funType.success){
        return funType
    }
    var resSub = funType.sub
    val args = mutableListOf<StrongType>()
    for(arg in e.args){
        val updatedTypeEnv = applyEnvSubstitution(resSub, localTypeEnv)
        val argType = typeOf(arg, updatedTypeEnv)
        if(!argType.success){
            return argType
        }
        resSub = composeSub(resSub, argType.sub)
        args.add(argType.result.type)
    }
    val inferredFun = applySubstitution(resSub, funType.result.type)
    val resultingFun = TFunc("fun", args, tyres)
    val uSub = unify( resultingFun, inferredFun)
    if(!uSub.success){
        return TypeCheckResult(false, TypeError("error", e, uSub.errorMessage).toScheme(), te)
    }
    val inferredRes = applySubstitution(uSub.sub, tyres)
    resSub = composeSub(resSub, uSub.sub)
    return TypeCheckResult(true, inferredRes.toScheme(), te, resSub)

}
fun doType(e: EDo, te: TypeEnv): TypeCheckResult {
   val localTypeEnv = te.expand()
    for(exp in e.expressions){
        val type = typeOf(exp, localTypeEnv)
        if(!type.success){
            return type
        }
    }
    return TypeCheckResult(true, typeOf(e.expressions.last(), localTypeEnv).result, te)
}

fun setVarType(e: ESetVar, te: TypeEnv): TypeCheckResult {
   val t = typeOf(e.variableValue, te)
    if(!t.success){
        return t
    }
    te.env[e.name] = t.result
    return TypeCheckResult(true, t.result, te)
}


fun symbolType(e: ESymbol, te:TypeEnv): TypeCheckResult {
    val res = te.isGloballyDefined(e.name)
    if(res.first){
        return TypeCheckResult(true, res.second, te)
    }
    return TypeCheckResult(false, res.second, te)
}

fun binaryBoolOpType(e: EBinaryBoolOp, te:TypeEnv): TypeCheckResult {
   val left = typeOf(e.left, te)
    if(!left.success){
        return left
    }
   val right = typeOf(e.right,te)
    if(!right.success){
        return right
    }
    val subRes = unify(left.result.type, right.result.type)
    if(!subRes.success) {
        return TypeCheckResult(false,  TypeError("binary_bool_type_error", e, subRes.errorMessage).toScheme(),
            te
        )
    }
    return TypeCheckResult(true, TBool().toScheme(), te, subRes.sub)
}

fun binaryNumericOpType(e: EBinaryNumericOp, te:TypeEnv): TypeCheckResult {
    val left = typeOf(e.left, te)
    if(!left.success){
        return left
    }
    val right = typeOf(e.right,te)
    if(!right.success){
        return right
    }
    if(left.result.type is TInt && right.result.type is TInt){
        return TypeCheckResult(true, left.result, te)
    }
    if(left.result.type is TFloat && right.result.type is TInt){
        return TypeCheckResult(true, left.result, te)
    }
    if(left.result.type is TInt && right.result.type is TFloat){
        return TypeCheckResult(true, right.result, te)
    }
    if(left.result.type is TFloat && right.result.type is TFloat){
        return TypeCheckResult(true, left.result, te)
    }

    return TypeCheckResult(false,  TypeError("binary_numeric_type_error", e, "left and right arguments of `${e.operationName}` are expected to be numerics, but got $left and $right in ${e.unparse()}").toScheme(), te)
}

fun binaryIntegerOpType(e: EBinaryIntegerOp, te:TypeEnv): TypeCheckResult {
    val left = typeOf(e.left, te)
    if(!left.success){
        return left
    }
    val right = typeOf(e.right,te)
    if(!right.success){
        return right
    }
    val leftSub = unify(left.result.type, TInt())
    val rightSub = unify(right.result.type, TInt())
    if(!leftSub.success) {
        return TypeCheckResult(false,  TypeError("binary_int_type_error", e, leftSub.errorMessage).toScheme(),
            te
        )
    }
    if(!rightSub.success) {
        return TypeCheckResult(false,  TypeError("binary_int_type_error", e, rightSub.errorMessage).toScheme(),
            te
        )
    }
    val composedSub = composeSub(leftSub.sub, rightSub.sub)
    return TypeCheckResult(true, TInt().toScheme(), te, composedSub)
}
fun binaryFloatOpType(e: EBinaryFloatOp, te:TypeEnv): TypeCheckResult {
    val left = typeOf(e.left, te)
    if(!left.success){
        return left
    }
    val right = typeOf(e.right,te)
    if(!right.success){
        return right
    }
    val leftSub = unify(left.result.type, TFloat())
    val rightSub = unify(right.result.type, TFloat())
    if(!leftSub.success) {
        return TypeCheckResult(false,  TypeError("binary_float_type_error", e, leftSub.errorMessage).toScheme(),
            te
        )
    }
    if(!rightSub.success) {
        return TypeCheckResult(false,  TypeError("binary_float_type_error", e, rightSub.errorMessage).toScheme(),
            te
        )
    }
    val composedSub = composeSub(leftSub.sub, rightSub.sub)
   return TypeCheckResult(true, TFloat().toScheme(), te, composedSub)
}
