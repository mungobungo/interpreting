package core

interface StrongType {
    fun unparse(): String
    fun nestedUnparse(): String
}

class TInt : StrongType{
    override fun unparse(): String {
       return "int"
    }

    override fun nestedUnparse(): String {
       return unparse()
    }
}

class TBool: StrongType{
    override fun unparse(): String {
       return "bool"
    }

    override fun nestedUnparse(): String {
       return unparse()
    }

}

class TFloat : StrongType{
    override fun unparse(): String {
       return "float"
    }

    override fun nestedUnparse(): String {
        return unparse()
    }
}

data class TFunc(val arguments:List<StrongType>, val result:StrongType) :StrongType{
    override fun unparse(): String {

       return arguments.joinToString(" -> ") { it.nestedUnparse() }  + " -> " + result.unparse()
    }

    override fun nestedUnparse(): String {
       return "(${this.unparse()})"
    }
}

class TInvalidType: StrongType{
    override fun unparse(): String {
       return "invalid_type"
    }

    override fun nestedUnparse(): String {
       return unparse()
    }
}