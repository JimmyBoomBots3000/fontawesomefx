/* Licensed under the Apache License, Version 2.0. See LICENSE. */
import groovy.json.JsonSlurper
import java.nio.charset.StandardCharsets
import java.nio.file.Files

// Invoked by GMavenPlus in generate-sources. Inputs are pinned in the module;
// generated Java belongs exclusively in target/.
def input = new File(project.basedir, 'src/main/fontawesome')
def json = new JsonSlurper()
def metadata = json.parse(new File(input, 'metadata.json'), 'UTF-8')
def legacy = json.parse(new File(input, 'legacy.json'), 'UTF-8')
def entries = new TreeMap<String, List<String>>()
def constant = { String name ->
    String value = name.toUpperCase(Locale.ROOT).replace('-', '_')
    value.charAt(0) in '0'..'9' ? 'FA_' + value : value
}
metadata.icons.each { String name, data ->
    assert name ==~ /[a-z0-9]+(?:-[a-z0-9]+)*/ : "Invalid upstream icon name: ${name}"
    def styles = ['solid', 'brands', 'regular'].findAll { it in data.styles }
    assert styles : "No supported free style for ${name}"
    entries[constant(name)] = [name, styles.first()]
    styles.each { String style ->
        if (style == 'regular' || style != styles.first()) {
            entries[style.toUpperCase(Locale.ROOT) + '_' + constant(name)] = [name, style]
        }
    }
}
legacy.each { String name, List<String> mapping ->
    if (entries.containsKey(name) && entries[name] != mapping) {
        def original = entries[name]
        entries[original[1].toUpperCase(Locale.ROOT) + '_' + name] = original
    }
    entries[name] = mapping
}
def lines = entries.collect { String name, List<String> mapping ->
    assert name ==~ /[A-Z_][A-Z_0-9]*/ : "Invalid Java constant: ${name}"
    def (target, style) = mapping
    def icon = metadata.icons[target]
    assert icon != null && style in icon.styles : "Invalid mapping for ${name}: ${mapping}"
    int codePoint = Integer.parseInt(icon.unicode, 16)
    assert Character.isValidCodePoint(codePoint) : "Invalid Unicode for ${name}"
    String face = metadata.fonts[style]
    assert face != null && face ==~ /[A-Za-z0-9 ]+/ : "Invalid font face: ${face}"
    '    ' + name + '(0x' + Integer.toHexString(codePoint).toUpperCase(Locale.ROOT) + ', "' + face + '")'
}
['MEANPATH': 'f20c', 'TRIPADVISOR': 'f262'].each { String name, String code ->
    lines << '    /** Removed upstream; retained using the legacy font. */\n' +
            '    @Deprecated\n    ' + name + '(0x' + code + ', "FontAwesome")'
}
String template = new File(input, 'FontAwesomeIcon.java.template').getText('UTF-8')
String result = template.replace('@FONT_VERSION@', metadata.version)
        .replace('@ICON_CONSTANTS@', lines.join(',\n'))
assert !result.contains('@FONT_VERSION@') && !result.contains('@ICON_CONSTANTS@')
def output = new File(project.build.directory,
        'generated-sources/fontawesome/de/jensd/fx/glyphs/fontawesome/FontAwesomeIcon.java')
Files.createDirectories(output.parentFile.toPath())
// Avoid recompilation on an unchanged incremental build.
if (!output.isFile() || output.getText('UTF-8') != result) {
    Files.writeString(output.toPath(), result, StandardCharsets.UTF_8)
}
log.info("Generated ${lines.size()} Font Awesome Free ${metadata.version} constants in ${output}")
