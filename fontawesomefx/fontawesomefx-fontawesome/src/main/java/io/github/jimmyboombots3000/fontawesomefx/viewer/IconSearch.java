/* Licensed under the Apache License, Version 2.0. See LICENSE. */
package io.github.jimmyboombots3000.fontawesomefx.viewer;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.util.Arrays;
import java.util.Locale;

/** Search metadata for one icon; no JavaFX nodes are allocated while filtering. */
final class IconSearch {
    final FontAwesomeIcon icon;
    private final String name;
    private final String[] tokens;
    private final String acronym;

    IconSearch(FontAwesomeIcon icon) {
        this.icon = icon;
        name = icon.name().toLowerCase(Locale.ROOT);
        tokens = name.split("_");
        StringBuilder initials = new StringBuilder();
        for (String token : tokens) {
            initials.append(token.charAt(0));
        }
        acronym = initials.toString();
    }

    boolean matches(String query) {
        return Arrays.stream(query.trim().toLowerCase(Locale.ROOT).split("\\s+"))
                .allMatch(this::matchesWord);
    }

    private boolean matchesWord(String word) {
        if (name.contains(word) || acronym.startsWith(word)) {
            return true;
        }
        for (String token : tokens) {
            if (token.startsWith(word) || (word.length() >= 4 && token.length() >= 4
                    && withinTwoEdits(word, token))) {
                return true;
            }
        }
        return false;
    }

    private static boolean withinTwoEdits(String left, String right) {
        if (Math.abs(left.length() - right.length()) > 2) {
            return false;
        }
        int[] previous = new int[right.length() + 1];
        for (int j = 0; j < previous.length; j++) {
            previous[j] = j;
        }
        for (int i = 1; i <= left.length(); i++) {
            int[] current = new int[right.length() + 1];
            current[0] = i;
            int minimum = i;
            for (int j = 1; j <= right.length(); j++) {
                int substitution = previous[j - 1] + (left.charAt(i - 1) == right.charAt(j - 1) ? 0 : 1);
                current[j] = Math.min(substitution, Math.min(previous[j] + 1, current[j - 1] + 1));
                minimum = Math.min(minimum, current[j]);
            }
            if (minimum > 2) {
                return false;
            }
            previous = current;
        }
        return previous[right.length()] <= 2;
    }
}
