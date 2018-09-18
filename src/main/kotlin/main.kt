@file:JvmName("Main")
fun main(args: Array<String>) {
    if (args.size != 4) {
        println("Pass 4 parameters: a, b, c, e")
        return
    }
    val a = args[0].toDoubleOrNull()
    val b = args[1].toDoubleOrNull()
    val c = args[2].toDoubleOrNull()
    val e = args[3].toDoubleOrNull()

    if (a != null && b != null && c != null && e != null) {
        val solver = Solver(a,b,c,e)
        solver.getRoots().forEach(::println)
    }
}