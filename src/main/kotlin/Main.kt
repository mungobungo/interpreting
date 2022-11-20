import core.*
import parser.parse

fun main(args: Array<String>) {
    println("REPL v0.2")
    val variables = hashMapOf<String, Expression>(
        "x" to EInt(3),
        "y" to EInt(10),
    )
    val context = Context(Environment(variables))
    val typeEnv = TypeEnv(hashMapOf())
    for( elem in variables){
        typeEnv.env[elem.key]  = typeOf(elem.value, typeEnv).result
    }


    while (true){
        print(">> ")
        val input = readln()
        if(input == "quit")
        {
            return
        }
        val parseStart = System.nanoTime()
        val parsed = parse(input)
        val parserTime = System.nanoTime() - parseStart
        if(!parsed.success ){
            println("Parsing error: " + parsed.error!!.message  + "\n ${parsed.error!!.input}")
            continue
        }

        val parsedValue = parsed.value!!.desugar()
        val typeStart = System.nanoTime()

        val typed = typeOf(parsedValue, typeEnv)
        val typeTime = System.nanoTime() - typeStart
        if(typed.result.type is TypeError){
            println("Type error: " + typed.result.type.error)
        }

        val evalStart = System.nanoTime()
        val evaluated = parsedValue.eval(context)
        val evalTime = System.nanoTime() - evalStart
        if(!evaluated.success){
            println("Eval error: " + evaluated.error!!.message + "\n ${evaluated.error!!.input}")
        }
        else{
            println(evaluated.value!!.unparse() + " :: ${typed.result}" )
        }
        println("parser: ${parserTime/1e6f}ms, type: ${typeTime/1e6f}ms,  eval: ${evalTime/1e6f}ms")
    }

}