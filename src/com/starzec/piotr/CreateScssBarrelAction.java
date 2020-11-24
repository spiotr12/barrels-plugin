package com.starzec.piotr;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.starzec.piotr.dialogs.LanguageComparator;
import org.jetbrains.annotations.NotNull;

public class CreateScssBarrelAction extends CreateBarrelAction {
    public CreateScssBarrelAction() {
        super(Language.findLanguageByID("SCSS"), new LanguageComparator() {
            @Override
            public Boolean compare(Language src, Language dest) {
                return src.is(dest);
            }
        });
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        super.createBarrel(event, "_index");
    }

    @Override
    protected String exportLineBuilder(PsiDirectory barrelRootDir, PsiFile file) {
        String fileWithRelativePath = VfsUtilCore.getRelativePath(file.getVirtualFile(), barrelRootDir.getVirtualFile());
        String fixedFileName = file.getName();

        if (fixedFileName.startsWith("_")) {
            fixedFileName = fixedFileName.substring(1);
        }

        fileWithRelativePath = fileWithRelativePath.replace(file.getName(), fixedFileName);

        fileWithRelativePath = fileWithRelativePath.replaceAll(".scss$", "");

        return String.format("@forward './%s';", fileWithRelativePath);
    }

    @Override
    protected String exportLineBuilder(PsiDirectory barrelRootDir, PsiDirectory directory) {
        return String.format("@forward './%s';", VfsUtilCore.getRelativePath(directory.getVirtualFile(), barrelRootDir.getVirtualFile()));
    }
}
