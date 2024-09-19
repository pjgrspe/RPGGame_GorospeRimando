package ph.edu.auf.gorospe.patrickjason.rpggame

object CharacterConfig {
    val maxHealth = 200.00

    val heroStats = CharacterStats(
        hp = 100.00,
        def = 12.00,
        attack = 40.00,
        xp = 0
    )

    val enemyStats = CharacterStats(
        hp = 120.00,
        def = 15.00,
        attack = 30.00,
        xp = 0
    )

    val heroName = "Liwanag"
    val enemyName = "Kadiliman"
}