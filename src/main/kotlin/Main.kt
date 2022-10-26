import core.Context
import core.EInt
import core.Environment
import core.Expression
import parser.parse
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    println("REPL v0.1")
    val variables = hashMapOf<String, Expression>(
        "x" to EInt(3),
        "y" to EInt(10)

    )
    val context = Context(Environment(variables))

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
        val evalStart = System.nanoTime()

        val evaluated = parsed.value!!.desugar().eval(context)
        val evalTime = System.nanoTime() - evalStart
        if(!evaluated.success){
            println("Eval error: " + evaluated.error!!.message + "\n ${evaluated.error!!.input}")
        }else{
            println(evaluated.value!!.unparse())
        }
        println("parser: ${parserTime/1000000.0}ms, eval: ${evalTime/1000000.0}ms")
    }
    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.

}