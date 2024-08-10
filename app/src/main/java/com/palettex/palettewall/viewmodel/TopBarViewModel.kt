import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class TopBarViewModel : ViewModel() {
    var isTopBarVisible by mutableStateOf(true)
        private set

    fun onScroll(deltaY: Float) {
        if (deltaY > 0) {
            // Scrolling down
            isTopBarVisible = false
        } else if (deltaY < 0) {
            // Scrolling up
            isTopBarVisible = true
        }
    }
}
