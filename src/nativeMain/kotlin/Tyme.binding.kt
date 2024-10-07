
import tyme4rs_clib.add
import tyme4rs_clib.get_solar_day

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
fun main(args: Array<String>) {
    try {
        val a = args[0].toInt()
        val b = args[1].toInt()
        val res = add(a, b)
        val a = get_solar_day()
        println("res=$res, solar=$a")
    } catch(e: Exception) {
        println("error: ${e.message}")
    }
}
