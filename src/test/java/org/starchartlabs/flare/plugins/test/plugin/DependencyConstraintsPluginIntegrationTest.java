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
public class DependencyConstraintsPluginIntegrationTest {

    private File testProjectDirectoryAll;

    @BeforeClass
    public void setup() throws Exception {
        URL directory = this.getClass().getClassLoader()
                .getResource("org/starchartlabs/flare/plugins/test/dependency/constraints");

        testProjectDirectoryAll = new File(directory.toURI());
    }

    @Test
    public void applyConstraints() throws Exception {
        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDirectoryAll)
                .withArguments("tasks", "--info")
                .withGradleVersion("5.0")
                .build();

        Assert.assertTrue(result.getOutput()
                .contains("Applied configuration ':compile' dependency constraint: group:artifact:1.0"));
        Assert.assertTrue(result.getOutput()
                .contains("Applied configuration ':compile' dependency constraint: group2:artifact2:2.0"));
        Assert.assertTrue(result.getOutput()
                .contains("Applied configuration ':createdAfterDsl' dependency constraint: group:artifact:1.0"));
        Assert.assertTrue(result.getOutput()
                .contains("Applied configuration ':createdAfterDsl' dependency constraint: group2:artifact2:2.0"));

        TaskOutcome outcome = result.task(":tasks").getOutcome();
        Assert.assertTrue(TaskOutcome.SUCCESS.equals(outcome));
    }

}
