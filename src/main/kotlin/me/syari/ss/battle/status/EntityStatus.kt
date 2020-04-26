package me.syari.ss.battle.status

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.battle.status.player.StatusType

interface EntityStatus {
    /**
     * ダメージの属性
     */
    var damageElementType: ElementType?

    /**
     * ステータスマップの取得
     * @return [Map]<[StatusType], [Float]>
     */
    fun get(): Map<StatusType, Float>
}