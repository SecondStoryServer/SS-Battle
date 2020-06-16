package me.syari.ss.battle.status.player

import me.syari.ss.battle.status.StatusType
import me.syari.ss.core.scheduler.CustomTask

/**
 * ステータス変動
 */
data class StatusChange(
    val statusType: StatusType,
    val value: Float,
    val changeType: Type
) {
    internal val removeTask = mutableSetOf<CustomTask>()

    internal fun cancelAllTask() {
        removeTask.forEach {
            it.cancel()
        }
    }

    /**
     * 変動元
     */
    enum class Cause {
        Equipment,
        ActiveSkill,
        PassiveSkillMain,
        PassiveSkillExtra,
        GuildBuff,
        GlobalBuff
    }

    /**
     * 変動の仕方
     */
    enum class Type {
        Add,
        Multi
    }
}