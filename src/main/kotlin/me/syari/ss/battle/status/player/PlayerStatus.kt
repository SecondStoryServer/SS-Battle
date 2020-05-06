package me.syari.ss.battle.status.player

import me.syari.ss.battle.Main.Companion.battlePlugin
import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.battle.status.EntityStatus
import me.syari.ss.battle.status.player.event.StatusChangeAddEvent
import me.syari.ss.battle.status.player.event.StatusChangeClearEvent
import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.core.scheduler.CustomScheduler.runLater
import org.bukkit.OfflinePlayer
import org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH
import org.bukkit.attribute.AttributeModifier
import org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER

class PlayerStatus(val uuidPlayer: UUIDPlayer, statusChangeList: Map<StatusChange.Cause, MutableList<StatusChange>>): EntityStatus {
    val player
        get() = uuidPlayer.player

    val offlinePlayer
        get() = uuidPlayer.offlinePlayer

    private val statusChangeList = statusChangeList.toMutableMap()

    /**
     * ダメージの属性
     */
    override var damageElementType: ElementType? = null

    /**
     * ステータスマップ
     */
    override var map = mutableMapOf<StatusType, Float>()

    /**
     * ステータスマップの取得
     * @return [Map]<[StatusType], [Float]>
     */
    fun get(): Map<StatusType, Float> {
        val add = defaultStatus.toMutableMap()
        val multi = mutableMapOf<StatusType, Float>()
        statusChangeList.values.forEach { list ->
            list.forEach { statusChange ->
                fun MutableMap<StatusType, Float>.increase(type: StatusType, value: Float) {
                    put(type, getOrDefault(type, 0F) + value)
                }

                when (statusChange.changeType) {
                    StatusChange.Type.Add -> add
                    StatusChange.Type.Multi -> multi
                }.increase(statusChange.statusType, statusChange.value)
            }
        }
        multi.forEach { (type, value) ->
            add[type]?.let {
                add[type] = it * (1 + value)
            }
        }
        return add
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
        StatusChangeAddEvent(uuidPlayer, this, data).callEvent()
    }

    /**
     * 時限的なステータス変動の追加
     * @param cause 変動元
     * @param statusType ステータスの種類
     * @param value 変動する値
     * @param changeType 足し算か掛け算か
     * @param effectTime 効果時間
     */
    fun add(
        cause: StatusChange.Cause, statusType: StatusType, value: Float, changeType: StatusChange.Type, effectTime: Int
    ) {
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
        statusChangeList[cause]?.let { list ->
            list.forEach { it.cancelAllTask() }
            list.clear()
        }
        StatusChangeClearEvent(uuidPlayer, this, cause).callEvent()
    }

    /**
     * 全ての効果消去
     */
    fun clear() {
        statusChangeList.clear()
        StatusChangeClearEvent(uuidPlayer, this, null).callEvent()
    }

    private var lastChangeHealthModifier: AttributeModifier? = null

    private inline val maxHealthAttribute
        get() = player?.getAttribute(GENERIC_MAX_HEALTH)

    /**
     * プレイヤーの最大体力
     */
    var maxHealth: Double
        get() = maxHealthAttribute?.value ?: defaultStatus.getOrDefault(StatusType.MaxHealth, 1F).toDouble()
        set(value) {
            maxHealthAttribute?.let { attribute ->
                lastChangeHealthModifier?.let {
                    attribute.removeModifier(it)
                }
                val changeHealthModifier = AttributeModifier("changeHealth", value, ADD_NUMBER)
                attribute.addModifier(changeHealthModifier)
                lastChangeHealthModifier = changeHealthModifier
            }
        }

    /**
     * プレイヤーの現在体力
     */
    var health: Double
        get() = player?.health ?: maxHealth
        set(value) {
            player?.let {
                val maxHealth = maxHealth
                it.health = when {
                    value < 0 -> 0.0
                    maxHealth < value -> maxHealth
                    else -> value
                }
            }
        }

    /**
     * ステータスをコピーする
     * @return [PlayerStatus]
     */
    fun clone(): PlayerStatus {
        return PlayerStatus(uuidPlayer, statusChangeList.toMap())
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
                return statusMap.getOrPut(uuidPlayer) { PlayerStatus(uuidPlayer, mapOf()) }
            }
    }
}