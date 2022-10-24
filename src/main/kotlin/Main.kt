import parser.parse

fun main(args: Array<String>) {
    println("REPL v0.1")

    while (true){
        print(">> ")
        val input = readln()
        if(input == "quit")
        {
            return
        }
        val parsed = parse(input)
        if(!parsed.success ){
            println("Parsing error: " + parsed.error!!.message  + "\n ${parsed.error!!.input}")
            continue
        }
        val evaluated = parsed.value!!.desugar().eval()
        if(!evaluated.success){
            println("Eval error: " + evaluated.error!!.message + "\n ${evaluated.error!!.input}")
        }else{
            println(evaluated.value!!.unparse())
        }
    }
    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.

}