package teachingsolutions.domain_layer.exercise

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
                sound.setVolume(volume, volume)
                delay(delay)
            }
            sound.pause()
            sound.seekTo(0)
            sound.setVolume(1f, 1f)
        }
    }
}