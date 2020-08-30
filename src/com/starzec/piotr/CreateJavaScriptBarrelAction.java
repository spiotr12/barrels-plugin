package com.starzec.piotr;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class CreateJavaScriptBarrelAction extends CreateBarrelAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Language language = Language.findLanguageByID("JavaScript");
        assert language != null;
        super.createBarrel(event, language);
    }
}
