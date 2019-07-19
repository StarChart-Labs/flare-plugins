/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.plugin;

import org.gradle.api.Project;
import org.gradle.api.tasks.testing.logging.TestExceptionFormat;
import org.gradle.api.tasks.testing.logging.TestLogEvent;
import org.gradle.api.tasks.testing.logging.TestLogging;
import org.gradle.api.tasks.testing.logging.TestLoggingContainer;
import org.gradle.internal.impldep.org.testng.Assert;
import org.gradle.testfixtures.ProjectBuilder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class IncreasedTestLoggingPluginTest {

    private static final String PLUGIN_ID = "org.starchartlabs.flare.increased-test-logging";

    private Project project;

    @BeforeClass
    public void setupProject() {
        project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("java");
        project.getPluginManager().apply(PLUGIN_ID);
    }

    @Test
    public void expectionFormatApplied() throws Exception {
        org.gradle.api.tasks.testing.Test testTask = (org.gradle.api.tasks.testing.Test) project.getTasks()
                .getByName("test");

        TestLoggingContainer logging = testTask.getTestLogging();

        Assert.assertEquals(logging.getExceptionFormat(), TestExceptionFormat.FULL);
    }

    @Test
    public void quietLoggingApplied() throws Exception {
        org.gradle.api.tasks.testing.Test testTask = (org.gradle.api.tasks.testing.Test) project.getTasks()
                .getByName("test");

        TestLoggingContainer loggingContainer = testTask.getTestLogging();
        TestLogging logging = loggingContainer.getQuiet();

        Assert.assertEquals(logging.getEvents().size(), 2);
        Assert.assertTrue(logging.getEvents().contains(TestLogEvent.FAILED));
        Assert.assertTrue(logging.getEvents().contains(TestLogEvent.SKIPPED));
    }

    @Test
    public void infoLoggingApplied() throws Exception {
        org.gradle.api.tasks.testing.Test testTask = (org.gradle.api.tasks.testing.Test) project.getTasks()
                .getByName("test");

        TestLoggingContainer loggingContainer = testTask.getTestLogging();
        TestLogging logging = loggingContainer.getInfo();

        Assert.assertEquals(logging.getEvents().size(), 5);
        Assert.assertTrue(logging.getEvents().contains(TestLogEvent.FAILED));
        Assert.assertTrue(logging.getEvents().contains(TestLogEvent.SKIPPED));
        Assert.assertTrue(logging.getEvents().contains(TestLogEvent.PASSED));
        Assert.assertTrue(logging.getEvents().contains(TestLogEvent.STANDARD_OUT));
        Assert.assertTrue(logging.getEvents().contains(TestLogEvent.STANDARD_ERROR));
    }

    @Test
    public void debugLoggingApplied() throws Exception {
        org.gradle.api.tasks.testing.Test testTask = (org.gradle.api.tasks.testing.Test) project.getTasks()
                .getByName("test");

        TestLoggingContainer loggingContainer = testTask.getTestLogging();
        TestLogging logging = loggingContainer.getDebug();

        Assert.assertEquals(logging.getEvents().size(), 6);
        Assert.assertTrue(logging.getEvents().contains(TestLogEvent.FAILED));
        Assert.assertTrue(logging.getEvents().contains(TestLogEvent.SKIPPED));
        Assert.assertTrue(logging.getEvents().contains(TestLogEvent.PASSED));
        Assert.assertTrue(logging.getEvents().contains(TestLogEvent.STANDARD_OUT));
        Assert.assertTrue(logging.getEvents().contains(TestLogEvent.STANDARD_ERROR));
        Assert.assertTrue(logging.getEvents().contains(TestLogEvent.STARTED));
    }

}
