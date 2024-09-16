package ph.edu.auf.gorospe.patrickjason.rpggame

interface CharacterInterface {
    fun attack(opponent: Character): Double
    fun defend(damage: Double): Double
    fun heal(): Double
    fun chooseAction(opponent: Character): String
}