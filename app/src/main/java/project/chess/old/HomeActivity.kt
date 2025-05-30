package project.chess.old

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import project.chess.R

class HomeActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_home)
    }
}