/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.starchartlabs.alloy.core.Strings;
import org.starchartlabs.flare.plugins.test.TestGradleProject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MultiModuleLibraryPluginIntegrationTest {

    private static final Path BUILD_FILE_DIRECTORY = Paths.get("org", "starchartlabs", "flare", "plugins", "test",
            "multi", "module", "library");

    private static final Path SINGLE_PROJECT_BUILD_FILE = BUILD_FILE_DIRECTORY.resolve("singleProjectBuild.gradle");

    private static final Path ROOT_PROJECT_BUILD_FILE = BUILD_FILE_DIRECTORY.resolve("rootProjectBuild.gradle");

    private static final Path SUB_PROJECT_BUILD_FILE = BUILD_FILE_DIRECTORY.resolve("subProjectBuild.gradle");

    private static final Path DEPENDENCIES_FILE = BUILD_FILE_DIRECTORY.resolve("dependencies.properties");

    private static final Path DEVELOPERS_FILE = BUILD_FILE_DIRECTORY.resolve("developers.properties");

    private static final Path CONTRIBUTORS_FILE = BUILD_FILE_DIRECTORY.resolve("contributors.properties");

    private Path singleProjectPath;

    private Path multiModuleProjectPath;

    @BeforeClass
    public void setupProjects() throws Exception {
        singleProjectPath = TestGradleProject.builder(SINGLE_PROJECT_BUILD_FILE)
                .addJavaFile("org.starchartlabs.flare.merge.coverage.reports", "Main")
                .addTestFile("org.starchartlabs.flare.test.merge.coverage.reports", "MainTest",
                        "org.starchartlabs.flare.merge.coverage.reports.Main")
                .addFile(DEPENDENCIES_FILE, Paths.get("dependencies.properties"))
                .build()
                .getProjectDirectory();

        multiModuleProjectPath = TestGradleProject.builder(ROOT_PROJECT_BUILD_FILE)
                .addFile(DEPENDENCIES_FILE, Paths.get("dependencies.properties"))
                .addFile(DEVELOPERS_FILE, Paths.get("developers.properties"))
                .addFile(CONTRIBUTORS_FILE, Paths.get("contributors.properties"))
                .subProject("one", SUB_PROJECT_BUILD_FILE)
                .addJavaFile("org.starchartlabs.flare.merge.coverage.reports.one", "Mainone")
                .addTestFile("org.starchartlabs.flare.test.merge.coverage.reports.one", "MainoneTest",
                        "org.starchartlabs.flare.merge.coverage.reports.one.Mainone")
                .and()
                .subProject("two", SUB_PROJECT_BUILD_FILE)
                .addJavaFile("org.starchartlabs.flare.merge.coverage.reports.two", "Maintwo")
                .addTestFile("org.starchartlabs.flare.test.merge.coverage.reports.two", "MaintwoTest",
                        "org.starchartlabs.flare.merge.coverage.reports.two.Maintwo")
                .and()
                .build()
                .getProjectDirectory();
    }

    @Test
    public void applyToSingleProject() throws Exception {
        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(singleProjectPath.toFile())
                .withArguments("build", "--info")
                .withGradleVersion("5.0")
                .build();

        List<String> expectedLines = new ArrayList<>();

        // Check constraints application
        expectedLines.add("Applied configuration ':compile' dependency constraint: org.testng:testng:6.14.3");

        // Check managed credentials setup
        expectedLines.add("Credentials configured: bintray");

        // Check increased test logging application
        expectedLines.add("test Exception format: FULL");
        expectedLines.add("test Quiet logging: [SKIPPED, FAILED]");
        expectedLines.add("test Info logging: [PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR]");
        expectedLines.add("test Debug logging: [STARTED, PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR]");

        // Check source/javadoc jar tasks
        expectedLines.add("Artifact Verification: maven:sources");
        expectedLines.add("Artifact Verification: maven:javadoc");

        // Check meta data configuration
        expectedLines.add("url: http://url");

        expectedLines.add("scm.vcsUrl: http://scm/url");
        expectedLines.add("scm.connection: scm/connection");
        expectedLines.add("scm.developerConnection: scm/developerConnection");

        expectedLines.add("developer: developer/id:developer/name:developer/url");

        expectedLines.add("contributor: contributor/name:contributor/url");

        expectedLines.add("license: license/name:license/tag:license/url:license/distribution");
        expectedLines.add("license: The Apache Software License, Version 2.0:Apache 2.0"
                + ":http://www.apache.org/licenses/LICENSE-2.0.txt:repo");
        expectedLines.add("license: The MIT License:MIT:https://opensource.org/licenses/MIT:mit/distribution");
        expectedLines.add(
                "license: Eclipse Public License 1.0:EPL:https://opensource.org/licenses/EPL-1.0:epl/distribution");

        for (String expectedLine : expectedLines) {
            Assert.assertTrue(result.getOutput().contains(expectedLine),
                    Strings.format("Did not find expected line '%s'", expectedLine));
        }

        // Check merge coverage reports application
        TaskOutcome outcome = result.task(":mergeCoverageReports").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));

        Path reportOutput = singleProjectPath.resolve(Paths.get("build", "reports", "jacoco", "report.xml"));

        Assert.assertTrue(Files.exists(reportOutput));

        boolean coveredMainFile = Files.lines(reportOutput)
                .map(String::trim)
                .anyMatch(line -> line.contains(
                        "<class name=\"org/starchartlabs/flare/merge/coverage/reports/Main\" sourcefilename=\"Main.java\">"));

        Assert.assertTrue(coveredMainFile, "Coverage report missing line for expected source file");

        // Check source/javadoc jar tasks
        Path sourcesJar = singleProjectPath.resolve("build").resolve("libs")
                .resolve(singleProjectPath.getFileName().toString() + "-sources.jar");
        Path javadocJar = singleProjectPath.resolve("build").resolve("libs")
                .resolve(singleProjectPath.getFileName().toString() + "-javadoc.jar");

        verifyFile(sourcesJar.toFile(), "org/starchartlabs/flare/merge/coverage/reports/Main.java");
        verifyFile(javadocJar.toFile(), "org/starchartlabs/flare/merge/coverage/reports/Main.html");
    }

    @Test
    public void applyToMultiModuleProject() throws Exception {
        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(multiModuleProjectPath.toFile())
                .withArguments("build", "--info")
                .withGradleVersion("5.0")
                .build();

        List<String> expectedLines = new ArrayList<>();

        // Check constraints application
        expectedLines.add("Applied configuration ':one:compile' dependency constraint: org.testng:testng:6.14.3");
        expectedLines.add("Applied configuration ':two:compile' dependency constraint: org.testng:testng:6.14.3");

        // Check managed credentials setup
        expectedLines.add("Credentials configured: bintray");

        // Check increased test logging application
        expectedLines.add("test Exception format: FULL");
        expectedLines.add("test Quiet logging: [SKIPPED, FAILED]");
        expectedLines.add("test Info logging: [PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR]");
        expectedLines.add("test Debug logging: [STARTED, PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR]");

        // Check merge coverage reports application
        TaskOutcome outcome = result.task(":mergeCoverageReports").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));

        Path reportOutput = multiModuleProjectPath.resolve(Paths.get("build", "reports", "jacoco", "report.xml"));

        Assert.assertTrue(Files.exists(reportOutput));

        boolean coveredMainOneFile = Files.lines(reportOutput)
                .map(String::trim)
                .anyMatch(line -> line.contains(
                        "<class name=\"org/starchartlabs/flare/merge/coverage/reports/one/Mainone\" sourcefilename=\"Mainone.java\">"));
        boolean coveredMainTwoFile = Files.lines(reportOutput)
                .map(String::trim)
                .anyMatch(line -> line.contains(
                        "<class name=\"org/starchartlabs/flare/merge/coverage/reports/two/Maintwo\" sourcefilename=\"Maintwo.java\">"));

        Assert.assertTrue(coveredMainOneFile, "Coverage report missing line for expected source file Mainone");
        Assert.assertTrue(coveredMainTwoFile, "Coverage report missing line for expected source file Maintwo");

        // Check source/javadoc jar tasks
        for (String subProjectName : Arrays.asList("one", "two")) {
            Path sourcesJar = multiModuleProjectPath.resolve(subProjectName).resolve("build").resolve("libs")
                    .resolve(subProjectName + "-sources.jar");
            Path javadocJar = multiModuleProjectPath.resolve(subProjectName).resolve("build").resolve("libs")
                    .resolve(subProjectName + "-javadoc.jar");

            verifyFile(sourcesJar.toFile(), "org/starchartlabs/flare/merge/coverage/reports/" + subProjectName + "/Main"
                    + subProjectName + ".java");
            verifyFile(javadocJar.toFile(), "org/starchartlabs/flare/merge/coverage/reports/" + subProjectName + "/Main"
                    + subProjectName + ".html");

            expectedLines.add("Artifact Verification: " + subProjectName + ":maven:sources");
            expectedLines.add("Artifact Verification: " + subProjectName + ":maven:javadoc");

            // Check meta data configuration
            expectedLines.add("url: https://github.com/owner/" + subProjectName);

            expectedLines.add("scm.vcsUrl: https://github.com/owner/" + subProjectName);
            expectedLines.add("scm.connection: scm:git:git://github.com/owner/" + subProjectName + ".git");
            expectedLines.add("scm.developerConnection: scm:git:ssh://github.com/owner/" + subProjectName + ".git");

            expectedLines
            .add("developer: dev-file-usernameonly:dev-file-usernameonly:https://github.com/dev-file-usernameonly");
            expectedLines.add("developer: dev-file-username:dev-file-name:https://github.com/dev-file-username");

            expectedLines.add("contributor: contrib-file-usernameonly:https://github.com/contrib-file-usernameonly");
            expectedLines.add("contributor: contrib-file-name:https://github.com/contrib-file-username");

            expectedLines.add("license: The MIT License:MIT:https://opensource.org/licenses/MIT:repo");
        }

        for (String expectedLine : expectedLines) {
            Assert.assertTrue(result.getOutput().contains(expectedLine),
                    Strings.format("Did not find expected line '%s'", expectedLine));
        }
    }

    private void verifyFile(File archiveFile, String expectedFilePath) throws IOException {
        boolean found = false;

        try (JarFile jarFile = new JarFile(archiveFile)) {
            found = Collections.list(jarFile.entries()).stream()
                    .map(JarEntry::getName)
                    .anyMatch(name -> Objects.equals(name, expectedFilePath));
        }

        Assert.assertTrue(found, "Did not find expected file " + expectedFilePath);
    }

}
