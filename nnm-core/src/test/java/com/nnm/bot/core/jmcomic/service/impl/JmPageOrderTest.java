package com.nnm.bot.core.jmcomic.service.impl;

import org.junit.jupiter.api.Test;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;

class JmPageOrderTest {
    @Test
    void ordersChaptersAndPagesNumericallyRegardlessOfCompletionOrder() {
        assertThat(Stream.of("10_title/12/1.jpg", "02_title/900/10.jpg",
                        "02_title/900/2.jpg", "01_title/999/1.jpg")
                .sorted(JmPageOrder::compare).toList())
                .containsExactly("01_title/999/1.jpg", "02_title/900/2.jpg",
                        "02_title/900/10.jpg", "10_title/12/1.jpg");
    }
}
