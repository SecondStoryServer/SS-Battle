package me.syari.ss.battle.status

import me.syari.ss.battle.equipment.ElementType

interface OnDamageStatus {
    /**
     * ダメージの属性
     */
    val damageElementType: ElementType

    /**
     * ステータスマップ
     */
    val map: Map<StatusType, Float>
}