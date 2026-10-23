# Third-party notices

FontAwesomeFX's Java code remains under [Apache License 2.0](LICENSE).
Copyright headers and author credits in inherited sources are preserved.
This fork is based on the work of [Jens Deters](https://github.com/Jerady),
with contributions from other authors credited in the source, including
Diego Cirujano Cuesta. See [NOTICE](NOTICE) for the fork's changes.

The fonts are separately licensed; the Java code's license does not relicense them.
The following notices and license texts are bundled under `META-INF` in each JAR.
Each JAR contains only the font files belonging to its module; this shared notice
lists all fonts in the repository, not necessarily all fonts in a particular JAR.

| Bundled assets                                           | Attribution                           | License text                                                                                                                            |
|----------------------------------------------------------|---------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| Font Awesome Free 7.3.1 solid, regular, brands           | Fonticons, Inc. / Font Awesome        | [Official Free license](licenses/fontawesome-free.txt); fonts use OFL 1.1                                                               |
| Font Awesome 4.7.0 compatibility font                    | Dave Gandy / Fort Awesome             | [Attribution](licenses/legacy-fonts.txt), [OFL 1.1](licenses/OFL-1.1.txt)                                                               |
| Material Design Icons 1.7.22                             | Austin Andrews and Google             | [Upstream license and notices](licenses/material-design-icons.txt)                                                                      |
| Material Icons, including the copy in materialstackicons | Google, Inc.                          | [Attribution](licenses/legacy-fonts.txt), [Apache 2.0](LICENSE)                                                                         |
| Octicons 4.3.0                                           | GitHub, Inc.                          | [Upstream MIT notice](licenses/octicons.txt); bundled legacy font uses [OFL 1.1](licenses/OFL-1.1.txt) per its original module metadata |
| Weather Icons 2.0.10                                     | Erik Flowers and Lukas Bischoff       | [Attribution](licenses/legacy-fonts.txt), [OFL 1.1](licenses/OFL-1.1.txt)                                                               |
| 525icons 3.0                                             | Rune V Cappelen Stromsted (@runestro) | [Attribution](licenses/legacy-fonts.txt), [OFL 1.1](licenses/OFL-1.1.txt)                                                               |
| EmojiOne SVG font and artwork                            | Adobe Systems Incorporated; EmojiOne  | [Attribution](licenses/legacy-fonts.txt), [Adobe MIT](licenses/emojione-adobe-MIT.txt), [artwork CC BY 4.0](licenses/CC-BY-4.0.txt)     |

Font Awesome's official Free license also describes CC BY 4.0 for its SVG/JS icons
and MIT for its non-font code. Its OpenType fonts are the assets bundled here.
Brand names and logos remain the property of their respective owners.

The EmojiOne font's embedded license specifies **CC BY 4.0**, rather than the
CC BY-SA 4.0 previously listed in this repository's `fontinfo.properties`.
This notice follows the actual bundled font metadata. It does not apply the
licensing terms of newer JoyPixels releases retroactively to this legacy font.

License sources:

- [Font Awesome Free](https://fontawesome.com/license/free)
- [Material Design Icons 1.7.22](https://github.com/Templarian/MaterialDesign-Webfont/blob/v1.7.22/license.md)
- [Octicons 4.3.0](https://github.com/primer/octicons/blob/v4.3.0/LICENSE)
- [Weather Icons](https://github.com/erikflowers/weather-icons#license)
- [Google Material Icons](https://github.com/google/material-design-icons)
- [Adobe EmojiOne Color](https://github.com/adobe-fonts/emojione-color)

The Maven Wrapper scripts retain their Apache Software Foundation license headers.
Maven dependencies such as OpenJFX and JUnit are resolved separately, not shaded into
this project's JARs; their own notices accompany their distributions.
