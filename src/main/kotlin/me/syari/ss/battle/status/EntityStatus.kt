package me.syari.ss.battle.status

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.battle.status.player.StatusType

interface EntityStatus {
    var damageElementType: ElementType?

    fun get(): Map<StatusType, Float>
}