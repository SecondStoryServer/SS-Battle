package me.syari.ss.battle.equipment

enum class ElementType(val display: String) {
    Fire("&c火"),
    Water("&b水"),
    Wood("&a木"),
    Holy("&e聖"),
    Dark("&d闇");

    companion object {
        private val elementEffect by lazy {
            mapOf(
                    Fire to Water to 1.5F,
                    Fire to Wood to 0.7F,
                    Water to Wood to 1.5F,
                    Water to Fire to 0.7F,
                    Wood to Fire to 1.5F,
                    Wood to Water to 0.7F,
                    Dark to Fire to 0.8F,
                    Dark to Water to 0.8F,
                    Dark to Wood to 0.8F,
                    Dark to Holy to 2.0F,
                    Holy to Dark to 0.5F
            )
        }

        fun getDefenseRate(attack: ElementType, defense: ElementType): Float {
            return elementEffect.getOrDefault(attack to defense, 1.0F)
        }
    }
}