/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.starchartlabs.flare.plugins.test.IntegrationTestListener;
import org.starchartlabs.flare.plugins.test.TestGradleProject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(value = { IntegrationTestListener.class })
public class SourceJarsPluginIntegrationTest {

    private static final Path BUILD_FILE_DIRECTORY = Paths.get("org", "starchartlabs", "flare", "plugins", "test",
            "source", "jars");

    private static final Path SIMPLE_BUILD_FILE = BUILD_FILE_DIRECTORY.resolve("simpleBuild.gradle");

    private static final Path MAVEN_PUBLISH_BUILD_FILE = BUILD_FILE_DIRECTORY.resolve("mavenPublishBuild.gradle");

    private Path simpleProjectPath;

    private Path mavenPublishProjectPath;

    @BeforeClass
    public void setup() throws Exception {
        simpleProjectPath = TestGradleProject.builder(SIMPLE_BUILD_FILE)
                .addJavaFile("org.starchartlabs.flare.merge.coverage.reports", "Main")
                .addTestFile("org.starchartlabs.flare.test.merge.coverage.reports", "MainTest",
                        "org.starchartlabs.flare.merge.coverage.reports.Main")
                .build()
                .getProjectDirectory();

        mavenPublishProjectPath = TestGradleProject.builder(MAVEN_PUBLISH_BUILD_FILE)
                .addJavaFile("org.starchartlabs.flare.merge.coverage.reports", "Main")
                .addTestFile("org.starchartlabs.flare.test.merge.coverage.reports", "MainTest",
                        "org.starchartlabs.flare.merge.coverage.reports.Main")
                .build()
                .getProjectDirectory();
    }

    @Test
    public void applyPluginSimpleProject() throws Exception {
        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(simpleProjectPath.toFile())
                .withArguments("assemble")
                .withGradleVersion("5.0")
                .build();

        // Verify Jar contains expected source or JavaDoc file(s)
        Path sourcesJar = simpleProjectPath.resolve("build").resolve("libs")
                .resolve(simpleProjectPath.getFileName().toString() + "-sources.jar");
        Path javadocJar = simpleProjectPath.resolve("build").resolve("libs")
                .resolve(simpleProjectPath.getFileName().toString() + "-javadoc.jar");

        verifyFile(sourcesJar.toFile(), "org/starchartlabs/flare/merge/coverage/reports/Main.java");
        verifyFile(javadocJar.toFile(), "org/starchartlabs/flare/merge/coverage/reports/Main.html");

        TaskOutcome outcome = result.task(":assemble").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));

        outcome = result.task(":sourcesJar").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));

        outcome = result.task(":javadocJar").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));
    }

    @Test
    public void applyPluginMavenPublishProject() throws Exception {
        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(mavenPublishProjectPath.toFile())
                .withArguments("assemble", "outputMavenArtifacts")
                .withGradleVersion("5.0")
                .build();

        // Verify Jar contains expected source or JavaDoc file(s)
        Path sourcesJar = mavenPublishProjectPath.resolve("build").resolve("libs")
                .resolve(mavenPublishProjectPath.getFileName().toString() + "-sources.jar");
        Path javadocJar = mavenPublishProjectPath.resolve("build").resolve("libs")
                .resolve(mavenPublishProjectPath.getFileName().toString() + "-javadoc.jar");

        verifyFile(sourcesJar.toFile(), "org/starchartlabs/flare/merge/coverage/reports/Main.java");
        verifyFile(javadocJar.toFile(), "org/starchartlabs/flare/merge/coverage/reports/Main.html");

        // Verify artifact output
        Assert.assertTrue(result.getOutput().contains("Artifact Verification: maven:sources"));
        Assert.assertTrue(result.getOutput().contains("Artifact Verification: maven:javadoc"));

        TaskOutcome outcome = result.task(":assemble").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));

        outcome = result.task(":outputMavenArtifacts").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));

        outcome = result.task(":sourcesJar").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));

        outcome = result.task(":javadocJar").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));
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
