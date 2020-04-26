package me.syari.ss.battle.status.player

import me.syari.ss.core.scheduler.CustomTask

data class StatusChange(
        val statusType: StatusType,
        val value: Float,
        val changeType: Type
) {
    val removeTask = mutableSetOf<CustomTask>()

    fun cancelAllTask() {
        removeTask.forEach {
            it.cancel()
        }
    }

    enum class Cause {
        Job,
        Equipment,
        ActiveSkill,
        PassiveSkill,
        GuildBuff
    }

    enum class Type {
        Add,
        Multi
    }
}