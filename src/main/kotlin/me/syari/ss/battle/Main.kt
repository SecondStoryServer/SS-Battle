package me.syari.ss.battle

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object {
        internal lateinit var battlePlugin: JavaPlugin
    }

    override fun onEnable() {
        battlePlugin = this
    }
}