# FontAwesomeFX

Scalable icon fonts for JavaFX, with **Font Awesome Free 7.3.1**, a searchable
icon viewer, and a Maven build.

This is an independent fork of **[FontAwesomeFX](https://bitbucket.org/Jerady/fontawesomefx) by [Jens Deters](https://github.com/Jerady)**.
The original project supplied the JavaFX glyph controls, factories, builders, and
icon-pack integrations that this fork continues to use. Credit also belongs to
its other contributors, whose notices remain in the source.

This fork updates the Font Awesome fonts and catalog, supports switching between
solid, regular, and brand icons, replaces the retired Gradle/Bintray build, and
adds a standalone icon viewer. The other bundled icon packs retain their existing
versions.

## Build and install locally

Use **JDK 17 or newer**. Maven Wrapper downloads Maven 3.9.9; you do not need a
separate Maven installation. Dependencies come from Maven Central. The library
sources retain Java 11 bytecode compatibility; the configured JavaFX version is
17.0.18. Building and running the tools is tested with JDK 17.

From the repository root:

```sh
./mvnw clean install
```

On Windows, use `mvnw.cmd`. On macOS, if your shell defaults to Java 8:

```sh
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
./mvnw clean install
```

This runs the tests and installs the parent POM, module JARs, and source JARs into
`~/.m2/repository` (or the local repository configured in your Maven settings).
To build only Font Awesome and its required modules:

```sh
./mvnw -pl :fontawesomefx-fontawesome -am clean install
```

Other useful commands:

```sh
./mvnw clean verify           # Build and test without installing
./mvnw -Pjavadoc install      # Also generate and install Javadoc JARs
```

An optional desktop smoke test opens and closes the viewer and validates filtering,
resizing, and the generated FXML (requires a graphical desktop):

```sh
./mvnw -pl :fontawesomefx-fontawesome -am -Dfontawesomefx.uiTests=true test
```

The root `pom.xml` is the Maven reactor and shared configuration. Module sources
remain under `fontawesomefx/`. The Gradle build and wrappers have been removed.

## Add to your application

After installing this fork locally, add:

```xml
<dependency>
    <groupId>io.github.jimmyboombots3000</groupId>
    <artifactId>fontawesomefx-fontawesome</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

The commons module and JavaFX dependencies are transitive. This fork's artifacts
are built locally; these instructions do not imply they have been published to
Maven Central. Applications that manage their own JavaFX version should align
all their JavaFX dependencies.

```java
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.HOUSE);
icon.setGlyphSize(24);
icon.setStyle("-fx-fill: cornflowerblue;");

