package com.starzec.piotr;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class CreateJavaScriptBarrelAction extends CreateBarrelAction {
    public CreateJavaScriptBarrelAction() {
        super(Language.findLanguageByID("JavaScript"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        super.createBarrel(event);
    }
}
