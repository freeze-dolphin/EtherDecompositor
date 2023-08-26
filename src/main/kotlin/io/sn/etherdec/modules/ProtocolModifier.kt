package io.sn.etherdec.modules

import io.sn.etherdec.EtherCore
import io.sn.etherdec.objects.AbstractModule
import io.sn.etherdec.objects.Unregisterable


class ProtocolModifier(plug: EtherCore) : AbstractModule(plug), Unregisterable {

    override fun postSetup() {
    }

    override fun onDisable() {
    }

}
