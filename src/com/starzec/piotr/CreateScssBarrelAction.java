package com.starzec.piotr;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class CreateScssBarrelAction extends CreateBarrelAction {
    public CreateScssBarrelAction() {
        super(Language.findLanguageByID("SCSS"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        super.createBarrel(event, "_index");
    }
}
