package ph.edu.auf.gorospe.patrickjason.rpggame

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var hero: Hero
    private lateinit var enemy: Enemy
    private lateinit var game: Game
    private lateinit var tvActions: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val heroStats = CharacterStats(hp = 100.0, def = 20.0)
        val enemyStats = CharacterStats(hp = 100.0, def = 25.0)

        hero = Hero("Jason", heroStats)
        enemy = Enemy("Enemy", enemyStats)
        game = Game(hero, enemy)

        val tvGameState: TextView = findViewById(R.id.tvGameState)
        tvActions = findViewById(R.id.tvActions)
        val btnAttack: Button = findViewById(R.id.btnAttack)
        val btnDefend: Button = findViewById(R.id.btnDefend)
        val btnHeal: Button = findViewById(R.id.btnHeal)

        btnAttack.setOnClickListener {
            val damage = hero.attack(enemy)
            enemy.defend(damage)
            updateGameState(tvGameState)
            updateActions("Hero attacked Enemy for $damage damage")
        }

        btnDefend.setOnClickListener {
            // Implement defend logic
            updateActions("Hero defended")
        }

        btnHeal.setOnClickListener {
            hero.heal()
            updateGameState(tvGameState)
            updateActions("Hero healed")
        }

        updateGameState(tvGameState)
    }

    private fun updateGameState(tvGameState: TextView) {
        val gameState = "Hero HP: ${hero.stats.hp} | Enemy HP: ${enemy.stats.hp}"
        tvGameState.text = gameState
    }

    private fun updateActions(action: String) {
        tvActions.text = action
    }
}