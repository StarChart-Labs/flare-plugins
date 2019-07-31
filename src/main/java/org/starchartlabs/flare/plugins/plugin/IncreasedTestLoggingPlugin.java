/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.logging.TestExceptionFormat;
import org.gradle.api.tasks.testing.logging.TestLogEvent;

/**
 * Plug-in which applies a convention of increased logging during test runs
 *
 * @author romeara
 * @since 0.1.0
 */
public class IncreasedTestLoggingPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPluginManager().withPlugin("java", plugin -> {
            TaskCollection<Test> testTasks = project.getTasks().withType(Test.class);

            testTasks.forEach(it -> {
                it.getTestLogging().setExceptionFormat(TestExceptionFormat.FULL);

                it.getTestLogging().getQuiet().events(TestLogEvent.FAILED,
                        TestLogEvent.SKIPPED);

                it.getTestLogging().getInfo().events(TestLogEvent.FAILED,
                        TestLogEvent.SKIPPED,
                        TestLogEvent.PASSED,
                        TestLogEvent.STANDARD_OUT,
                        TestLogEvent.STANDARD_ERROR);

                it.getTestLogging().getDebug().events(TestLogEvent.FAILED,
                        TestLogEvent.SKIPPED,
                        TestLogEvent.PASSED,
                        TestLogEvent.STANDARD_OUT,
                        TestLogEvent.STANDARD_ERROR,
                        TestLogEvent.STARTED);
            });
        });
    }

}
