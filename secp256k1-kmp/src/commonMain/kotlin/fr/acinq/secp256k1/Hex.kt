@file:Suppress("ktlint")

package fr.acinq.secp256k1
public object Hex {
    private val hexCode = arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    public fun decode(hex: String): ByteArray {
        val input = hex.filterNot { it.isWhitespace() }
        val offset = when {
            input.length >= 2 && input[0] == '0' && input[1] == 'x' -> 2
            input.length >= 2 && input[0] == '0' && input[1] == 'X' -> 2
            else -> 0
        }
        val len = input.length - offset
        require(len % 2 == 0)
        val out = ByteArray(len / 2)

        fun hexToBin(ch: Char): Int = when (ch) {
            in '0'..'9' -> ch - '0'
            in 'a'..'f' -> ch - 'a' + 10
            in 'A'..'F' -> ch - 'A' + 10
            else -> throw IllegalArgumentException("illegal hex character: $ch")
        }

        for (i in out.indices) {
            out[i] = (hexToBin(input[offset + 2 * i]) * 16 + hexToBin(input[offset + 2 * i + 1])).toByte()
        }

        return out
    }

    public fun encode(input: ByteArray, offset: Int, len: Int): String {
        val r = StringBuilder(len * 2)
        for (i in 0 until len) {
            val b = input[offset + i]
            r.append(hexCode[(b.toInt() shr 4) and 0xF])
            r.append(hexCode[b.toInt() and 0xF])
        }
        return r.toString()
    }

    public fun encode(input: ByteArray): String = encode(input, 0, input.size)
}
