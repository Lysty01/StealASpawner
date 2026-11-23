// Documentation checks: ./gradlew checkDocs (also run by ./gradlew check / build).
//
// 1. Every local Markdown link and image in the repository points to an existing file.
// 2. Every page listed in gitbook/SUMMARY.md exists, and every page in gitbook/ is listed in it.
// 3. .gitbook.yaml points to an existing root, readme and summary.
// 4. Every command and permission declared in plugin.yml is documented in the GitBook.

val docsRoot = layout.projectDirectory.asFile

tasks.register("checkDocs") {
    group = "verification"
    description = "Checks Markdown links, the GitBook summary and command/permission documentation."

    inputs.files(
        fileTree(docsRoot) {
            include("**/*.md", ".gitbook.yaml", "src/main/resources/plugin.yml")
            exclude("build/**", ".gradle/**", ".git/**", "run/**")
        }
    )
    outputs.upToDateWhen { false }

    doLast {
        val errors = mutableListOf<String>()
        fun rel(f: File) = f.relativeTo(docsRoot).invariantSeparatorsPath

        val linkRegex = Regex("""!?\[[^\]]*]\(\s*<?([^)\s>]+)>?(?:\s+"[^"]*")?\s*\)""")
        val fenceRegex = Regex("(?ms)^\\s*(```|~~~).*?^\\s*\\1")
        val inlineCodeRegex = Regex("`[^`\n]*`")

        fun stripCode(text: String) = text.replace(fenceRegex, "").replace(inlineCodeRegex, "")

        fun localTargets(file: File): List<Pair<String, File>> =
            linkRegex.findAll(stripCode(file.readText())).mapNotNull { match ->
                val raw = match.groupValues[1]
                val isExternal = Regex("^[a-zA-Z][a-zA-Z0-9+.-]*:").containsMatchIn(raw) || raw.startsWith("//")
                if (isExternal || raw.startsWith("#")) return@mapNotNull null
                val path = java.net.URLDecoder.decode(raw.substringBefore('#').substringBefore('?'), "UTF-8")
                if (path.isEmpty()) null else raw to file.parentFile.resolve(path).normalize()
            }.toList()

        // 1. Local links in every Markdown file
        val markdownFiles = fileTree(docsRoot) {
            include("**/*.md")
            exclude("build/**", ".gradle/**", ".git/**", "run/**")
        }.files.sortedBy { rel(it) }

        var linkCount = 0
        markdownFiles.forEach { file ->
            localTargets(file).forEach { (raw, target) ->
                linkCount++
                if (!target.exists()) errors += "Broken link in ${rel(file)}: ($raw)"
            }
        }

        // 2 & 3. GitBook configuration and summary
        val gitbookConfig = docsRoot.resolve(".gitbook.yaml")
        var gitbookDir = docsRoot.resolve("gitbook")
        var summaryName = "SUMMARY.md"
        var readmeName = "README.md"
        if (gitbookConfig.exists()) {
            val text = gitbookConfig.readText()
            Regex("""(?m)^root:\s*(\S+)""").find(text)?.let { gitbookDir = docsRoot.resolve(it.groupValues[1]).normalize() }
            Regex("""(?m)^\s+summary:\s*(\S+)""").find(text)?.let { summaryName = it.groupValues[1] }
            Regex("""(?m)^\s+readme:\s*(\S+)""").find(text)?.let { readmeName = it.groupValues[1] }
        } else {
            errors += "Missing .gitbook.yaml"
        }

        val summary = gitbookDir.resolve(summaryName)
        var pageCount = 0
        if (!gitbookDir.isDirectory) errors += "GitBook root not found: ${rel(gitbookDir)}"
        if (!gitbookDir.resolve(readmeName).exists()) errors += "GitBook readme not found: ${rel(gitbookDir.resolve(readmeName))}"
        if (!summary.exists()) {
            errors += "GitBook summary not found: ${rel(summary)}"
        } else {
            val listed = localTargets(summary).map { it.second }.toSet()
            val pages = fileTree(gitbookDir) { include("**/*.md") }.files.filter { it != summary }
            pageCount = pages.size
            pages.filter { it.normalize() !in listed }.sortedBy { rel(it) }.forEach {
                errors += "Page missing from ${rel(summary)}: ${rel(it)}"
            }
        }

        // 4. Commands and permissions declared in plugin.yml must be documented
        val pluginYml = docsRoot.resolve("src/main/resources/plugin.yml")
        fun topLevelKeys(section: String): List<String> {
            if (!pluginYml.exists()) return emptyList()
            val lines = pluginYml.readLines()
            val start = lines.indexOfFirst { it.trimEnd() == "$section:" }
            if (start < 0) return emptyList()
            val keys = mutableListOf<String>()
            var indent = -1
            for (line in lines.drop(start + 1)) {
                if (line.isBlank() || line.trimStart().startsWith("#")) continue
                val lineIndent = line.length - line.trimStart().length
                if (lineIndent == 0) break
                if (indent < 0) indent = lineIndent
                if (lineIndent == indent) keys += line.trim().substringBefore(':').trim('\'', '"')
            }
            return keys
        }

        val commandsDoc = gitbookDir.resolve("commands")
        val commandsText = if (commandsDoc.isDirectory) commandsDoc.walk().filter { it.extension == "md" }.joinToString("\n") { it.readText() } else ""
        val commands = topLevelKeys("commands")
        commands.forEach { cmd ->
            if (!commandsText.contains("/$cmd")) errors += "Command /$cmd (plugin.yml) is not documented in ${rel(commandsDoc)}/"
        }

        val permissionsDoc = gitbookDir.resolve("permissions/README.md")
        val permissionsText = if (permissionsDoc.exists()) permissionsDoc.readText() else ""
        val permissions = topLevelKeys("permissions")
        permissions.forEach { perm ->
            if (!permissionsText.contains("`$perm`")) errors += "Permission $perm (plugin.yml) is not documented in ${rel(permissionsDoc)}"
        }

        if (errors.isNotEmpty()) {
            throw GradleException("Documentation check failed (${errors.size} problem(s)):\n" + errors.joinToString("\n") { " - $it" })
        }
        logger.lifecycle(
            "Documentation OK: ${markdownFiles.size} Markdown files, $linkCount local links, " +
                "$pageCount GitBook pages, ${commands.size} commands and ${permissions.size} permissions checked."
        )
    }
}

tasks.named("check") { dependsOn("checkDocs") }
