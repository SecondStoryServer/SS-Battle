package me.syari.ss.battle.status.player.event

import me.syari.ss.battle.status.player.PlayerStatus
import me.syari.ss.core.event.CustomEvent
import me.syari.ss.core.player.UUIDPlayer

open class StatusChangeEvent(val player: UUIDPlayer, val status: PlayerStatus): CustomEvent()