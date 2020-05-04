package me.syari.ss.battle.status

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.battle.status.player.StatusType

interface EntityStatus {
    /**
     * ダメージの属性
     */
    val damageElementType: ElementType?

    /**
     * ステータスマップ
     */
    val map: Map<StatusType, Float>
}