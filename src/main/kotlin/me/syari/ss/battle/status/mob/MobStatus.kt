package me.syari.ss.battle.status.mob

import me.syari.ss.battle.status.EntityStatus
import me.syari.ss.battle.status.OnDamageStatus
import org.bukkit.entity.Entity

class MobStatus(
    val entity: Entity, mobTypeStatus: MobTypeStatus
): EntityStatus, OnDamageStatus {
    /**
     * ダメージの属性
     */
    override val damageElementType = mobTypeStatus.damageElementType

    /**
     * ステータスマップ
     */
    override val map = mobTypeStatus.status

    companion object {
        /**
         * モブのステータスを取得します
         */
        fun from(entity: Entity): MobStatus? {
            return MobTypeStatus.from(entity)?.let { MobStatus(entity, it) }
        }
    }
}