package me.syari.ss.battle.status.mob

import me.syari.ss.battle.Main.Companion.battlePlugin
import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.battle.status.EntityStatus
import me.syari.ss.battle.status.player.StatusType
import org.bukkit.entity.Entity
import org.bukkit.metadata.FixedMetadataValue

class MobStatus(override var damageElementType: ElementType?, private val status: Map<StatusType, Float>) : EntityStatus {
    /**
     * ステータスマップの取得
     * @return [Map]<[StatusType], [Float]>
     */
    override fun get(): Map<StatusType, Float> {
        return status
    }

    companion object {
        private val statusMap = mutableMapOf<String, MobStatus>()

        private const val mobUniqueIdMetaDataKey = "ss-mob-id"

        private fun getMobStatus(id: String): MobStatus? {
            return statusMap[id]
        }

        /**
         * モブの固有ID
         */
        var Entity.mobUniqueId: String?
            get() {
                val metaDataList = getMetadata(mobUniqueIdMetaDataKey)
                return if (metaDataList.isNotEmpty()) {
                    metaDataList.first().asString()
                } else {
                    null
                }
            }
            set(value) {
                setMetadata(mobUniqueIdMetaDataKey, FixedMetadataValue(battlePlugin, value))
            }

        /**
         * モブのステータスを取得します
         */
        fun from(entity: Entity): MobStatus? {
            return entity.mobUniqueId?.let { getMobStatus(it) }
        }
    }
}