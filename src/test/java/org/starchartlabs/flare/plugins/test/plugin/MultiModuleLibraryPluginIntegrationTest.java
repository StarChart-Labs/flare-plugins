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
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
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

        // Check constraints application
        Assert.assertTrue(result.getOutput()
                .contains("Applied configuration ':compile' dependency constraint: org.testng:testng:6.14.3"));

        // Check managed credentials setup
        Assert.assertTrue(result.getOutput().contains("Credentials configured: bintray"));

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

        // Check increased test logging application
        Assert.assertTrue(result.getOutput().contains("test Exception format: FULL"));
        Assert.assertTrue(result.getOutput().contains("test Quiet logging: [SKIPPED, FAILED]"));
        Assert.assertTrue(result.getOutput()
                .contains("test Info logging: [PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR]"));
        Assert.assertTrue(result.getOutput()
                .contains("test Debug logging: [STARTED, PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR]"));

        // Check source/javadoc jar tasks
        Path sourcesJar = singleProjectPath.resolve("build").resolve("libs")
                .resolve(singleProjectPath.getFileName().toString() + "-sources.jar");
        Path javadocJar = singleProjectPath.resolve("build").resolve("libs")
                .resolve(singleProjectPath.getFileName().toString() + "-javadoc.jar");

        verifyFile(sourcesJar.toFile(), "org/starchartlabs/flare/merge/coverage/reports/Main.java");
        verifyFile(javadocJar.toFile(), "org/starchartlabs/flare/merge/coverage/reports/Main.html");

        Assert.assertTrue(result.getOutput().contains("Artifact Verification: maven:sources"));
        Assert.assertTrue(result.getOutput().contains("Artifact Verification: maven:javadoc"));
    }

    @Test
    public void applyToMultiModuleProject() throws Exception {
        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(multiModuleProjectPath.toFile())
                .withArguments("build", "--info")
                .withGradleVersion("5.0")
                .build();

        // Check constraints application
        Assert.assertTrue(result.getOutput()
                .contains("Applied configuration ':one:compile' dependency constraint: org.testng:testng:6.14.3"));
        Assert.assertTrue(result.getOutput()
                .contains("Applied configuration ':two:compile' dependency constraint: org.testng:testng:6.14.3"));

        // Check managed credentials setup
        Assert.assertTrue(result.getOutput().contains("Credentials configured: bintray"));

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

        // Check increased test logging application
        Assert.assertTrue(result.getOutput().contains("test Exception format: FULL"));
        Assert.assertTrue(result.getOutput().contains("test Quiet logging: [SKIPPED, FAILED]"));
        Assert.assertTrue(result.getOutput()
                .contains("test Info logging: [PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR]"));
        Assert.assertTrue(result.getOutput()
                .contains("test Debug logging: [STARTED, PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR]"));

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

            Assert.assertTrue(
                    result.getOutput().contains("Artifact Verification: " + subProjectName + ":maven:sources"));
            Assert.assertTrue(
                    result.getOutput().contains("Artifact Verification: " + subProjectName + ":maven:javadoc"));
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
