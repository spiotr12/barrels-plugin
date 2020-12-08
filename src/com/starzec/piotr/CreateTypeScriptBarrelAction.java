package com.starzec.piotr;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.starzec.piotr.dialogs.LanguageComparator;
import org.jetbrains.annotations.NotNull;

public class CreateTypeScriptBarrelAction extends CreateBarrelAction {
    public CreateTypeScriptBarrelAction() {
        super(Language.findLanguageByID("TypeScript"), new LanguageComparator() {
            @Override
            public Boolean compare(Language src, Language dest) {
                return src.isKindOf(dest);
            }
        });
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        super.createBarrel(event);
    }

    // TODO: Remove extension

    @Override
    protected String exportLineBuilder(PsiDirectory barrelRootDir, PsiFile file) {
        String fileWithRelativePath = VfsUtilCore.getRelativePath(file.getVirtualFile(), barrelRootDir.getVirtualFile());
        fileWithRelativePath = fileWithRelativePath.replaceAll(".ts$|.tsx$", "");

        return String.format("export * from './%s';", fileWithRelativePath);
    }

    @Override
    protected String exportLineBuilder(PsiDirectory barrelRootDir, PsiDirectory directory) {
        return String.format("export * from './%s';", VfsUtilCore.getRelativePath(directory.getVirtualFile(), barrelRootDir.getVirtualFile()));
    }
}
