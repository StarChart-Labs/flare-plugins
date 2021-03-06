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
public class DependencyConstraintsPluginIntegrationTest {

    private static final Path BUILD_FILE_DIRECTORY = Paths.get("org", "starchartlabs", "flare", "plugins", "test",
            "dependency", "constraints");

    private static final Path BUILD_FILE = BUILD_FILE_DIRECTORY.resolve("build.gradle");

    private static final Path DEPENDENCIES_FILE = BUILD_FILE_DIRECTORY.resolve("dependencies.properties");

    private Path projectPath;

    @BeforeClass
    public void setup() throws Exception {
        projectPath = TestGradleProject.builder(BUILD_FILE)
                .addFile(DEPENDENCIES_FILE, Paths.get("dependencies.properties"))
                .build()
                .getProjectDirectory();
    }

    @Test
    public void applyConstraints() throws Exception {
        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(projectPath.toFile())
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
