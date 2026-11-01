package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.repository.HermeticRepository
import com.example.ui.HermeticaApp
import com.example.ui.particles.AlchemicalParticleContainer
import com.example.ui.theme.GrimoireCover
import com.example.ui.theme.HermeticaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = HermeticRepository(applicationContext)

        setContent {
            HermeticaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = GrimoireCover
                ) {
                    AlchemicalParticleContainer {
                        HermeticaApp(repository = repository)
                    }
                }
            }
        }
    }
}
