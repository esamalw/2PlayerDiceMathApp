package com.example.assignmentnum1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.assignmentnum1.models.Dice
import kotlin.random.Random

class MainActivity : AppCompatActivity() {



    private lateinit var roll_die_btn : Button
    private lateinit var guess_btn : Button
    private lateinit var dice_iv : ImageView
    private lateinit var question_tv : TextView
    private lateinit var ans_edt : EditText
    private lateinit var current_player_tv : TextView
    private lateinit var player_1_total_tv : TextView
    private lateinit var player_2_total_tv : TextView
    private lateinit var current_jackpot_tv : TextView

    private val diceValuesList = ArrayList<Dice>()

    private var firstNum: Int = 0
    private var secondNum: Int = 0
    private var diceNumber: Int = 0
    private var rolledAgain: Int = 0
    private var playerOneScore: Int = 0
    private var playerTwoScore: Int = 0
    private var jackpotScore: Int = 5
    private var currentPlayer: Int = 1 //1 == P1 &&& 2 == P2

    private fun addPlayerPoints(jackpot:Boolean){

        if(jackpot){
            if(currentPlayer==1){
                playerOneScore += jackpotScore
                player_1_total_tv.text = "Player 1 total: $playerOneScore"
            }else if(currentPlayer==2){
                playerTwoScore += jackpotScore
                player_2_total_tv.text = "Player 2 total: $playerTwoScore"
            }
            jackpotScore = 5
            current_jackpot_tv.text = "Current Jackpot: $jackpotScore"

        }else{
            if(currentPlayer==1){
                if(rolledAgain==0){
                    playerOneScore ++
                    player_1_total_tv.text = "Player 1 total: $playerOneScore"
                }else{
                    playerOneScore += rolledAgain
                    player_1_total_tv.text = "Player 1 total: $playerOneScore"
                }

            }else if(currentPlayer==2){
                if(rolledAgain==0){
                    playerTwoScore ++
                    player_2_total_tv.text = "Player 2 total: $playerTwoScore"
                }else{
                    playerTwoScore += rolledAgain
                    player_2_total_tv.text = "Player 2 total: $playerTwoScore"
                }

            }
        }

        if(playerOneScore>=20){
            showGameEndDialog(this@MainActivity,"Player 1 has won the game with $playerOneScore")
        }else if(playerTwoScore>=20){
            showGameEndDialog(this@MainActivity,"Player 2 has won the game with $playerTwoScore")
        }


    }

    private fun switchPlayers(){
        if(currentPlayer==1){
            current_player_tv.text = "Current Player: P2"
            currentPlayer = 2
        }else if(currentPlayer==2){
            currentPlayer = 1
            current_player_tv.text = "Current Player: P1"
        }
    }

