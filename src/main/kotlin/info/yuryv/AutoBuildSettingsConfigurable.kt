package info.yuryv

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

/**
 * Settings UI shown under Tools > Auto Build on Save.
 */
class AutoBuildSettingsConfigurable : Configurable {

    private var panel: JPanel? = null
    private val enabledCheckBox = JBCheckBox("Enable Auto Build on Save")
    private val requireZeroErrorsCheckBox = JBCheckBox("Only build when there are zero linter errors")
    private val debounceSpinner = JSpinner(SpinnerNumberModel(1500, 0, 10_000, 100))

    override fun getDisplayName() = "Auto Build on Save"

    override fun createComponent(): JComponent {
        panel = FormBuilder.createFormBuilder()
            .addComponent(enabledCheckBox)
            .addComponent(requireZeroErrorsCheckBox)
            .addLabeledComponent(
                JBLabel("Debounce delay (ms):"),
                debounceSpinner
            )
            .addComponentFillVertically(JPanel(), 0)
            .panel
        return panel!!
    }

    override fun isModified(): Boolean {
        val s = AutoBuildSettings.getInstance().state
        return enabledCheckBox.isSelected != s.enabled ||
                requireZeroErrorsCheckBox.isSelected != s.requireZeroErrors ||
                debounceSpinner.value as Int != s.debounceMs
    }

    override fun apply() {
        val s = AutoBuildSettings.getInstance().state
        s.enabled = enabledCheckBox.isSelected
        s.requireZeroErrors = requireZeroErrorsCheckBox.isSelected
        s.debounceMs = debounceSpinner.value as Int
    }

    override fun reset() {
        val s = AutoBuildSettings.getInstance().state
        enabledCheckBox.isSelected = s.enabled
        requireZeroErrorsCheckBox.isSelected = s.requireZeroErrors
        debounceSpinner.value = s.debounceMs
    }
}
