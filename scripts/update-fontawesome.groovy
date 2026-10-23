/* Licensed under the Apache License, Version 2.0. See LICENSE. */
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.awt.Font
import java.nio.file.Files
import java.security.MessageDigest
import java.util.zip.ZipFile

// Explicit maintenance operation only: -Pupdate-fontawesome -Dfontawesome.archive=...
// Imports source data and font resources; it never writes Java into src/main/java.
String archivePath = session.userProperties.getProperty('fontawesome.archive')
assert archivePath : 'Supply -Dfontawesome.archive=/path/to/fontawesome-free-VERSION-desktop.zip'
def archive = new File(archivePath)
assert archive.isFile() : "Archive not found: ${archive}"
try (def zip = new ZipFile(archive)) {
    def metadataEntry = zip.entries().find { it.name.endsWith('/metadata/icons.json') }
    assert metadataEntry != null : 'Missing upstream icons.json'
    String prefix = metadataEntry.name.substring(0, metadataEntry.name.indexOf('metadata/'))
    def match = prefix =~ /^fontawesome-free-(\d+\.\d+\.\d+)-desktop\/$/
    assert match.matches() : "Unexpected desktop archive layout: ${prefix}"
    String version = match.group(1)
    def upstream = new JsonSlurper().parse(zip.getInputStream(metadataEntry), 'UTF-8')
    def icons = new TreeMap()
    upstream.each { String name, data ->
        if (data.free) icons[name] = [unicode: data.unicode, styles: data.free]
    }
    assert !icons.isEmpty() : 'No free icons in archive'
    def fonts = [:]
    def resources = [:]
    [solid: 'Free-Solid-900.otf', regular: 'Free-Regular-400.otf', brands: 'Brands-Regular-400.otf'].each { style, suffix ->
        def entry = zip.entries().find { it.name.startsWith(prefix + 'otfs/') && it.name.endsWith(suffix) }
        assert entry != null : "Missing ${style} font"
        byte[] bytes = zip.getInputStream(entry).withCloseable { it.readAllBytes() }
        fonts[style] = Font.createFont(Font.TRUETYPE_FONT, new ByteArrayInputStream(bytes)).getFontName(Locale.ROOT)
        resources[style] = bytes
    }
    def licenseEntry = zip.getEntry(prefix + 'LICENSE.txt')
    assert licenseEntry != null : 'Missing upstream license'
    byte[] license = zip.getInputStream(licenseEntry).withCloseable { it.readAllBytes() }
    String sha256 = MessageDigest.getInstance('SHA-256').digest(Files.readAllBytes(archive.toPath())).encodeHex().toString()
    def metadata = [version: version,
                    source: "https://github.com/FortAwesome/Font-Awesome/releases/download/${version}/fontawesome-free-${version}-desktop.zip",
                    sha256: sha256, fonts: fonts, icons: icons]
    def module = project.basedir
    new File(module, 'src/main/fontawesome/metadata.json').setText(JsonOutput.prettyPrint(JsonOutput.toJson(metadata)) + '\n', 'UTF-8')
    def resource = new File(module, 'src/main/resources/de/jensd/fx/glyphs/fontawesome')
    resources.each { style, bytes -> Files.write(new File(resource, "fa-${style}.otf").toPath(), bytes) }
    Files.write(new File(module, 'src/main/resources/license/LICENSE.txt').toPath(), license)
    Files.write(new File(module, '../../licenses/fontawesome-free.txt').toPath(), license)
    log.info("Imported Font Awesome Free ${version}; archive SHA-256: ${sha256}")
}
