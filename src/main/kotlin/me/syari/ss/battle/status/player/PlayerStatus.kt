package me.syari.ss.battle.status.player

import me.syari.ss.battle.Main.Companion.battlePlugin
import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.battle.status.EntityStatus
import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.core.scheduler.CustomScheduler.runLater
import org.bukkit.OfflinePlayer

class PlayerStatus : EntityStatus {
    private val statusChangeList = mutableMapOf<StatusChange.Cause, MutableList<StatusChange>>()

    override var damageElementType: ElementType? = null

    override fun get(): Map<StatusType, Float> {
        val status = defaultStatus.toMutableMap()
        val multi = mutableMapOf<StatusType, Float>()
        statusChangeList.values.forEach { list ->
            list.forEach { statusChange ->
                fun MutableMap<StatusType, Float>.increase(type: StatusType, value: Float) {
                    put(type, getOrDefault(type, 0F) + value)
                }

                when (statusChange.changeType) {
                    StatusChange.Type.Add -> status
                    StatusChange.Type.Multi -> multi
                }.increase(statusChange.statusType, statusChange.value)
            }
        }
        multi.forEach { (type, value) ->
            status[type]?.let {
                status[type] = it * (1 + value)
            }
        }
        return status
    }

    private fun add(cause: StatusChange.Cause, data: StatusChange) {
        statusChangeList.getOrPut(cause) { mutableListOf() }.add(data)
    }

    private fun add(cause: StatusChange.Cause, data: StatusChange, effectTime: Int) {
        add(cause, data)
        runLater(battlePlugin, effectTime.toLong()) {
            statusChangeList[cause]?.remove(data)
        }?.let { data.removeTask.add(it) }
    }

    fun add(cause: StatusChange.Cause, statusType: StatusType, value: Float, changeType: StatusChange.Type) {
        add(cause, StatusChange(statusType, value, changeType))
    }

    fun add(cause: StatusChange.Cause, statusType: StatusType, value: Float, changeType: StatusChange.Type, effectTime: Int) {
        add(cause, StatusChange(statusType, value, changeType), effectTime)
    }

    fun clear(cause: StatusChange.Cause) {
        statusChangeList[cause]?.forEach { it.cancelAllTask() }
        statusChangeList.clear()
    }

    companion object {
        private val defaultStatus = mapOf(
                StatusType.BaseAttack to 1F,
                StatusType.MaxDamage to 1F,
                StatusType.MaxHealth to 20F,
                StatusType.RegenHealth to 1F,
                StatusType.MoveSpeed to 0.2F
        )

        private val statusMap = mutableMapOf<UUIDPlayer, PlayerStatus>()

        val OfflinePlayer.status
            get(): PlayerStatus {
                val uuidPlayer = UUIDPlayer(this)
                return statusMap.getOrPut(uuidPlayer) { PlayerStatus() }
            }
    }
}