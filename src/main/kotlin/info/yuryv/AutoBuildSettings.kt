package info.yuryv

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "AutoBuildSettings",
    storages = [Storage("auto-build-on-save.xml")]
)
class AutoBuildSettings : PersistentStateComponent<AutoBuildSettings.State> {

    data class State(
        var enabled: Boolean = true,
        /** Milliseconds to wait after save before triggering build (debounce). */
        var debounceMs: Int = 1500,
        /** Only build if there are zero errors (warnings are OK). */
        var requireZeroErrors: Boolean = true
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        fun getInstance(): AutoBuildSettings =
            ApplicationManager.getApplication()
                .getService(AutoBuildSettings::class.java)
    }
}
