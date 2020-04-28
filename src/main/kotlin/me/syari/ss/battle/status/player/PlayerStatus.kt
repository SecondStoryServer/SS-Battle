package me.syari.ss.battle.status.player

import me.syari.ss.battle.Main.Companion.battlePlugin
import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.battle.status.EntityStatus
import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.core.scheduler.CustomScheduler.runLater
import org.bukkit.OfflinePlayer

class PlayerStatus : EntityStatus {
    private val statusChangeList = mutableMapOf<StatusChange.Cause, MutableList<StatusChange>>()

    /**
     * ダメージの属性
     */
    override var damageElementType: ElementType? = null

    /**
     * ステータスマップの取得
     * @return [Map]<[StatusType], [Float]>
     */
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

    /**
     * ステータス変動の追加
     * @param cause 変動元
     * @param statusType ステータスの種類
     * @param value 変動する値
     * @param changeType 足し算か掛け算か
     */
    fun add(cause: StatusChange.Cause, statusType: StatusType, value: Float, changeType: StatusChange.Type) {
        val data = StatusChange(statusType, value, changeType)
        statusChangeList.getOrPut(cause) { mutableListOf() }.add(data)
    }

    /**
     * 時限的なステータス変動の追加
     * @param cause 変動元
     * @param statusType ステータスの種類
     * @param value 変動する値
     * @param changeType 足し算か掛け算か
     * @param effectTime 効果時間
     */
    fun add(cause: StatusChange.Cause, statusType: StatusType, value: Float, changeType: StatusChange.Type, effectTime: Int) {
        add(cause, statusType, value, changeType)
        val data = StatusChange(statusType, value, changeType)
        runLater(battlePlugin, effectTime.toLong()) {
            statusChangeList[cause]?.remove(data)
        }?.let { data.removeTask.add(it) }
    }

    /**
     * 指定変動元の効果消去
     * @param cause 変動元
     */
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

        /**
         * プレイヤーのステータス
         */
        val OfflinePlayer.status
            get(): PlayerStatus {
                val uuidPlayer = UUIDPlayer(this)
                return statusMap.getOrPut(uuidPlayer) { PlayerStatus() }
            }
    }
}