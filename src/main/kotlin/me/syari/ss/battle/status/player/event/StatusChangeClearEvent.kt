package me.syari.ss.battle.status.player.event

import me.syari.ss.battle.status.player.PlayerStatus
import me.syari.ss.battle.status.player.StatusChange
import me.syari.ss.core.player.UUIDPlayer

class StatusChangeClearEvent(player: UUIDPlayer, status: PlayerStatus, val cause: StatusChange.Cause?):
    StatusChangeEvent(
    player, status
)