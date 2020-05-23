package me.syari.ss.battle.status.player

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.battle.status.OnDamageStatus
import me.syari.ss.battle.status.StatusType

class OnDamagePlayerStatus(status: PlayerStatus, override val damageElementType: ElementType): OnDamageStatus {
    private val status = status.clone()

    override val map: Map<StatusType, Float>
        get() {
            status.update()
            return status.map
        }

    fun add(statusType: StatusType, value: Float, changeType: StatusChange.Type){
        status.add(StatusChange.Cause.Equipment, statusType, value, changeType)
    }
}