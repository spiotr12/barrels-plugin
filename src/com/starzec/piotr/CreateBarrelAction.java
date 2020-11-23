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
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * Base action for creating barrels
 */
public abstract class CreateBarrelAction extends AnAction {
    public Language language;

    public CreateBarrelAction(Language language) {
        assert language != null;
        this.language = language;
    }

    private final NotificationGroup NOTIFICATION_GROUP =
            new NotificationGroup("Barrels plugin", NotificationDisplayType.BALLOON, true);

    /**
     * Create barrel with provided language and default "index" file name
     */
    public void createBarrel(@NotNull AnActionEvent event) {
        createBarrel(event, "index");
    }

    /**
     * Create barrel with provided language file name
     */
    public void createBarrel(@NotNull AnActionEvent event, String fileName) {
        final Project project = event.getProject();
        assert project != null;

        VirtualFile virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        assert virtualFile != null;

        if (!virtualFile.isDirectory()) {
            virtualFile = virtualFile.getParent();
        }

        final PsiManager psiManager = PsiManager.getInstance(project);
        PsiFileFactory factory = PsiFileFactory.getInstance(project);

        final PsiDirectory psiDirectory = psiManager.findDirectory(virtualFile);
        assert psiDirectory != null;

        final LanguageFileType fileType = language.getAssociatedFileType();
        assert fileType != null;

        final String extension = fileType.getDefaultExtension();
        final String fullFileName = String.format("%s.%s", fileName, extension);
        final PsiFile file = factory.createFileFromText(fullFileName, language, "");

        FileEditorManager editorManager = FileEditorManager.getInstance(project);
        assert editorManager != null;

        PsiFile existingFile = psiDirectory.findFile(fullFileName);

        if (existingFile != null) {
            final String msg = String.format("Barrel \"%s\" already exists at: \n\"%s\"", fullFileName, virtualFile.getPath());
            final Notification notification = NOTIFICATION_GROUP.createNotification(msg, NotificationType.ERROR);
            notification.notify(project);
            editorManager.openFile(existingFile.getVirtualFile(), true);
            return;
        }

        final Application app = ApplicationManager.getApplication();

        app.runWriteAction(() -> {
            FilesToExportDialog dialog = new FilesToExportDialog(project, psiDirectory, language);
            dialog.show();

            if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
                // TODO: Use selected items to generate code
                // dialog.getSelectedItems();


                PsiElement newElement = psiDirectory.add(file);

                final String msg = String.format("Barrel \"%s\" successfully created", fullFileName);
                final Notification notification = NOTIFICATION_GROUP.createNotification(msg, NotificationType.INFORMATION);
                notification.notify(project);

                editorManager.openFile(newElement.getContainingFile().getVirtualFile(), true);
            }

            if (dialog.getExitCode() == DialogWrapper.CANCEL_EXIT_CODE) {
                System.out.println("CANCELED");
            }
        });
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        event.getPresentation().setEnabledAndVisible(project != null);
    }
}
