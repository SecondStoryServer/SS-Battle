package me.syari.ss.battle.status

import me.syari.ss.battle.status.mob.MobStatus
import me.syari.ss.battle.status.player.PlayerStatus
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

interface EntityStatus {
    /**
     * ステータスマップ
     */
    val map: Map<StatusType, Float>

    companion object {
        /**
         * エンティティのステータスを取得します
         */
        fun from(entity: Entity): EntityStatus? {
            return when (entity) {
                is Player -> PlayerStatus.from(entity)
                else -> MobStatus.from(entity)
            }
        }
    }
}