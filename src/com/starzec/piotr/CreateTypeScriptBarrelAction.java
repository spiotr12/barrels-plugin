package com.starzec.piotr;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class CreateTypeScriptBarrelAction extends CreateBarrelAction {
    public CreateTypeScriptBarrelAction() {
        super(Language.findLanguageByID("TypeScript"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        super.createBarrel(event);
    }
}
