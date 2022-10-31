import core.*
import parser.parse

fun main(args: Array<String>) {
    println("REPL v0.1")
    //[fun, mkc, [], [[let, start_counter, 0, [lambda, [], [[setvar, inner_counter, [add, start_counter, 1]], inner_counter]]]]]
    var context = defaultContext

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
            //context = evaluated.context
        }
        println("parser: ${parserTime/1e6f}ms, eval: ${evalTime/1e6f}ms")
    }

}