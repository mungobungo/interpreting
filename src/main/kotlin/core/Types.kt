package core

interface StrongType

class TInt: StrongType

class  TFloat: StrongType

class  TBool: StrongType

data class TypeError(val input: Expression,  val error:String):StrongType

fun typeOf(e: Expression) : StrongType{
    when(e){
        is EInt -> return TInt()
        is EFloat -> return TFloat()
        is EBool -> return TBool()
    }
    return TypeError(e, "unsupported expression ${e.unparse()}")
}