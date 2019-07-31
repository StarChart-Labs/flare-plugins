/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.plugin;

import java.io.File;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.testing.jacoco.tasks.JacocoReport;

/**
 * Plug-in that adds a custom task and dependencies for merging multiple coverage reports into a single XML report
 *
 * @author romeara
 * @since 0.1.0
 */
public class MergeCoverageReportsPlugin implements Plugin<Project> {

    private static final String TASK_NAME = "mergeCoverageReports";

    @Override
    public void apply(Project project) {
        setupMergeTask(project);
    }

    private void setupMergeTask(Project rootProject) {
        // base: In order for this to integrate with lifecycle tasks, base must be applied
        // jacoco: In order for the report task to operate, jacoco must be applied
        rootProject.getPluginManager().apply("base");
        rootProject.getPluginManager().apply("jacoco");

        // Create task - its available without the jacoco plug-in being applied
        JacocoReport mergeTask = rootProject.getTasks().create(TASK_NAME, JacocoReport.class);

        configureJacocoReportTask(mergeTask);

        configureProjects(rootProject, mergeTask);

        rootProject.getTasks().getByName("check").dependsOn(mergeTask);
    }

    private JacocoReport configureJacocoReportTask(JacocoReport task) {
        task.setGroup("Verification");
        task.setDescription("Merges Jacoco coverage reports from multiple modules into a single report");

        // Only run if at least one jacoco report exists
        task.onlyIf(a -> !task.getExecutionData().getFiles().isEmpty());

        task.executionData(
                task.getProject().fileTree(task.getProject().getRootDir().getAbsolutePath())
                .include("**/build/jacoco/*.exec"));

        task.reports(reports -> {
            File destination = task.getProject()
                    .file(task.getProject().getBuildDir().toString() + "/reports/jacoco/report.xml");

            reports.getXml().setEnabled(true);
            reports.getXml().setDestination(destination);
            reports.getHtml().setEnabled(false);
            reports.getCsv().setEnabled(false);
        });

        return task;
    }

    private void configureProjects(Project rootProject, JacocoReport task) {
        // Apply to root project for single-module cases, and sub-projects, for multi-module cases
        rootProject.getAllprojects().forEach(project -> {
            project.getPluginManager().withPlugin("java", javaPlugin -> {
                configureJava(project, task);
            });
        });
    }

    private void configureJava(Project project, JacocoReport task) {
        // Merge coverage depends on output from all tests
        Task test = project.getTasks().getByName("test");
        if (test != null) {
            task.dependsOn(test);
        }

        JavaPluginConvention javaPluginConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        SourceSetContainer sourceSets = javaPluginConvention.getSourceSets();
        SourceSet mainSourceSet = sourceSets.findByName("main");

        task.sourceSets(mainSourceSet);
    }

}
