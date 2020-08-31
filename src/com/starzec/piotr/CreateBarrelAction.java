package com.starzec.piotr;

import com.intellij.lang.Language;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

/**
 * Base action for creating barrels
 */
public abstract class CreateBarrelAction extends AnAction {
    private final NotificationGroup NOTIFICATION_GROUP =
            new NotificationGroup("Barrels plugin", NotificationDisplayType.BALLOON, true);

    /**
     * Create barrel with provided language and default "index" file name
     */
    public void createBarrel(@NotNull AnActionEvent event, Language language) {
        this.createBarrel(event, language, "index");
    }

    /**
     * Create barrel with provided language file name
     */
    public void createBarrel(@NotNull AnActionEvent event, Language language, String fileName) {
        final Project project = event.getProject();
        assert project != null;

        VirtualFile virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        assert virtualFile != null;

        if (!virtualFile.isDirectory()) {
            virtualFile = virtualFile.getParent();
        }

        final PsiManager manager = PsiManager.getInstance(project);
        PsiFileFactory factory = PsiFileFactory.getInstance(project);

        final PsiDirectory psiDirectory = manager.findDirectory(virtualFile);
        assert psiDirectory != null;

        final LanguageFileType fileType = language.getAssociatedFileType();
        assert fileType != null;

        final String extension = fileType.getDefaultExtension();
        final String fullFileName = String.format("%s.%s", fileName, extension);
        final PsiFile file = factory.createFileFromText(fullFileName, language, "");

        if (psiDirectory.findFile(fullFileName) != null) {
            final String msg = String.format("Barrel \"%s\" already exists at: \n\"%s\"", fullFileName, virtualFile.getPath());
            final Notification notification = NOTIFICATION_GROUP.createNotification(msg, NotificationType.ERROR);
            notification.notify(project);
            return;
        }

        final Application app = ApplicationManager.getApplication();

        app.runWriteAction(() -> {
            psiDirectory.add(file);

            final String msg = String.format("Barrel \"%s\" successfully created", fullFileName);
            final Notification notification = NOTIFICATION_GROUP.createNotification(msg, NotificationType.INFORMATION);
            notification.notify(project);
        });
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        event.getPresentation().setEnabledAndVisible(project != null);
    }
}
