package me.syari.ss.battle.status.player

import me.syari.ss.battle.equipment.ElementType

/**
 * ステータスの種類
 */
sealed class StatusType(val display: String) {
    object BaseAttack: StatusType("&6攻撃力")
    object BaseDefense: StatusType("&6防御力")
    data class ElementAttack(val elementType: ElementType): StatusType("&6属性攻撃力≪${elementType.display}&6≫")
    data class ElementDefense(val elementType: ElementType): StatusType("&6属性防御力≪${elementType.display}&6≫")
    object SkillAttack: StatusType("&6特殊攻撃力")
    object AttackSpeed: StatusType("&6攻撃速度")
    object CriticalChance: StatusType("&6会心率")
    object MaxDamage: StatusType("&6最大ダメージ")
    object MaxHealth: StatusType("&6最大体力")
    object RegenHealth: StatusType("&6自然治癒")
    object DodgeChance: StatusType("&6回避率")
    object MoveSpeed: StatusType("&6移動速度")
    object Luck: StatusType("&6幸運")
    object ExpBoost: StatusType("&6経験値ブースト")
}