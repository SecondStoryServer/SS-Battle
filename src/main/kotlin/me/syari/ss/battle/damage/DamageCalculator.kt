package me.syari.ss.battle.damage

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.battle.status.EntityStatus
import me.syari.ss.battle.status.player.StatusType
import kotlin.random.Random

object DamageCalculator {
    /**
     * ダメージを計算します
     * @param attacker ダメージを与えるエンティティのステータス
     * @param victim ダメージを受けるエンティティのステータス
     * @return [Float]
     */
    fun getDamage(attacker: EntityStatus, victim: EntityStatus?): Float {
        val damageElementType = attacker.damageElementType
        val attackerStatus = attacker.map
        val victimStatus = victim?.map
        var damage = getAttack(damageElementType, attackerStatus) - getDefense(damageElementType, victimStatus)
        return if (0F < damage) {
            attackerStatus[StatusType.CriticalChance]?.let { chance ->
                if (Random.nextFloat() < chance) {
                    damage *= 1.5F
                }
            }
            attackerStatus[StatusType.MaxDamage]?.let { maxDamage ->
                if (maxDamage < damage) {
                    damage = maxDamage
                }
            }
            damage
        } else {
            0F
        }
    }

    private fun getDefense(damageElementType: ElementType, victimStatus: Map<StatusType, Float>?): Float {
        var defense = 0F
        victimStatus?.forEach { (type, value) ->
            if (type is StatusType.Defense) {
                defense += value * ElementType.getDefenseRate(damageElementType, type.elementType)
            } else if (type is StatusType.BaseDefense) {
                defense += value
            }
        }
        return defense
    }

    private fun getBaseAttack(attackerStatus: Map<StatusType, Float>): Float {
        return attackerStatus.getOrDefault(StatusType.BaseAttack, 0F)
    }

    private fun getAttack(attackerStatus: Map<StatusType, Float>, elementType: ElementType): Float {
        return attackerStatus.getOrDefault(StatusType.Attack(elementType), 0F)
    }

    private fun getAttack(damageElementType: ElementType, attackerStatus: Map<StatusType, Float>): Float {
        return getBaseAttack(attackerStatus) + getAttack(attackerStatus, damageElementType)
    }
}