package me.syari.ss.battle.status

import me.syari.ss.battle.equipment.ElementType

/**
 * ステータスの種類
 */
sealed class StatusType(val display: String) {
    data class Attack(val elementType: ElementType): StatusType("&6攻撃力≪${elementType.display}&6≫")
    data class MagicAttack(val elementType: ElementType): StatusType("&6特殊攻撃力≪${elementType.display}&6≫")
    data class Defense(val elementType: ElementType): StatusType("&6防御力≪${elementType.display}&6≫")
    object CriticalChance: StatusType("&6会心率")
    object MaxDamage: StatusType("&6最大ダメージ")
    object MaxHealth: StatusType("&6最大体力")
    object RegenHealth: StatusType("&6自然治癒")
    object DodgeChance: StatusType("&6回避率")
    object MoveSpeed: StatusType("&6移動速度")
    object Luck: StatusType("&6幸運")
    object ExpBoost: StatusType("&6経験値ブースト")

    companion object {
        val allAttack: List<Attack>
        val allMagicAttack: List<MagicAttack>
        val allDefense: List<Defense>

        init {
            val allElement = ElementType.values()
            allAttack = allElement.map { Attack(it) }
            allMagicAttack = allElement.map { MagicAttack(it) }
            allDefense = allElement.map { Defense(it) }
        }
    }
}