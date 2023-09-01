package io.sn.etherdec

import de.cubbossa.commonsettings.Setting
import de.cubbossa.commonsettings.SettingBuilder
import io.sn.etherdec.objects.ScoreBoardStatus
import java.util.*
import java.util.concurrent.CompletableFuture
import de.cubbossa.commonsettings.NamespacedKey as SettingKey

@Deprecated("为记分板设置的 CommonSettings API，随着记分板系统用了其他插件而废弃")
object CommonSettings {

    private val scoreboardStatusData: HashMap<UUID, ScoreBoardStatus> = hashMapOf()
    val scoreboardKey = SettingKey("EtherDecompositor", "scoreboardstatus")

    @Deprecated("废弃")
    fun setup() {
        EtherCore.settings.registerSetting(
            SettingBuilder(ScoreBoardStatus::class.java, scoreboardKey)
                .withTitle("Player scoreboard display policy")
                .withGetter { uuid: UUID -> scoreboardStatusData.getOrDefault(uuid, ScoreBoardStatus.DISABLED) }
                .withSetter { uuid: UUID, newValue: ScoreBoardStatus ->
                    scoreboardStatusData[uuid] = newValue
                    CompletableFuture.completedFuture(Setting.SettingChangeResult.SUCCESS)
                }
                .build())
    }

}