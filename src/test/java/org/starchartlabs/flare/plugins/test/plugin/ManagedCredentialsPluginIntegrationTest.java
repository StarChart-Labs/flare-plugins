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
public class ManagedCredentialsPluginIntegrationTest {

    private static final Path BUILD_FILE_DIRECTORY = Paths.get("org", "starchartlabs", "flare", "plugins", "test",
            "managed", "credentials");

    private static final Path BUILD_FILE = BUILD_FILE_DIRECTORY.resolve("build.gradle");

    private Path projectPath;

    @BeforeClass
    public void setup() throws Exception {
        projectPath = TestGradleProject.builder(BUILD_FILE)
                .build()
                .getProjectDirectory();
    }

    // TODO romeara Try to figure out a way to test the environment variable scenario

    @Test
    public void systemPropertiesCredentials() throws Exception {
        String expectedUsername = "prop_user";
        String expectedPassword = "prop_password";

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(projectPath.toFile())
                .withArguments("credentialsPrintout", "-Dproperty_1=" + expectedUsername,
                        "-Dproperty_2=" + expectedPassword)
                .withGradleVersion("5.0")
                .build();

        Assert.assertTrue(result.getOutput().contains(expectedUsername + ":" + expectedPassword));

        TaskOutcome outcome = result.task(":credentialsPrintout").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));
    }

    @Test
    public void defaultCredentials() throws Exception {
        String expectedUsername = "username_1";
        String expectedPassword = "password_2";

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(projectPath.toFile())
                .withArguments("credentialsPrintout")
                .withGradleVersion("5.0")
                .build();

        Assert.assertTrue(result.getOutput().contains(expectedUsername + ":" + expectedPassword));

        TaskOutcome outcome = result.task(":credentialsPrintout").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));
    }

}
