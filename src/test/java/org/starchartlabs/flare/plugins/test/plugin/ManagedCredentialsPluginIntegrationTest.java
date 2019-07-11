/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.plugin;

import java.io.File;
import java.net.URL;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.starchartlabs.flare.plugins.test.IntegrationTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(value = { IntegrationTestListener.class })
public class ManagedCredentialsPluginIntegrationTest {

    private File testProjectDirectory;

    @BeforeClass
    public void setup() throws Exception {
        URL directory = this.getClass().getClassLoader()
                .getResource("org/starchartlabs/flare/plugins/test/managed/credentials");

        testProjectDirectory = new File(directory.toURI());
    }

    // TODO romeara Try to figure out a way to test the environment variable scenario

    @Test
    public void systemPropertiesCredentials() throws Exception {
        String expectedUsername = "prop_user";
        String expectedPassword = "prop_password";

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDirectory)
                .withArguments("credentialsPrintout", "-Dproperty_1=" + expectedUsername,
                        "-Dproperty_2=" + expectedPassword)
                .withGradleVersion("5.0")
                .build();

        result.getOutput().contains(expectedUsername + ":" + expectedPassword);

        TaskOutcome outcome = result.task(":credentialsPrintout").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));
    }

    @Test
    public void defaultCredentials() throws Exception {
        String expectedUsername = "username_1";
        String expectedPassword = "password_2";

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDirectory)
                .withArguments("credentialsPrintout")
                .withGradleVersion("5.0")
                .build();

        result.getOutput().contains(expectedUsername + ":" + expectedPassword);

        TaskOutcome outcome = result.task(":credentialsPrintout").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));
    }

}
