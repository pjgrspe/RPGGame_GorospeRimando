package ph.edu.auf.gorospe.patrickjason.rpggame

import android.os.Handler
import android.os.Looper
//import kotlinx.coroutines.delay

interface GameCallback {
    fun onGameStateUpdate(heroHp: Double, enemyHp: Double)
    fun onActionUpdate(action: String)
    fun onGameEnd(winner: String)
    fun onTurnChange(turn: String)
}

class Game(private val hero: Hero, private val enemy: Enemy, private val callback: GameCallback) {

    private var currentTurn: String = "hero"
    private var gameEnded: Boolean = false //Add flag to indicate if the game has ended
    private var heroDefenseTurns: Int = 0
    private var enemyDefenseTurns: Int = 0

    fun start() {
        heroDefenseTurns = 0
        enemyDefenseTurns = 0
        callback.onGameStateUpdate(hero.stats.hp, enemy.stats.hp)
        callback.onTurnChange(currentTurn)
    }

    //Perform action for the hero
    fun performHeroAction(action: String) {

        if (gameEnded) return //Check if the game has ended
        when (action) {
            "attack" -> {
                val damage = hero.attack(enemy)    // Hero attacks and deals damage
                val diceRoll = (1..10).random()
                val actualDamage: Double
                when (diceRoll){
                    10 -> {
                        actualDamage = enemy.defend(damage * 2)
                        callback.onActionUpdate("Hero attacks! Critical hit! Deals $actualDamage damage to the enemy.")
                    }
                    1 -> {
                        callback.onActionUpdate("Hero attacks! Missed!")
                    }
                    else -> {
                        actualDamage = enemy.defend(damage)
                        callback.onActionUpdate("Hero attacks! Deals $actualDamage damage to the enemy.")
                    }
                }
            }
            "defend" -> {
                if (heroDefenseTurns > 0) {
                    callback.onActionUpdate("Hero is already defending!")
                } else {
                    heroDefenseTurns = (1..4).random() // Randomize defense turns
                    hero.stats.def += 20                    // Hero increases defense
                    callback.onActionUpdate("Hero defends! Defense increased by 20. for $heroDefenseTurns turns")
                }
            }
            "heal" -> {
                if (hero.stats.hp >= CharacterConfig.maxHealth) {
                    callback.onActionUpdate("Hero is already at full health!")
                    return
                }
                val healAmount = hero.heal()       // Hero heals
                callback.onActionUpdate("Hero heals for $healAmount HP.")
            }
            else -> {
                callback.onActionUpdate("Invalid action!")
                return
            }
        }

        callback.onGameStateUpdate(hero.stats.hp, enemy.stats.hp)
        //Delay the switch
        Handler(Looper.getMainLooper()).postDelayed({
            if (heroDefenseTurns > 0) {
                heroDefenseTurns--
                if (heroDefenseTurns == 0) {
                    hero.stats.def -= 20
                    callback.onActionUpdate("Hero's defense state ended")
                }
            }
            checkGameEnd()
            switchTurn()
        }, 2000)
    }

    //Perform action for the enemy automatically
    private fun performEnemyAction() {
        if (gameEnded) return //Check if the game has ended

        if (currentTurn != "enemy") {
            callback.onActionUpdate("Wait for the enemy's turn!")
            return
        }

        //Enemy AI decides action
        val action = enemy.chooseAction(hero)
        when (action) {
            "attack" -> {
                val damage = enemy.attack(hero)    // Enemy attacks and deals damage
                val diceRoll = (1..10).random()
                val actualDamage: Double
                when (diceRoll) {
                    10 -> {
                        actualDamage = hero.defend(damage * 2)
                        callback.onActionUpdate("Enemy attacks! Critical hit! Deals $actualDamage damage to the hero.")
                    }
                    1 -> {
                        callback.onActionUpdate("Enemy attacks! Missed!")
                    }
                    else -> {
                        actualDamage = hero.defend(damage)
                        callback.onActionUpdate("Enemy attacks! Deals $actualDamage damage to the hero.")
                    }
                }
            }
            "defend" ->
                if (enemyDefenseTurns > 0) {
                    callback.onActionUpdate("Enemy is already defending!")
                } else {
                    enemyDefenseTurns = (1..4).random() // Randomize defense turns
                    enemy.stats.def += 20                    // Hero increases defense
                    callback.onActionUpdate("Enemy defends! Defense increased by 20. for $enemyDefenseTurns turns")
                }
            "heal" -> {
                if (enemy.stats.hp >= CharacterConfig.maxHealth) {
                    callback.onActionUpdate("Enemy is already at full health!")
                    return
                }
                val healAmount = enemy.heal()      // Enemy heals
                callback.onActionUpdate("Enemy heals for $healAmount HP.")
            }
        }

        callback.onGameStateUpdate(hero.stats.hp, enemy.stats.hp)
        //Delay turn switching
        Handler(Looper.getMainLooper()).postDelayed({
            if (enemyDefenseTurns > 0) {
                enemyDefenseTurns--
                if (enemyDefenseTurns == 0) {
                    enemy.stats.def -= 20
                    callback.onActionUpdate("Enemy's defense state ended")
                }
            }
            checkGameEnd()
            switchTurn()
        }, 2000)
    }

    //Switch turns between hero and enemy
    private fun switchTurn() {
        if (gameEnded) return //Check if the game has ended

        currentTurn = if (currentTurn == "hero") "enemy" else "hero"
        callback.onTurnChange(currentTurn)

        // Thinking time for the enemy
        if (currentTurn == "enemy") {
            callback.onActionUpdate("Enemy is deciding its action...")
            Handler(Looper.getMainLooper()).postDelayed({
                performEnemyAction() //Enemy acts after a short delay
            }, 2000)
        }
    }

    private fun checkGameEnd() {
        when {
            hero.stats.hp <= 0 -> {
                gameEnded = true
                callback.onGameEnd("Enemy")
                return
            }
            enemy.stats.hp <= 0 -> {
                hero.stats.xp += 50
                callback.onGameStateUpdate(hero.stats.hp, enemy.stats.hp) //Ensure XP update is reflected
                gameEnded = true
                callback.onGameEnd("Hero")
                return
            }
        }
    }
}
