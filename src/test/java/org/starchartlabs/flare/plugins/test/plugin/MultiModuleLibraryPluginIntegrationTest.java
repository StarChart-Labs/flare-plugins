/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.plugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
                .addJavaFile("org.starchartlabs.flare.merge.coverage.reports.one", "MainOne")
                .addTestFile("org.starchartlabs.flare.test.merge.coverage.reports.one", "MainOneTest",
                        "org.starchartlabs.flare.merge.coverage.reports.one.MainOne")
                .and()
                .subProject("two", SUB_PROJECT_BUILD_FILE)
                .addJavaFile("org.starchartlabs.flare.merge.coverage.reports.two", "MainTwo")
                .addTestFile("org.starchartlabs.flare.test.merge.coverage.reports.two", "MainTwoTest",
                        "org.starchartlabs.flare.merge.coverage.reports.two.MainTwo")
                .and()
                .build()
                .getProjectDirectory();
    }

    @Test
    public void applyToSingleProject() throws Exception {
        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(singleProjectPath.toFile())
                .withArguments("check", "--info")
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
    }

    @Test
    public void applyToMultiModuleProject() throws Exception {
        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(multiModuleProjectPath.toFile())
                .withArguments("check", "--info")
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
                        "<class name=\"org/starchartlabs/flare/merge/coverage/reports/one/MainOne\" sourcefilename=\"MainOne.java\">"));
        boolean coveredMainTwoFile = Files.lines(reportOutput)
                .map(String::trim)
                .anyMatch(line -> line.contains(
                        "<class name=\"org/starchartlabs/flare/merge/coverage/reports/two/MainTwo\" sourcefilename=\"MainTwo.java\">"));

        Assert.assertTrue(coveredMainOneFile, "Coverage report missing line for expected source file MainOne");
        Assert.assertTrue(coveredMainTwoFile, "Coverage report missing line for expected source file MainTwo");
    }

}
