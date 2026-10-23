/* Licensed under the Apache License, Version 2.0. See LICENSE. */
package io.github.jimmyboombots3000.fontawesomefx.viewer;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IconSearchTest {
    private final IconSearch arrow = new IconSearch(FontAwesomeIcon.ARROW_CIRCLE_LEFT);

    @Test
    void supportsNamesInitialsAndAllSearchWords() {
        assertTrue(arrow.matches(""));
        assertTrue(arrow.matches("  ARROW  left "));
        assertTrue(arrow.matches("acl"));
        assertTrue(arrow.matches("arr cir"));
        assertFalse(arrow.matches("arrow github"));
    }

    @Test
    void toleratesTwoEditsButDoesNotFuzzShortWords() {
        assertTrue(arrow.matches("arow"));
        assertTrue(arrow.matches("circel"));
        assertFalse(arrow.matches("xyzxyz"));
        assertFalse(arrow.matches("lft"));
    }

    @Test
    void searchDoesNotDependOnSystemLocale() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertTrue(new IconSearch(FontAwesomeIcon.CIRCLE).matches("CIRCLE"));
        } finally {
            Locale.setDefault(previous);
        }
    }
}