    private fun showGameEndDialog(context: Context, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
            .setCancelable(false) // This makes the dialog uncancelable
            .setPositiveButton("OK") { dialog, _ ->

                dialog.dismiss()
                val i = Intent(this, StartGameActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Populate Dice vlaues in list
        populateDiceValueList()

        current_jackpot_tv = findViewById(R.id.current_jackpot_tv)
        current_jackpot_tv.text = "Current Jackpot: $jackpotScore"
        player_2_total_tv = findViewById(R.id.player_2_total_tv)
        player_1_total_tv = findViewById(R.id.player_1_total_tv)
        ans_edt = findViewById(R.id.ans_edt)
        current_player_tv = findViewById(R.id.current_player_tv)
        question_tv = findViewById(R.id.question_tv)
        dice_iv = findViewById(R.id.dice_iv)
        guess_btn = findViewById(R.id.guess_btn)
        guess_btn.setOnClickListener {

            if(ans_edt.text.isEmpty()){

                ans_edt.error = "Answer cannot be empty"
                return@setOnClickListener
            }
            if(diceValuesList[diceNumber].value == 1){
                var result:Int = 0
                val check: Int = firstNum + secondNum
                if(check==ans_edt.text.toString().toInt()){
                    Toast.makeText(this@MainActivity,"correct Answer",Toast.LENGTH_SHORT).show()
                    addPlayerPoints(false)
                }else{
                    Toast.makeText(this@MainActivity,"wrong Answer",Toast.LENGTH_SHORT).show()
                    jackpotScore++
                }
            }else if(diceValuesList[diceNumber].value == 2){

                val check: Int = firstNum - secondNum
                if(check==ans_edt.text.toString().toInt()){
                    Toast.makeText(this@MainActivity,"correct Answer",Toast.LENGTH_SHORT).show()
                    addPlayerPoints(false)
                }else{
                    Toast.makeText(this@MainActivity,"wrong Answer",Toast.LENGTH_SHORT).show()
                    jackpotScore++
                }
            }else if(diceValuesList[diceNumber].value == 3){
                val check: Int = firstNum * secondNum
                if(check==ans_edt.text.toString().toInt()){
                    Toast.makeText(this@MainActivity,"correct Answer",Toast.LENGTH_SHORT).show()
                    addPlayerPoints(false)
                }else{
                    Toast.makeText(this@MainActivity,"wrong Answer",Toast.LENGTH_SHORT).show()
                    jackpotScore++
                }
            }else if(diceValuesList[diceNumber].value == 6){
                val check: Int = firstNum + secondNum
                if(check==ans_edt.text.toString().toInt()){
                    Toast.makeText(this@MainActivity,"correct Answer",Toast.LENGTH_SHORT).show()
                    addPlayerPoints(true)
                }else{
                    Toast.makeText(this@MainActivity,"wrong Answer",Toast.LENGTH_SHORT).show()
                    jackpotScore++
                }
            }

            switchPlayers()

            question_tv.text = "Roll Dice"
            ans_edt.setText("")
            current_jackpot_tv.text = "Current Jackpot: $jackpotScore"

        }


        roll_die_btn = findViewById(R.id.roll_die_btn)
        roll_die_btn.setOnClickListener{

            diceNumber = genDieRandomNumber()
            dice_iv.setImageResource(diceValuesList[diceNumber].drawableId)
            //Toast.makeText(this@MainActivity,diceNumber.toString(),Toast.LENGTH_SHORT).show()

            rolledAgain = 0
            when (diceValuesList[diceNumber].value) {
                1 -> {//addition
                    ans_edt.isEnabled = true
                    firstNum = genAddSubRandomNumber()
                    secondNum = genAddSubRandomNumber()

                    question_tv.setText("$firstNum + $secondNum")
                }
                2 -> {//substraction
                    ans_edt.isEnabled = true
                    firstNum = genAddSubRandomNumber()
                    secondNum = genSmallerAddSubRandomNumber(firstNum)

                    question_tv.setText("$firstNum - $secondNum")
                }
                3 -> {//multiplication
                    ans_edt.isEnabled = true
                    firstNum = genMulRandomNumber()
                    secondNum = genMulRandomNumber()

                    question_tv.setText("$firstNum x $secondNum")
                }
                4 -> {//Roll Again
                    ans_edt.isEnabled = false
                    Toast.makeText(this@MainActivity,"Current Player: $currentPlayer can roll again",Toast.LENGTH_SHORT).show()
                    rolledAgain++

                }
                5 -> {//lose turn and switch to other player
                    ans_edt.isEnabled = false
                    Toast.makeText(this@MainActivity,"Turn lost, other player can roll now",Toast.LENGTH_SHORT).show()
                    switchPlayers()
                }
                6 -> {//Jackpot
                    ans_edt.isEnabled = true
                    firstNum = genAddSubRandomNumber()
                    secondNum = genAddSubRandomNumber()

                    question_tv.setText("$firstNum + $secondNum")

                }
            }




        }
    }


    //Dice random numbers
    private fun genDieRandomNumber(): Int {
        val random = Random
        return random.nextInt(6)
    }

    //Add random numbers
    private fun genAddSubRandomNumber(): Int {
        val random = Random
        return random.nextInt(99)
    }
    //Add random numbers
    private fun genSmallerAddSubRandomNumber(value:Int): Int {
        val random = Random
        return random.nextInt(value)
    }

    //Multiply random numbers
    private fun genMulRandomNumber(): Int {
        val random = Random
        return random.nextInt(20)
    }

    private fun populateDiceValueList(){
        diceValuesList.add(Dice(R.drawable.dice_1, 1))
        diceValuesList.add(Dice(R.drawable.dice_2, 2))
        diceValuesList.add(Dice(R.drawable.dice_3, 3))
        diceValuesList.add(Dice(R.drawable.dice_4, 4))
        diceValuesList.add(Dice(R.drawable.dice_5, 5))
        diceValuesList.add(Dice(R.drawable.dice_6, 6))
    }
}