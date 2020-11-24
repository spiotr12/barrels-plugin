package com.starzec.piotr;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.starzec.piotr.dialogs.LanguageComparator;
import org.jetbrains.annotations.NotNull;

public class CreateJavaScriptBarrelAction extends CreateBarrelAction {
    public CreateJavaScriptBarrelAction() {
        super(Language.findLanguageByID("JavaScript"), new LanguageComparator() {
            @Override
            public Boolean compare(Language src, Language dest) {
                return src.is(dest) || src.isKindOf(dest);
            }
        });
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        super.createBarrel(event);
    }

    // TODO: Investigate why no files is found (in filter)
    // TODO: Remove extension

    @Override
    protected String exportLineBuilder(PsiDirectory barrelRootDir, PsiFile file) {
        String fileWithRelativePath = VfsUtilCore.getRelativePath(file.getVirtualFile(), barrelRootDir.getVirtualFile());
        fileWithRelativePath = fileWithRelativePath.replaceAll(".js$|.jsx$", "");

        return String.format("export * from './%s';", fileWithRelativePath);
    }

    @Override
    protected String exportLineBuilder(PsiDirectory barrelRootDir, PsiDirectory directory) {
        return String.format("export * from './%s';", VfsUtilCore.getRelativePath(directory.getVirtualFile(), barrelRootDir.getVirtualFile()));
    }
}
