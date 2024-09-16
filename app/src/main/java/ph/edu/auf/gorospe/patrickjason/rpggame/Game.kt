package ph.edu.auf.gorospe.patrickjason.rpggame

class Game(
    val hero: Hero,
    val enemy: Enemy
) : GameInterface {

    override fun start() {
        var turn = 1
        println("The battle begins!")
        println("${hero.name} vs ${enemy.name}")

        while (hero.isAlive() && enemy.isAlive()) {
            Thread.sleep(1000)
            println("\nTurn $turn")

            val heroAction = hero.chooseAction(enemy)
            val enemyAction = enemy.chooseAction(hero)

            if (hero.isAlive()) {
                when (heroAction) {
                    "attack" -> {
                        val attackDamage = hero.attack(enemy)
                        val finalDamage = if (enemyAction == "defend") {
                            enemy.defend(attackDamage)
                        } else {
                            enemy.stats.hp -= attackDamage
                            attackDamage
                        }
                        println("${hero.name} attacks ${enemy.name} for $finalDamage damage.")
                    }
                    "defend" -> {
                        println("${hero.name} is defending.")
                    }
                    "heal" -> {
                        val healAmount = hero.heal()
                        println("${hero.name} heals for $healAmount HP.")
                    }
                }
            }

            if (enemy.isAlive()) {
                when (enemyAction) {
                    "attack" -> {
                        val attackDamage = enemy.attack(hero)
                        val finalDamage = if (heroAction == "defend") {
                            hero.defend(attackDamage)
                        } else {
                            hero.stats.hp -= attackDamage
                            attackDamage
                        }
                        println("${enemy.name} attacks ${hero.name} for $finalDamage damage.")
                    }
                    "defend" -> {
                        println("${enemy.name} is defending.")
                    }
                    "heal" -> {
                        val healAmount = enemy.heal()
                        println("${enemy.name} heals for $healAmount HP.")
                    }
                }
            }

            if (!hero.isAlive()) {
                println("${hero.name} has been defeated!")
                break
            }

            if (!enemy.isAlive()) {
                println("${enemy.name} has been defeated!")
                break
            }

            println("${hero.name} HP: ${hero.stats.hp} | ${enemy.name} HP: ${enemy.stats.hp}")
            turn++
        }

        if (hero.isAlive()) {
            println("${hero.name} wins!")
        } else if (enemy.isAlive()) {
            println("${enemy.name} wins!")
        }
    }
}