// The view selects the appropriate font when the icon changes.
icon.setIcon(FontAwesomeIcon.REGULAR_HEART);
icon.setIcon(FontAwesomeIcon.GITHUB);
```

FXML:

```xml
<?import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView?>
<FontAwesomeIconView glyphName="HOUSE" glyphSize="24" />
```

Modular applications require `de.jensd.fx.fontawesomefx.fontawesome` in their
`module-info.java`, alongside the JavaFX modules they use. The inherited library API retains its Java package and module names.
The new viewer code uses `io.github.jimmyboombots3000.fontawesomefx.viewer`.

Prefer `setGlyphSize(...)` / `glyphSize` for numeric sizing. The inherited
`setSize(String)` method uses a reflective JavaFX CSS parser; applications using
that method need this JVM option on the module path:

```text
--add-opens javafx.graphics/javafx.css=de.jensd.fx.fontawesomefx.commons
```

For applications running FontAwesomeFX on the classpath, use `ALL-UNNAMED` in
place of the commons module name. The Maven demo launch configuration includes
the module-path option.

## Browse icons

After `./mvnw install`, launch the standalone viewer from the repository root:

```sh
./mvnw -pl :fontawesomefx-fontawesome javafx:run
```

The viewer provides:

- Debounced search by name, multiple words, initials, and approximate spelling.
- Filters for solid, regular, brand, and legacy icons.
- Adjustable preview size, including mouse-wheel adjustment over the spinner.
- Java and FXML examples for the selected icon, with copy buttons; clicking an
  example also copies it.
- A virtualized table that creates preview nodes for visible cells.

Press Escape in the search field to clear it. The viewer uses only JavaFX and
this library; it does not require CommonsFX, ControlsFX, or Apache Commons.
Its entry point is `io.github.jimmyboombots3000.fontawesomefx.viewer.IconViewer`.

## Icon names and compatibility

The Font Awesome catalog contains all current free solid, regular, and brand
icon/style pairs, plus historical names. Regular variants use `REGULAR_`, such
as `REGULAR_HEART`. Names starting with a digit use `FA_`, such as `FA_500PX`.
Additional style variants use their style prefix when needed.

All 786 existing enum names are retained. Where an old name differs from the
current upstream meaning, it keeps its compatibility mapping and the modern
icon is also available with a style prefix, such as `SOLID_REPEAT`.
`MEANPATH` and `TRIPADVISOR` were removed upstream; these two deprecated constants
alone use the retained Font Awesome 4.7 font. Other icon designs follow the
current upstream artwork.

The enum is generated automatically during Maven's `generate-sources` phase by
`scripts/generate-fontawesome.groovy`, using GMavenPlus. Generated Java goes to
`fontawesomefx/fontawesomefx-fontawesome/target/generated-sources/fontawesome`,
which Maven registers as a source root. `clean` removes it; the next build
regenerates it. Source and Javadoc JARs include the generated API.

Pinned build inputs live in the module's `src/main/fontawesome/` directory:

- `metadata.json`: free icon names, Unicode values, styles, font faces, upstream
  version and download URL, and the imported desktop archive's SHA-256.
- `legacy.json`: compatibility mappings for historical enum names.
- `FontAwesomeIcon.java.template`: the enum structure and original attribution.

Normal builds use these local inputs and the checked-in font resources. They
require neither Python nor an upstream Font Awesome download. Groovy is a Maven
plugin dependency only; applications using the library do not depend on it.

To update the pinned inputs and fonts, download an official
[Font Awesome Free desktop ZIP](https://github.com/FortAwesome/Font-Awesome/releases).
Run the explicit import profile with an absolute path to that archive:

```sh
./mvnw -pl :fontawesomefx-fontawesome -am -Pupdate-fontawesome \
  -Dfontawesome.archive=/absolute/path/to/fontawesome-free-7.3.1-desktop.zip verify
```

The import script runs in `initialize`, updates metadata, font resources, and the
bundled Free license notices, then normal source generation runs. It does not
write Java into `src/main/java`. Review the imported changes, update
`fontinfo.properties` and font version references in this README, and commit the
inputs and resources. Generated files under `target/` are never committed.
Bundled font versions are separate from the version of this fork.

## Modules

All artifacts use group ID `io.github.jimmyboombots3000`.
Modules inherit the parent version, and internal dependencies
use `${project.version}`. Bundled font versions remain independent.

| Artifact                           | Contents                                    |
|------------------------------------|---------------------------------------------|
| `fontawesomefx-commons`            | Shared glyph views, builders, and factories |
| `fontawesomefx-controls`           | Additional JavaFX controls                  |
| `fontawesomefx-fontawesome`        | Font Awesome Free and icon viewer           |
| `fontawesomefx-materialdesignfont` | Material Design Icons                       |
| `fontawesomefx-materialicons`      | Google Material Icons                       |
| `fontawesomefx-materialstackicons` | Stacked Material Icons                      |
| `fontawesomefx-octicons`           | Octicons                                    |
| `fontawesomefx-weathericons`       | Weather Icons                               |
| `fontawesomefx-icons525`           | 525icons                                    |
| `fontawesomefx-emojione`           | Legacy EmojiOne font                        |

## Contributing

Bug reports, documentation improvements, tests, and focused pull requests are
welcome. Please read [CONTRIBUTING.md](CONTRIBUTING.md) for development setup,
testing, compatibility guidelines, and contribution licensing. Open an issue
before starting a substantial feature or public API change.

## License and attribution

The Java code remains licensed under the **[Apache License 2.0](LICENSE)**.
Substantial original code by [Jens Deters](https://github.com/Jerady) remains;
its copyright and author notices are preserved. See [NOTICE](NOTICE) for
attribution and a description of this fork's changes. The original project is
[available on Bitbucket](https://bitbucket.org/Jerady/fontawesomefx/).

The bundled fonts and artwork have their own licenses. Font Awesome's fonts
use SIL OFL 1.1; other packs include OFL, Apache, MIT, and Creative Commons
licensed material. See [THIRD-PARTY-NOTICES.md](THIRD-PARTY-NOTICES.md) and
[licenses/](licenses/) for the applicable texts and credits. Maven packages
these notices in the JARs under `META-INF`.
