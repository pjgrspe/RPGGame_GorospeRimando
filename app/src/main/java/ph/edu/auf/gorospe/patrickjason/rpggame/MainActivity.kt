package ph.edu.auf.gorospe.patrickjason.rpggame

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), GameCallback {

    private lateinit var tvActions: TextView
    private lateinit var tvHeroName: TextView
    private lateinit var tvHeroAttributes: TextView
    private lateinit var tvEnemyName: TextView
    private lateinit var tvEnemyAttributes: TextView
    private lateinit var ivTurnIndicator: ImageView
    private lateinit var btnAttack: Button
    private lateinit var btnDefend: Button
    private lateinit var btnHeal: Button
    private lateinit var ivHeroAction: ImageView
    private lateinit var ivEnemyAction: ImageView
    private lateinit var pbHeroXp: ProgressBar
    private lateinit var tvHeroLevel: TextView
    private lateinit var hero: Hero
    private lateinit var enemy: Enemy
    private lateinit var game: Game
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvActions = findViewById(R.id.tvActions)
        ivTurnIndicator = findViewById(R.id.ivTurnIndicator)
        ivHeroAction = findViewById(R.id.ivHeroActionEffect)
        ivEnemyAction = findViewById(R.id.ivEnemyActionEffect)
        tvHeroName = findViewById<TextView>(R.id.tvHeroName)
        tvHeroAttributes = findViewById<TextView>(R.id.tvHeroAttributes)
        tvEnemyName = findViewById<TextView>(R.id.tvEnemyName)
        tvEnemyAttributes = findViewById<TextView>(R.id.tvEnemyAttributes)
        pbHeroXp = findViewById(R.id.pbHeroXp)
        tvHeroLevel = findViewById(R.id.tvHeroLevel)

        // Initialize buttons
        btnAttack = findViewById(R.id.btnAttack)
        btnDefend = findViewById(R.id.btnDefend)
        btnHeal = findViewById(R.id.btnHeal)

        hero = Hero(CharacterConfig.heroName, CharacterConfig.heroStats)
        enemy = Enemy(CharacterConfig.enemyName, CharacterConfig.enemyStats)

        game = Game(hero, enemy, this)
        game.start()

        btnAttack.setOnClickListener {
            performHeroActionWithSingleClick("attack")
        }

        btnDefend.setOnClickListener {
            performHeroActionWithSingleClick("defend")
        }

        btnHeal.setOnClickListener {
            performHeroActionWithSingleClick("heal")
        }
    }

    // Perform hero action with a single click
    private fun performHeroActionWithSingleClick(action: String) {
        // Disable buttons right after an action is performed
        setButtonsEnabled(false)
        game.performHeroAction(action)
    }

    //Update the game state
    @SuppressLint("SetTextI18n")
    override fun onGameStateUpdate(heroHp: Double, enemyHp: Double) {
        runOnUiThread {
            tvHeroName.text = hero.name
            tvHeroAttributes.text = "HP: ${hero.stats.hp}\nAttack: ${hero.stats.attack}\nDefense: ${hero.stats.def}"

            tvEnemyName.text = enemy.name
            tvEnemyAttributes.text = "HP: ${enemy.stats.hp}\nAttack: ${enemy.stats.attack}\nDefense: ${enemy.stats.def}"

            updateHeroXpBar() // Ensure XP bar is updated
        }
    }

    //Display the action that was performed by the hero or enemy
    override fun onActionUpdate(action: String) {
        runOnUiThread {
            tvActions.text = action

            //Show action image
            when {
                action.contains("Hero attacks") -> {
                    showActionImage(ivHeroAction, R.drawable.attack)
                }
                action.contains("Hero defends") -> {
                    showActionImage(ivHeroAction, R.drawable.defend)
                }
                action.contains("Hero heals") -> {
                    showActionImage(ivHeroAction, R.drawable.heal)
                }
                action.contains("Enemy attacks") -> {
                    showActionImage(ivEnemyAction, R.drawable.attack)
                }
                action.contains("Enemy defends") -> {
                    showActionImage(ivEnemyAction, R.drawable.defend)
                }
                action.contains("Enemy heals") -> {
                    showActionImage(ivEnemyAction, R.drawable.heal)
                }
            }
        }
    }

    private fun showActionImage(imageView: ImageView, drawableRes: Int) {
        imageView.setImageResource(drawableRes)
        imageView.visibility = View.VISIBLE

        //Hide the image after 2 seconds
        handler.postDelayed({
            imageView.visibility = View.GONE
        }, 2000)
    }

    @SuppressLint("SetTextI18n")
    override fun onGameEnd(winner: String) {
        runOnUiThread {
            tvActions.text = "$winner wins!"
            setButtonsEnabled(false)

            //Show a dialog to reset the game
            AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("$winner wins! Do you want to play again?")
                .setPositiveButton("Yes") { _, _ ->
                    resetGame()

                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    //Indicate whose turn it is (hero or enemy) and enable/disable buttons accordingly
    @SuppressLint("SetTextI18n")
    override fun onTurnChange(turn: String) {
        runOnUiThread {
            val imageRes = if (turn == "hero") R.drawable.hero_fighting else R.drawable.enemy_fighting
            ivTurnIndicator.setImageResource(imageRes)

            if (turn == "hero") {
                tvActions.text = "Your turn! Choose an action."
                setButtonsEnabled(true) //Enable buttons when it's the hero's turn
            } else {
                tvActions.text = "Enemy's turn!"
                setButtonsEnabled(false) //Disable buttons during the enemy's turn
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun resetGame() {
        CharacterConfig.heroStats.reset()
        CharacterConfig.enemyStats.reset()
        hero = Hero(CharacterConfig.heroName, CharacterConfig.heroStats)
        enemy = Enemy(CharacterConfig.enemyName, CharacterConfig.enemyStats)
        game = Game(hero, enemy, this)
        game.start()
        setButtonsEnabled(true)
        tvActions.text = "Game reset! Your turn! Choose an action."
    }

    //enable/disable all buttons at once
    private fun setButtonsEnabled(enabled: Boolean) {
        btnAttack.isEnabled = enabled
        btnDefend.isEnabled = enabled
        btnHeal.isEnabled = enabled
    }

    @SuppressLint("SetTextI18n")
    private fun updateHeroXpBar() {
        while (hero.stats.xp >= 100) {
            hero.stats.xp -= 100
            hero.stats.level += 1
            hero.stats.hp += 20 // Add health when leveling up
        }
        pbHeroXp.progress = hero.stats.xp
        tvHeroLevel.text = "Level: ${hero.stats.level}"
    }
}

