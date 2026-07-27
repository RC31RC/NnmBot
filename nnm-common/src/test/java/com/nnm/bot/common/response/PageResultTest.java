package com.nnm.bot.common.response;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageResultTest {

    @Test
    void keepsAnImmutableSnapshotOfRecords() {
        List<String> source = new ArrayList<>(List.of("one"));

        PageResult<String> result = PageResult.of(source, 1, 20, 1);
        source.add("two");

        assertThat(result.records()).containsExactly("one");
        assertThatThrownBy(() -> result.records().add("three"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void rejectsInvalidPageMetadata() {
        assertThatThrownBy(() -> PageResult.of(List.of(), 0, 20, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("current");
        assertThatThrownBy(() -> PageResult.of(List.of(), 1, -1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("size");
    }
}
