package me.syari.ss.battle.status.mob

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.battle.status.EntityStatus
import me.syari.ss.battle.status.player.StatusType
import org.bukkit.entity.Entity

class MobStatus(override var damageElementType: ElementType?, private val status: Map<StatusType, Float>) : EntityStatus {
    override fun get(): Map<StatusType, Float> {
        return status
    }

    companion object {
        private val statusMap = mutableMapOf<String, MobStatus>()

        fun getMobStatus(id: String): MobStatus? {
            return statusMap[id]
        }

        fun from(entity: Entity): MobStatus? {
            val metaDataList = entity.getMetadata("ss-mob-id")
            if (metaDataList.isEmpty()) return null
            return getMobStatus(metaDataList.first().asString())
        }
    }
}