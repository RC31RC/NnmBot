package com.nnm.bot.render;

import com.nnm.bot.render.engine.OpenHtmlToPdfEngine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenHtmlToPdfEngineTest {

    @Test
    void shouldRenderSimpleHtml() {
        OpenHtmlToPdfEngine engine = new OpenHtmlToPdfEngine("file:///");
        String html = """
                <!DOCTYPE html>
                <html>
                <body style="background:#1a1a2e;color:#fff;width:300px;padding:16px;">
                    <h2>Test Card</h2>
                    <p>Hello NnmBot</p>
                </body>
                </html>
                """;

        byte[] result = engine.renderHtmlToImage(html);
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void engineShouldBeAvailable() {
        OpenHtmlToPdfEngine engine = new OpenHtmlToPdfEngine("file:///");
        assertTrue(engine.isAvailable());
        assertEquals("openhtmltopdf", engine.engineName());
    }
}
