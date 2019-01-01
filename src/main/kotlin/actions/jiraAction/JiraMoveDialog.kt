package actions.jiraAction

import actions.jiraAction.di.JiraModule
import actions.jiraAction.di.DaggerJiraDIComponent
import actions.jiraAction.network.Transition
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.inject.Inject
import javax.swing.JComponent

class JiraMoveDialog constructor(private val project: Project):
        DialogWrapper(true) {

    @Inject
    lateinit var presenter: JiraMoveDialogPresenter

    private val panel : JiraMovePanel = JiraMovePanel()

    init {
        DaggerJiraDIComponent.builder()
                .jiraModule(JiraModule(this, project))
                .build().inject(this)
        isModal = true
        presenter.load()
        init()
    }

    override fun createCenterPanel(): JComponent? = panel

    override fun doOKAction() = presenter.doTransition(panel.getTransition(), panel.txtIssue.text)

    fun setIssue(issue: String) = panel.setIssue(issue)

    fun setTransitions(transitionList: List<Transition>) {
        for(transition in transitionList) {
            panel.addTransition(transition)
        }
    }

    fun success() {
        close(DialogWrapper.OK_EXIT_CODE)

        val noti = NotificationGroup("myplugin", NotificationDisplayType.BALLOON, true)
        noti.createNotification("Success", "Issue moved", NotificationType.INFORMATION, null).notify(project)
    }

    fun error(throwable: Throwable) {
        val noti = NotificationGroup("myplugin", NotificationDisplayType.BALLOON, true)
        noti.createNotification("Error", throwable.localizedMessage, NotificationType.ERROR, null).notify(project)
    }
}