package teachingsolutions.domain_layer.common

import android.media.MediaPlayer
import kotlinx.coroutines.delay

class MediaPlayerManager {
    companion object {
        suspend fun fadeOutSound(sound: MediaPlayer?, delay: Long) {
            sound ?: return

            var volume = 1.0f
            while (volume > 0) {
                volume -= 0.05f
                if (volume < 0) {
                    volume = 0f
                }
                try {
                    sound.setVolume(volume, volume)
                } catch (e: Exception) { }
                delay(delay)
            }
            try {
                sound.pause()
                sound.seekTo(0)
                sound.setVolume(1f, 1f)
            } catch (e: Exception) { }
        }
    }
}