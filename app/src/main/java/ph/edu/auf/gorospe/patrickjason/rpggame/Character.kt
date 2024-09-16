package ph.edu.auf.gorospe.patrickjason.rpggame

import java.util.Scanner

abstract class Character(
    val name: String,
    var stats: CharacterStats
) : CharacterInterface {

    override fun attack(opponent: Character): Double {
        val attackPower = (15..30).random().toDouble()
        return attackPower
    }

    override fun defend(damage: Double): Double {
        val reducedDamage = damage - stats.def
        val actualDamage = if (reducedDamage > 0) reducedDamage else 0.0
        stats.hp -= actualDamage
        return actualDamage
    }

    override fun heal(): Double {
        val healAmount = (10..20).random().toDouble()
        stats.hp += healAmount
        return healAmount
    }

    fun isAlive(): Boolean = stats.hp > 0

    override fun chooseAction(opponent: Character): String {
        if (this is Hero) {
            val scanner = Scanner(System.`in`)
            println("Choose an action: (1) Attack (2) Defend (3) Heal")
            return when (scanner.nextInt()) {
                1 -> "attack"
                2 -> "defend"
                3 -> "heal"
                else -> "attack"
            }
        } else {
            val hpPercentage = stats.hp / 100.0
            val opponentHpPercentage = opponent.stats.hp / 100.0

            return when {
                hpPercentage < 0.2 -> "heal"
                opponentHpPercentage <= 0.1 -> "attack"
                hpPercentage < 0.5 -> listOf("heal", "defend").random()
                else -> "attack"
            }
        }
    }
}