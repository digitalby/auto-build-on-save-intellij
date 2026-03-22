package info.yuryv

import com.intellij.compiler.ProblemsView
import com.intellij.openapi.compiler.CompilerManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.problems.WolfTheProblemSolver

private val LOG = logger<AutoBuildOnSaveListener>()

class AutoBuildOnSaveListener : FileDocumentManagerListener {

    // Debounce: track the last scheduled build time per project
    private val pendingBuildTimestamps = mutableMapOf<String, Long>()

    override fun beforeDocumentSaving(document: Document) {
        val settings = AutoBuildSettings.getInstance().state
        if (!settings.enabled) return

        // Find the project(s) this document belongs to
        val openProjects = ProjectManager.getInstance().openProjects
        for (project in openProjects) {
            if (project.isDisposed) continue

            val file: VirtualFile = com.intellij.openapi.fileEditor.FileDocumentManager
                .getInstance()
                .getFile(document) ?: continue

            // Only react to files that are part of this project's source
            val fileIndex = ProjectFileIndex.getInstance(project)
            if (!fileIndex.isInContent(file)) continue

            val debounceMs = settings.debounceMs.toLong()
            val now = System.currentTimeMillis()
            val last = pendingBuildTimestamps[project.locationHash] ?: 0L

            // Debounce: if a build was already scheduled recently, skip
            if (now - last < debounceMs) {
                LOG.debug("Auto Build on Save: skipping, debounce in effect for ${project.name}")
                continue
            }
            pendingBuildTimestamps[project.locationHash] = now

            triggerBuildIfClean(project, settings)
        }
    }

    private fun triggerBuildIfClean(
        project: com.intellij.openapi.project.Project,
        settings: AutoBuildSettings.State
    ) {
        if (settings.requireZeroErrors) {
            val wolf = WolfTheProblemSolver.getInstance(project)
            if (wolf.hasProblemFilesBeneath { true }) {
                LOG.info("Auto Build on Save: skipping build — linter errors detected in ${project.name}")
                notifySkipped(project)
                return
            }
        }

        LOG.info("Auto Build on Save: triggering make for ${project.name}")
        val compilerManager = CompilerManager.getInstance(project)

        // make() compiles only changed files (incremental), which is fast
        compilerManager.make { aborted, errors, warnings, context ->
            if (errors > 0) {
                LOG.info("Auto Build on Save: build finished with $errors error(s)")
            } else {
                LOG.info("Auto Build on Save: build successful (warnings=$warnings)")
            }
        }
    }

    private fun notifySkipped(project: com.intellij.openapi.project.Project) {
        com.intellij.notification.NotificationGroupManager.getInstance()
            .getNotificationGroup("Auto Build on Save")
            ?.createNotification(
                "Auto Build on Save",
                "Build skipped — fix linter errors first.",
                com.intellij.notification.NotificationType.WARNING
            )
            ?.notify(project)
    }
}
