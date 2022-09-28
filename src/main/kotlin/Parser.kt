import org.yaml.snakeyaml.Yaml

fun parse(input:String):SugarExpression{
    val yaml = Yaml()
    val script = yaml.load<Any>(input)
    return convert(script)
}
fun convert(obj:Any):SugarExpression{
   if(obj is Int){
       return SugarInt(obj)
   }
    if(obj is ArrayList<*>){
        val operation= obj[0]
        if(operation == "add"){
            val left = convert(obj[1])
            val right = convert(obj[2])
            return SugarAdd(left, right)
        }
        if(operation =="mul"){
            val left = convert(obj[1])
            val right = convert(obj[2])
            return SugarMul(left, right)
        }
        if(operation =="sub"){
            val left = convert(obj[1])
            val right = convert(obj[2])
            return SugarSub(left, right)
        }

        if(operation =="neg"){
            val value = convert(obj[1])
            return SugarNeg(value)
        }
    }
    return SugarInt(-42)
}