import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.junit.jupiter.api.Test

class Scratch {

    @Test
    fun test() {
        println(MiniMessage.miniMessage().deserialize("<gradient:#e4c0f8:#b6b4e8:#afd6e8><bold><st>            </st>  Yetzirah Land  <st>            </st></gradient>").let {
            LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().hexColors().build().serialize(it)
        })
        println(MiniMessage.miniMessage().deserialize("<gradient:#e4c0f8:#b6b4e8:#afd6e8><bold><st>                                            </st></gradient>").let {
            LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().hexColors().build().serialize(it)
        })
        assert(true)
    }

}