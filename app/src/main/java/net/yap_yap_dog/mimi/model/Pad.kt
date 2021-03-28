package net.yap_yap_dog.mimi.model

data class Key(
    val keyNumber: Int,
    val noteNumber: Int,
    val velocity: Int,
    val color: Int
)

data class Pad(
    val channel: Int = 0,
    val name: String = "untitled",
    val keys: List<Key> = (24 until 24 + keyCount).mapIndexed { index, noteNumber ->
        Key(
            index + 1,
            noteNumber,
            127,
            0x6A5ACD
        )
    }
) {
    companion object {
        // 現段階では8枚で固定のため
        const val keyCount = 8
    }
}