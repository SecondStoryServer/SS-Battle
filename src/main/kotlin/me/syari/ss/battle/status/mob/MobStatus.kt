package me.syari.ss.battle.status.mob

import me.syari.ss.battle.status.EntityStatus
import me.syari.ss.battle.status.player.StatusType
import org.bukkit.entity.Entity

class MobStatus(
    val entity: Entity, private val mobTypeStatus: MobTypeStatus
): EntityStatus {
    /**
     * ダメージの属性
     */
    override val damageElementType = mobTypeStatus.damageElementType

    /**
     * ステータスマップの取得
     * @return [Map]<[StatusType], [Float]>
     */
    override fun get(): Map<StatusType, Float> {
        return mobTypeStatus.status
    }

    companion object {
        /**
         * モブのステータスを取得します
         */
        fun from(entity: Entity): MobStatus? {
            return MobTypeStatus.from(entity)?.let { MobStatus(entity, it) }
        }
    }
}