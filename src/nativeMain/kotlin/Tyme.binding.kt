
import tyme4rs_clib.add

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
fun main(args: Array<String>) {
    try {
        val a = args[0].toInt()
        val b = args[1].toInt()
        val res = add(a, b)
        println("res=$res")
    } catch(e: Exception) {
        println("error: ${e.message}")
    }
}