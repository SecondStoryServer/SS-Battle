package me.syari.ss.battle.status.player

import me.syari.ss.battle.equipment.ElementType

sealed class StatusType(val display: String) {
    object BaseAttack : StatusType("攻撃力")
    object BaseDefense : StatusType("防御力")
    data class ElementAttack(val elementType: ElementType) : StatusType("属性攻撃力 ${elementType.display}")
    data class ElementDefense(val elementType: ElementType) : StatusType("属性防御力 ${elementType.display}")
    object CriticalChance : StatusType("会心率")
    object MaxDamage : StatusType("最大ダメージ")
    object MaxHealth : StatusType("最大体力")
    object RegenHealth : StatusType("体力回復")
    object MoveSpeed : StatusType("移動速度")
}