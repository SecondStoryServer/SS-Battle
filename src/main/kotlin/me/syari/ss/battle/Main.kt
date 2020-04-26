package me.syari.ss.battle

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object {
        lateinit var battlePlugin: JavaPlugin
    }

    override fun onEnable() {
        battlePlugin = this
    }
}