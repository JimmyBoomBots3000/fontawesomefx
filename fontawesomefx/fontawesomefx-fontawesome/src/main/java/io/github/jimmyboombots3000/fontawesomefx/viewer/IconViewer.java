/* Licensed under the Apache License, Version 2.0. See LICENSE. */
package io.github.jimmyboombots3000.fontawesomefx.viewer;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.util.Arrays;
import java.util.stream.Collectors;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Standalone searchable Font Awesome catalog with Java and FXML examples. */
public class IconViewer extends Application {
    private final PauseTransition debounce = new PauseTransition(Duration.millis(100));
    private final Label status = new Label();

    @Override
    public void start(Stage stage) {
        FilteredList<IconSearch> filtered = new FilteredList<>(FXCollections.observableArrayList(
                Arrays.stream(FontAwesomeIcon.values()).map(IconSearch::new).collect(Collectors.toList())));
        SortedList<IconSearch> sorted = new SortedList<>(filtered);
        TableView<IconSearch> table = new TableView<>(sorted);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setPlaceholder(new Label("No matching icons. Try a shorter search or another style."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Spinner<Integer> size = new Spinner<>(8, 96, 24);
        size.setPrefWidth(90);
        size.setAccessibleText("Icon size in pixels");
        size.setOnScroll(event -> {
            if (event.getDeltaY() > 0) size.increment();
            else if (event.getDeltaY() < 0) size.decrement();
            event.consume();
        });
        TextField search = new TextField();
        search.setId("icon-search");
        search.setPromptText("Search names, initials, or approximate spelling…");
        search.setAccessibleText("Search icons");
        search.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                search.clear();
                event.consume();
            }
        });
        ComboBox<String> style = new ComboBox<>(FXCollections.observableArrayList(
                "All styles", "Solid", "Regular", "Brands", "Legacy"));
        style.getSelectionModel().selectFirst();
        style.setAccessibleText("Filter icon style");
        Label count = new Label();
        count.textProperty().bind(Bindings.size(filtered).asString("%d icons"));
        Runnable filter = () -> {
            IconSearch selected = table.getSelectionModel().getSelectedItem();
            String query = search.getText();
            filtered.setPredicate(entry -> entry.matches(query)
                    && (style.getValue().equals("All styles") || styleOf(entry.icon).equals(style.getValue())));
            if (selected != null && filtered.contains(selected)) table.getSelectionModel().select(selected);
            else table.getSelectionModel().selectFirst();
        };
        debounce.setOnFinished(event -> filter.run());
        search.textProperty().addListener((observable, before, after) -> debounce.playFromStart());
        style.valueProperty().addListener((observable, before, after) -> filter.run());

        TableColumn<IconSearch, FontAwesomeIcon> preview = new TableColumn<>("Icon");
        preview.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().icon));
        preview.setSortable(false);
        preview.setMaxWidth(130);
        preview.setCellFactory(column -> new TableCell<IconSearch, FontAwesomeIcon>() {
            private final FontAwesomeIconView glyph = new FontAwesomeIconView();
            {
                glyph.glyphSizeProperty().bind(size.valueProperty());
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(FontAwesomeIcon icon, boolean empty) {
                super.updateItem(icon, empty);
                setText(null);
                if (empty || icon == null) {
                    setGraphic(null);
                } else {
                    glyph.setIcon(icon);
                    setGraphic(glyph);
                }
            }
        });
        TableColumn<IconSearch, String> name = new TableColumn<>("Enum name");
        name.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().icon.name()));
        name.setPrefWidth(490);
        TableColumn<IconSearch, String> fontStyle = new TableColumn<>("Style");
        fontStyle.setCellValueFactory(cell -> new ReadOnlyStringWrapper(styleOf(cell.getValue().icon)));
        fontStyle.setMaxWidth(110);
        table.getColumns().add(preview);
        table.getColumns().add(name);
        table.getColumns().add(fontStyle);
        table.getSelectionModel().selectFirst();

        TextArea javaCode = codeArea();
        TextArea fxmlCode = codeArea();
        javaCode.textProperty().bind(Bindings.createStringBinding(() -> {
            IconSearch selected = table.getSelectionModel().getSelectedItem();
            return selected == null ? "" : String.format(
                    "FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.%s);%n"
                    + "iconView.setGlyphSize(%d);", selected.icon.name(), size.getValue());
        }, table.getSelectionModel().selectedItemProperty(), size.valueProperty()));
        fxmlCode.textProperty().bind(Bindings.createStringBinding(() -> {
            IconSearch selected = table.getSelectionModel().getSelectedItem();
            return selected == null ? "" : String.format(
                    "<?import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView?>%n"
                    + "<FontAwesomeIconView glyphName=\"%s\" glyphSize=\"%d\" />",
                    selected.icon.name(), size.getValue());
        }, table.getSelectionModel().selectedItemProperty(), size.valueProperty()));
        javaCode.setOnMouseClicked(event -> copy(javaCode, "Java"));
        fxmlCode.setOnMouseClicked(event -> copy(fxmlCode, "FXML"));
        table.getSelectionModel().selectedItemProperty().addListener((observable, before, after) -> status.setText(""));
        HBox toolbar = new HBox(10, new Label("Size:"), size, style, count);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        VBox root = new VBox(10, toolbar, search, table,
                codeHeader("Java", javaCode), javaCode, codeHeader("FXML", fxmlCode), fxmlCode, status);
        root.setPadding(new Insets(16));
        VBox.setVgrow(table, Priority.ALWAYS);
        stage.setTitle("FontAwesomeFX — Icon Viewer");
        stage.setScene(new Scene(root, 900, 800));
        stage.setMinWidth(650);
        stage.setMinHeight(550);
        stage.show();
        search.requestFocus();
    }

    private static String styleOf(FontAwesomeIcon icon) {
        String font = icon.fontFamily();
        if (font.contains("Brands")) return "Brands";
        if (font.endsWith("Solid")) return "Solid";
        if (font.endsWith("Regular")) return "Regular";
        return "Legacy";
    }

    private static TextArea codeArea() {
        TextArea area = new TextArea();
        area.setEditable(false);
        area.setPrefRowCount(2);
        area.setStyle("-fx-font-family: monospace;");
        return area;
    }

    private HBox codeHeader(String language, TextArea area) {
        Button button = new Button("Copy " + language);
        button.disableProperty().bind(area.textProperty().isEmpty());
        button.setOnAction(event -> copy(area, language));
        HBox box = new HBox(12, new Label(language), button);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private void copy(TextArea area, String language) {
        if (area.getText().isEmpty()) return;
        ClipboardContent content = new ClipboardContent();
        content.putString(area.getText());
        boolean copied = Clipboard.getSystemClipboard().setContent(content);
        if (copied) area.selectAll();
        status.setText(copied ? language + " copied to clipboard" : "Clipboard unavailable");
    }

    @Override
    public void stop() {
        debounce.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
