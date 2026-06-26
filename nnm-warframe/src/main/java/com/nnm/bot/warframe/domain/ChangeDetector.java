package com.nnm.bot.warframe.domain;

public interface ChangeDetector<T> {

    boolean hasChanged(T oldState, T newState);

    String type();
}
