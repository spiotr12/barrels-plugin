package com.starzec.piotr.dialogs;

import com.intellij.lang.Language;

public abstract class LanguageComparator {
    public abstract Boolean compare(Language src, Language dest);
}
