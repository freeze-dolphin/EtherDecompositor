package io.sn.etherdec.modules

import org.junit.jupiter.api.Test

class AmmoTest {

    private fun printJson(id: String, data: Int) {
        println(",\n\"ETHERITE_AMMO_$id\":\n{\n\"id\": \"ammo/${id.lowercase()}\",\n\"item\": \"prismarine_shard\",\n\"template\": \"ITEM\",\n\"data\": $data\n}")
    }

    @Test
    fun alwaysPass() {
        var data = 4066
        AmmoList.ammoIds.forEach {
            printJson(it, data)
            data += 1
        }

        assert(true)
    }

}