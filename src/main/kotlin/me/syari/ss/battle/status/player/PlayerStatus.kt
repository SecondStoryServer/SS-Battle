package me.syari.ss.battle.status.player

import me.syari.ss.battle.Main.Companion.battlePlugin
import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.battle.status.EntityStatus
import me.syari.ss.battle.status.OnDamageStatus
import me.syari.ss.battle.status.StatusType
import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.core.scheduler.CreateScheduler.runLater
import org.bukkit.OfflinePlayer
import org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH
import org.bukkit.attribute.AttributeModifier
import org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER

class PlayerStatus(
    val uuidPlayer: UUIDPlayer
): EntityStatus {
    val player
        get() = uuidPlayer.player

    val offlinePlayer
        get() = uuidPlayer.offlinePlayer

    private val statusChangeAdd = mutableMapOf<StatusChange.Cause, MutableList<StatusChange>>()
    private val statusChangeMulti = mutableMapOf<StatusChange.Cause, MutableList<StatusChange>>()

    /**
     * ステータスマップ
     */
    override var map = mutableMapOf<StatusType, Float>()

    /**
     * ステータスマップの更新
     */
    fun update() {
        val statusMap = defaultStatus.toMutableMap()
        val multi = mutableMapOf<StatusType, Float>()
        statusChangeAdd.values.forEach { list ->
            list.forEach { statusChange ->
                fun MutableMap<StatusType, Float>.increase(
                    type: StatusType,
                    value: Float
                ) {
                    put(type, getOrDefault(type, 0F) + value)
                }

                when (statusChange.changeType) {
                    StatusChange.Type.Add -> statusMap
                    StatusChange.Type.Multi -> multi
                }.increase(statusChange.statusType, statusChange.value)
            }
        }
        multi.forEach { (type, value) ->
            statusMap[type]?.let {
                statusMap[type] = it * (1 + value)
            }
        }
        map = statusMap
    }

    /**
     * ステータス変動の追加
     * @param cause 変動元
     * @param statusType ステータスの種類
     * @param value 変動する値
     * @param changeType 足し算か掛け算か
     */
    fun add(
        cause: StatusChange.Cause,
        statusType: StatusType,
        value: Float,
        changeType: StatusChange.Type
    ) {
        val data = StatusChange(statusType, value, changeType)
        statusChangeAdd.getOrPut(cause) { mutableListOf() }.add(data)
    }

    /**
     * ステータス変動の追加
     * @param cause 変動元
     * @param statusTypeList ステータスの種類
     * @param value 変動する値
     * @param changeType 足し算か掛け算か
     */
    fun add(
        cause: StatusChange.Cause,
        statusTypeList: List<StatusType>,
        value: Float,
        changeType: StatusChange.Type
    ) {
        statusTypeList.forEach { statusType ->
            add(cause, statusType, value, changeType)
        }
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
        cause: StatusChange.Cause,
        statusType: StatusType,
        value: Float,
        changeType: StatusChange.Type,
        effectTime: Int
    ) {
        add(cause, statusType, value, changeType)
        val data = StatusChange(statusType, value, changeType)
        runLater(battlePlugin, effectTime.toLong()) {
            statusChangeAdd[cause]?.remove(data)
            update()
        }?.let { data.removeTask.add(it) }
    }

    /**
     * 時限的なステータス変動の追加
     * @param cause 変動元
     * @param statusTypeList ステータスの種類
     * @param value 変動する値
     * @param changeType 足し算か掛け算か
     * @param effectTime 効果時間
     */
    fun add(
        cause: StatusChange.Cause,
        statusTypeList: List<StatusType>,
        value: Float,
        changeType: StatusChange.Type,
        effectTime: Int
    ) {
        statusTypeList.forEach { statusType ->
            add(cause, statusType, value, changeType, effectTime)
        }
    }

    /**
     * 指定変動元の効果消去
     * @param cause 変動元
     */
    fun clear(cause: StatusChange.Cause) {
        statusChangeAdd[cause]?.let { list ->
            list.forEach { it.cancelAllTask() }
            list.clear()
        }
    }

    /**
     * 全ての効果消去
     */
    fun clear() {
        statusChangeAdd.clear()
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
     * ダメージ時のステータスを計算する
     * @return [EntityStatus]
     */
    fun onDamage(
        damageElementType: ElementType,
        run: OnDamagePlayerStatus.() -> Unit
    ): OnDamageStatus {
        return OnDamagePlayerStatus(this, damageElementType).apply(run)
    }

    /**
     * ステータスをコピーする
     * @return [PlayerStatus]
     */
    internal fun clone(): PlayerStatus {
        return PlayerStatus(uuidPlayer).apply {
            this.statusChangeAdd.putAll(statusChangeAdd)
            this.statusChangeMulti.putAll(statusChangeMulti)
        }
    }

    /**
     * ステータスをコピーし、
     */

    companion object {
        private val defaultStatus: Map<StatusType, Float>

        init {
            defaultStatus = mutableMapOf(
                StatusType.MaxDamage to 1F, StatusType.MaxHealth to 20F, StatusType.RegenHealth to 1F
            ).apply {
                StatusType.allAttack.map { put(it, 1F) }
            }
        }

        private val statusMap = mutableMapOf<UUIDPlayer, PlayerStatus>()

        /**
         * プレイヤーのステータス
         */
        val OfflinePlayer.status
            get() = from(this)

        /**
         * プレイヤーのステータスを取得します
         */
        fun from(offlinePlayer: OfflinePlayer): PlayerStatus {
            val uuidPlayer = UUIDPlayer(offlinePlayer)
            return statusMap.getOrPut(uuidPlayer) { PlayerStatus(uuidPlayer) }
        }
    }
}