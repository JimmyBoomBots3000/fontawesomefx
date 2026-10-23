/**
 * Copyright (c) 2013-2016 Jens Deters http://www.jensd.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *
 */
// Modified in this fork to load Font Awesome Free solid, regular and brands fonts.
package de.jensd.fx.glyphs.fontawesome;

import de.jensd.fx.glyphs.GlyphIcon;
import java.io.IOException;
import javafx.scene.text.Font;

/**
 *
 * @author Jens Deters
 */
public class FontAwesomeIconView extends GlyphIcon<FontAwesomeIcon> {

    public final static String TTF_PATH = "/de/jensd/fx/glyphs/fontawesome/fontawesome-webfont.ttf";

    public static final String SOLID_FONT_PATH = "/de/jensd/fx/glyphs/fontawesome/fa-solid.otf";
    public static final String REGULAR_FONT_PATH = "/de/jensd/fx/glyphs/fontawesome/fa-regular.otf";
    public static final String BRANDS_FONT_PATH = "/de/jensd/fx/glyphs/fontawesome/fa-brands.otf";

    static {
        for (String path : new String[]{SOLID_FONT_PATH, REGULAR_FONT_PATH, BRANDS_FONT_PATH, TTF_PATH}) {
            try (java.io.InputStream stream = FontAwesomeIconView.class.getResourceAsStream(path)) {
                if (stream == null || Font.loadFont(stream, 10.0d) == null) {
                    throw new IllegalStateException("Cannot load icon font: " + path);
                }
            } catch (IOException ex) {
                throw new ExceptionInInitializerError(ex);
            }
        }
    }

    public FontAwesomeIconView(FontAwesomeIcon icon, String iconSize) {
        super(FontAwesomeIcon.class);
        setIcon(icon);
        setStyle(String.format("-fx-font-family: '%s'; -fx-font-size: %s;", icon.fontFamily(), iconSize));
    }

    public FontAwesomeIconView(FontAwesomeIcon icon) {
        this(icon, "1em");
    }

    public FontAwesomeIconView() {
        this(FontAwesomeIcon.ANCHOR);
    }

    @Override
    public FontAwesomeIcon getDefaultGlyph() {
        return FontAwesomeIcon.ANCHOR;
    }

}
