import org.yaml.snakeyaml.Yaml

fun parse(input:String):Expression{
    val yaml = Yaml()
    val script = yaml.load<Any>(input)
    return convert(script)
}
fun convert(obj:Any):Expression{
   if(obj is Int){
       return EInt(obj)
   }
    if(obj is ArrayList<*>){
        val operation= obj[0]
        if(operation == "add"){
            val left = convert(obj[1])
            val right = convert(obj[2])
            return EAdd(left, right)
        }
    }
    return EInt(-42)
}