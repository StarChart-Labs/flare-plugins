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
public class BintrayCredentialsPluginIntegrationTest {

    private static final Path BUILD_FILE_DIRECTORY = Paths.get("org", "starchartlabs", "flare", "plugins", "test",
            "bintray", "credentials");

    private static final Path BUILD_FILE = BUILD_FILE_DIRECTORY.resolve("build.gradle");

    private Path projectPath;

    private BuildResult buildResult;

    @BeforeClass
    public void setup() throws Exception {
        projectPath = TestGradleProject.builder(BUILD_FILE)
                .build()
                .getProjectDirectory();

        buildResult = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(projectPath.toFile())
                .withArguments("credentialsPrintout")
                .withGradleVersion("5.0")
                .build();
    }

    // TODO romeara Try to figure out a way to test the environment variable scenario

    // Defaults to blank
    @Test
    public void defaultCredentials() throws Exception {
        String expectedUsername = "";
        String expectedPassword = "";

        Assert.assertTrue(buildResult.getOutput().contains(expectedUsername + ":" + expectedPassword));

        TaskOutcome outcome = buildResult.task(":credentialsPrintout").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));
    }

}
