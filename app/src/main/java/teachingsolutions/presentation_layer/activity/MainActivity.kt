package teachingsolutions.presentation_layer.activity

import android.os.Bundle
import android.view.Menu
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.pianomentor.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val callback = onBackPressedDispatcher.addCallback(this) {
            if (!findNavController(R.id.nav_host_fragment).popBackStack()) {
                isEnabled = false
                onBackPressed()
            }
        }
        callback.isEnabled = true
    }
}