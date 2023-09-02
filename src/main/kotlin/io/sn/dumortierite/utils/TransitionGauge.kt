@file:Suppress("unused")

package io.sn.dumortierite.utils

import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.ObjectUtils.min
import io.sn.etherdec.EtherCore
import kotlin.math.roundToInt

open class TransitionGauge(
    private val lengthInHalf: Int,
    private val separator: Char,
    private val progress: CharSequence,
    private val currentVal: Float,
    private val maxVal: Float,
    private val emptyp: Boolean
) {

    override fun toString(): String {
        val roundoff = (min(currentVal / maxVal, 1f) as Float).roundToInt() * 100

        return progress.repeat(
            lengthInHalf
        ) + " $roundoff % " + progress.repeat(
            lengthInHalf
        )
    }

    fun withColor(): String {
        if (!emptyp) {
            return "&8[ &a&m    &2&l ✔ &a&m    &8 ]"
        }

        val ratio = min(currentVal / maxVal, 1f) as Float
        val cutted = cutString(this.toString(), ratio)

        return EtherCore.legacys(EtherCore.minid("<dark_gray>[<transition:#FF5555:#FFFF55:#55FF55:$ratio>${cutted.first}</transition><gray>${cutted.second}</gray>]</dark_gray>"))
    }


    private fun cutString(str: String, ratio: Float): Pair<String, String> {
        val mid = (str.length * ratio).toInt()
        return Pair(
            str.slice(0 until mid), str.slice(mid until str.length)
        )
    }

}
