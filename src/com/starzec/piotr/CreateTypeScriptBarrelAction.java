package com.starzec.piotr;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class CreateTypeScriptBarrelAction extends CreateBarrelAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Language language = Language.findLanguageByID("TypeScript");
        assert language != null;
        super.createBarrel(event, language);
    }
}
