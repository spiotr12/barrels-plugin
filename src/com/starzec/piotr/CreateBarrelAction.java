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
import com.starzec.piotr.dialogs.FilesToExportDialog;
import com.starzec.piotr.dialogs.LanguageComparator;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Base action for creating barrels
 */
public abstract class CreateBarrelAction extends AnAction {

    public Language language;
    public LanguageComparator languageComparator;

    public CreateBarrelAction(Language language, LanguageComparator languageComparator) {
        assert language != null;
        this.language = language;
        this.languageComparator = languageComparator;
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

        FilesToExportDialog dialog = new FilesToExportDialog(psiDirectory, language, languageComparator);
        dialog.show();

        if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            app.runWriteAction(() -> {
                String content = buildBarrelFileContent(psiDirectory, dialog.getSelectedItems());

                final PsiFile file = factory.createFileFromText(fullFileName, language, content);
                final PsiElement newElement = psiDirectory.add(file);

                final String msg = String.format("Barrel \"%s\" successfully created", fullFileName);
                final Notification notification = NOTIFICATION_GROUP.createNotification(msg, NotificationType.INFORMATION);
                notification.notify(project);

                editorManager.openFile(newElement.getContainingFile().getVirtualFile(), true);
            });
        }

        if (dialog.getExitCode() == DialogWrapper.CANCEL_EXIT_CODE) {
            final String msg = "Barrel creation canceled";
            final Notification notification = NOTIFICATION_GROUP.createNotification(msg, NotificationType.INFORMATION);
            notification.notify(project);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        event.getPresentation().setEnabledAndVisible(project != null);
    }

    protected abstract String exportLineBuilder(PsiDirectory barrelRootDir, PsiFile file);

    protected abstract String exportLineBuilder(PsiDirectory barrelRootDir, PsiDirectory directory);

    private String buildBarrelFileContent(PsiDirectory barrelRootDir, PsiFileSystemItem[] items) {
        return Arrays.stream(items).map(item -> {
            if (item instanceof PsiFile) {
                return exportLineBuilder(barrelRootDir, (PsiFile) item);
            }
            if (item instanceof PsiDirectory) {
                return exportLineBuilder(barrelRootDir, (PsiDirectory) item);
            }
            return MessageFormat.format("Item is not a file nor a directory \"%s\"", item.getName());
        }).collect(Collectors.joining("\n"));
    }
}
