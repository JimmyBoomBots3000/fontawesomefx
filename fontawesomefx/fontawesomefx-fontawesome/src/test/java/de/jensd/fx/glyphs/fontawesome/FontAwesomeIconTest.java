package de.jensd.fx.glyphs.fontawesome;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FontAwesomeIconTest {
    @Test
    void everyIconHasAGlyphInItsBundledFont() throws Exception {
        Map<String, java.awt.Font> fonts = new HashMap<>();
        for (String path : new String[]{FontAwesomeIconView.SOLID_FONT_PATH,
                FontAwesomeIconView.REGULAR_FONT_PATH, FontAwesomeIconView.BRANDS_FONT_PATH,
                FontAwesomeIconView.TTF_PATH}) {
            try (InputStream stream = getClass().getResourceAsStream(path)) {
                assertNotNull(stream, path);
                java.awt.Font font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, stream);
                fonts.put(font.getFontName(java.util.Locale.ROOT), font);
            }
        }
        for (FontAwesomeIcon icon : FontAwesomeIcon.values()) {
            java.awt.Font font = fonts.get(icon.fontFamily());
            assertNotNull(font, icon.name() + ": " + icon.fontFamily());
            assertEquals(-1, font.canDisplayUpTo(icon.unicode()), icon.name());
        }
    }

    @Test
    void viewSwitchesFontsAndPreservesSize() {
        FontAwesomeIconView view = new FontAwesomeIconView(FontAwesomeIcon.HOUSE);
        view.setGlyphSize(24);
        view.setStyle(view.getStyle() + "-fx-fill: red;");
        for (FontAwesomeIcon icon : new FontAwesomeIcon[]{FontAwesomeIcon.GITHUB,
                FontAwesomeIcon.REGULAR_HEART, FontAwesomeIcon.HOUSE}) {
            view.setIcon(icon);
            assertEquals(icon.fontFamily(), view.getFont().getName());
            assertEquals(icon.fontFamily(), view.getGlyphFontFamily());
            assertEquals(icon.unicode(), view.getText());
            assertEquals(icon.unicode(), view.unicode());
            assertEquals(24, view.getFont().getSize());
            assertTrue(view.getStyle().contains("-fx-fill: red;"));
            assertTrue(view.getStyle().contains("-fx-font-size: 24.0;"));
        }
        // FXML and CSS update the glyphName property rather than calling setIcon.
        view.setGlyphName("REGULAR_HEART");
        assertEquals(FontAwesomeIcon.REGULAR_HEART.fontFamily(), view.getFont().getName());
        assertEquals(FontAwesomeIcon.REGULAR_HEART.unicode(), view.unicode());
        view.setGlyphSize(32);
        assertEquals(FontAwesomeIcon.REGULAR_HEART.fontFamily(), view.getFont().getName());
        assertEquals(32, view.getFont().getSize());
    }

    @Test
    void historicalNamesUseCurrentMappings() {
        assertEquals(FontAwesomeIcon.HOUSE.unicode(), FontAwesomeIcon.HOME.unicode());
        assertEquals(FontAwesomeIcon.REGULAR_ADDRESS_BOOK.unicode(), FontAwesomeIcon.ADDRESS_BOOK_ALT.unicode());
        assertEquals(FontAwesomeIcon.REGULAR_ADDRESS_BOOK.fontFamily(), FontAwesomeIcon.ADDRESS_BOOK_ALT.fontFamily());
    }
}
