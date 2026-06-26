package com.nnm.bot.warframe.domain;

public interface MessageBuilder<T> {

    byte[] buildImage(T data);

    String buildText(T data);

    String type();
}
