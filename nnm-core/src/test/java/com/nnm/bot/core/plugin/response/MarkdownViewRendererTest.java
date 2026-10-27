package com.nnm.bot.core.plugin.response;

import com.nnm.bot.core.config.ReplyProperties;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.*;

class MarkdownViewRendererTest {
    private MarkdownViewRenderer renderer(int maximum) {
        MarkdownViewRenderer renderer = new MarkdownViewRenderer();
        ReplyProperties limits = new ReplyProperties();
        limits.setMaxTextCharacters(maximum);
        ReflectionTestUtils.setField(renderer, "limits", limits);
        return renderer;
    }
    @Test
    void interpolatesExplicitDataAndListsWithEscapedMarkdownAndIndependentText() {
        MarkdownViewRenderer renderer = renderer(1000);
        Map<String,Object> data = Map.<String, Object>of("title", "[链接](evil)", "items", List.of(Map.of("name", "*物品*")));
        String markdown = renderer.render("# {{title}}\n{{#items}}- {{name}}{{/items}}", data, true);
        assertThat(markdown).isEqualTo("# \\[链接\\]\\(evil\\)\n- \\*物品\\*");
        assertThat(renderer.render("{{title}}：{{#items}}{{name}}{{/items}}", data, false)).isEqualTo("[链接](evil)：*物品*");
        assertThat(renderer.render("{{title}}", Map.of("title", "{{@notAnExpression}}"), false)).isEqualTo("{{@notAnExpression}}");
    }
    @Test
    void rejectsExpressionsMissingValuesNestedObjectsAndUnboundedExpansion() {
        MarkdownViewRenderer renderer = renderer(20);
        assertThatThrownBy(() -> renderer.render("{{missing}}", Map.of(), true)).hasMessageContaining("Missing");
        assertThatThrownBy(() -> renderer.render("{{@bean.run()}}", Map.of(), true)).hasMessageContaining("Unsupported");
        assertThatThrownBy(() -> renderer.render("{{object}}", Map.of("object", Map.of()), true)).hasMessageContaining("scalar");
        assertThatThrownBy(() -> renderer.render("{{#items}}{{item}}{{/items}}", Map.of("items", List.of("1234567890", "1234567890", "1")), true)).hasMessageContaining("limit");
    }
}
