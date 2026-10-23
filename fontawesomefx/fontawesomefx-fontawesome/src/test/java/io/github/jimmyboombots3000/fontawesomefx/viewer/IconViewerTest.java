/* Licensed under the Apache License, Version 2.0. See LICENSE. */
package io.github.jimmyboombots3000.fontawesomefx.viewer;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.scene.text.Text;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import static org.junit.jupiter.api.Assertions.*;

/** Opt-in smoke test: -Dfontawesomefx.uiTests=true; requires a desktop display. */
@EnabledIfSystemProperty(named = "fontawesomefx.uiTests", matches = "true")
class IconViewerTest {
    private static Stage stage;
    private static IconViewer viewer;

    @BeforeAll
    static void openViewer() throws Exception {
        Platform.startup(() -> Platform.setImplicitExit(false));
        onFx(() -> {
            viewer = new IconViewer();
            stage = new Stage();
            viewer.start(stage);
            stage.getScene().getRoot().applyCss();
            stage.getScene().getRoot().layout();
        });
    }

    @AfterAll
    static void closeViewer() throws Exception {
        onFx(() -> {
            if (viewer != null) viewer.stop();
            if (stage != null) stage.close();
        });
        Platform.exit();
    }

    @Test
    void filtersResizesAndProducesLoadableFxml() throws Exception {
        onFx(() -> {
            Parent root = stage.getScene().getRoot();
            TableView<?> table = (TableView<?>) root.lookup(".table-view");
            assertTrue(table.getItems().size() > 2000);
            assertTrue(root.lookupAll(".glyph-icon").size() < 100, "Previews should be virtualized");
            ((TextField) root.lookup("#icon-search")).setText("github");
        });
        // Let the debounce animation complete while the test thread waits.
        Thread.sleep(350);
        onFx(() -> {
            Parent root = stage.getScene().getRoot();
            TableView<?> table = (TableView<?>) root.lookup(".table-view");
            assertFalse(table.getItems().isEmpty());
            assertTrue(table.getItems().size() < 20);
            for (Object item : table.getItems()) {
                assertTrue(((IconSearch) item).matches("github"));
            }
            ((TextField) root.lookup("#icon-search")).clear();
            @SuppressWarnings("unchecked") ComboBox<String> styles = (ComboBox<String>) root.lookup(".combo-box");
            styles.setValue("Regular");
            for (Object item : table.getItems()) {
                assertEquals("Font Awesome 7 Free Regular", ((IconSearch) item).icon.fontFamily());
            }
            @SuppressWarnings("unchecked") Spinner<Integer> size = (Spinner<Integer>) root.lookup(".spinner");
            size.getValueFactory().setValue(40);
            root.applyCss();
            root.layout();
            root.lookupAll(".glyph-icon").stream().map(node -> (FontAwesomeIconView) node)
                    .filter(glyph -> glyph.isVisible() && glyph.getParent() != null)
                    .forEach(glyph -> assertEquals(FontAwesomeIcon.valueOf(glyph.getGlyphName()).fontFamily(),
                            glyph.getFont().getName()));
            TextArea fxml = root.lookupAll(".text-area").stream().map(node -> (TextArea) node)
                    .filter(area -> area.getText().startsWith("<?import")).findFirst().orElseThrow();
            try {
                FontAwesomeIconView icon = new FXMLLoader().load(new ByteArrayInputStream(
                        fxml.getText().getBytes(StandardCharsets.UTF_8)));
                assertEquals(40, icon.getGlyphSize().intValue());
                assertEquals("Font Awesome 7 Free Regular", icon.getGlyphFontFamily());
            } catch (Exception ex) {
                throw new AssertionError("Generated FXML must load", ex);
            }
        });
    }

    @Test
    void resizingKeepsTheIconFontAfterCssIsApplied() throws Exception {
        onFx(() -> {
            FontAwesomeIconView view = new FontAwesomeIconView();
            Text factoryIcon = FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.GITHUB, "28px");
            VBox root = new VBox(view, factoryIcon);
            new Scene(root);
            view.setStyle("-fx-fill: red;");
            for (FontAwesomeIcon icon : new FontAwesomeIcon[]{FontAwesomeIcon.ADDRESS_BOOK,
                    FontAwesomeIcon.REGULAR_HEART, FontAwesomeIcon.GITHUB}) {
                view.setIcon(icon);
                for (int size : new int[]{24, 28, 48, 12, 32}) {
                    view.setGlyphSize(size);
                    root.applyCss();
                    root.layout();
                    assertTrue(view.getStyle().contains("-fx-fill: red;"));
                    assertEquals(icon.fontFamily(), view.getFont().getName(), icon.name() + " at " + size);
                    assertEquals(size, view.getFont().getSize());
                    assertEquals(FontAwesomeIcon.GITHUB.fontFamily(), factoryIcon.getFont().getName());
                    assertEquals(1, view.getStyle().split("-fx-font-family:", -1).length - 1);
                    assertEquals(1, view.getStyle().split("-fx-font-size:", -1).length - 1);
                }
            }
        });
    }

    private static void onFx(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }
}
