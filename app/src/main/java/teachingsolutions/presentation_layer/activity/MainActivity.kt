package teachingsolutions.presentation_layer.activity

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.pianomentor.R
import dagger.hilt.android.AndroidEntryPoint
import teachingsolutions.domain_layer.common.FileStorageManager

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val callback = onBackPressedDispatcher.addCallback(this) {
            if (!findNavController(R.id.nav_host_fragment).popBackStack()) {
                isEnabled = false
                FileStorageManager.clearCacheDir()
                onBackPressed()
            }
        }
        callback.isEnabled = true
    }

    override fun onDestroy() {
        super.onDestroy()
        FileStorageManager.clearCacheDir()
    }
}