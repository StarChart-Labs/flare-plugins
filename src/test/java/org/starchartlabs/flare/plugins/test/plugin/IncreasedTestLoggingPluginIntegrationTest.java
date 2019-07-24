/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.plugin;

import java.nio.file.Path;
import java.nio.file.Paths;

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
public class IncreasedTestLoggingPluginIntegrationTest {

    private static final Path BUILD_FILE_DIRECTORY = Paths.get("org", "starchartlabs", "flare", "plugins", "test",
            "increased", "test", "logging");

    private static final Path BUILD_FILE = BUILD_FILE_DIRECTORY.resolve("build.gradle");

    private Path projectPath;

    @BeforeClass
    public void setup() throws Exception {
        projectPath = TestGradleProject.builder(BUILD_FILE)
                .build()
                .getProjectDirectory();
    }

    @Test
    public void applyPlugin() throws Exception {
        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(projectPath.toFile())
                .withArguments("testPrintout")
                .withGradleVersion("5.0")
                .build();

        Assert.assertTrue(result.getOutput().contains("test Exception format: FULL"));
        Assert.assertTrue(result.getOutput().contains("test Quiet logging: [SKIPPED, FAILED]"));
        Assert.assertTrue(result.getOutput()
                .contains("test Info logging: [PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR]"));
        Assert.assertTrue(result.getOutput()
                .contains("test Debug logging: [STARTED, PASSED, SKIPPED, FAILED, STANDARD_OUT, STANDARD_ERROR]"));

        TaskOutcome outcome = result.task(":testPrintout").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));
    }

}
