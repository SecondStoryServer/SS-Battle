package me.syari.ss.battle.status.mob

import me.syari.ss.battle.Main
import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.battle.status.StatusType
import org.bukkit.entity.Entity
import org.bukkit.metadata.FixedMetadataValue

data class MobTypeStatus(
    val damageElementType: ElementType, val status: Map<StatusType, Float>
) {
    companion object {
        private val statusMap = mutableMapOf<String, MobTypeStatus>()

        fun register(id: String, status: MobTypeStatus) {
            statusMap[id] = status
        }

        fun clear() {
            statusMap.clear()
        }

        private const val mobTypeIdMetaDataKey = "ss-mob-id"

        private fun getMobStatus(id: String): MobTypeStatus? {
            return statusMap[id]
        }

        /**
         * モブの種類ID
         */
        var Entity.mobTypeId: String?
            get() {
                val metaDataList = getMetadata(mobTypeIdMetaDataKey)
                return if (metaDataList.isNotEmpty()) {
                    metaDataList.first().asString()
                } else {
                    null
                }
            }
            set(value) {
                setMetadata(mobTypeIdMetaDataKey, FixedMetadataValue(Main.battlePlugin, value))
            }

        /**
         * モブのステータスを取得します
         */
        fun from(entity: Entity): MobTypeStatus? {
            return entity.mobTypeId?.let { getMobStatus(it) }
        }
    }
}