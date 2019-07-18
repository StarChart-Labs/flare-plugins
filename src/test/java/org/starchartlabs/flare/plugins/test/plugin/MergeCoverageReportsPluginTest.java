/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.plugin;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testing.jacoco.tasks.JacocoReport;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MergeCoverageReportsPluginTest {

    private static final String PLUGIN_ID = "org.starchartlabs.flare.merge-coverage-reports";

    @Test
    public void singleProjectWithoutJacoco() throws Exception {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(PLUGIN_ID);

        Task task = project.getTasks().findByName("mergeCoverageReports");

        assertMergeTask(project, task, false);
    }

    @Test
    public void singleProjectJacoco() throws Exception {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(PLUGIN_ID);
        project.getPluginManager().apply("jacoco");

        Task task = project.getTasks().findByName("mergeCoverageReports");

        assertMergeTask(project, task, false);
    }

    @Test
    public void singleProjectJacocoAndJava() throws Exception {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(PLUGIN_ID);
        project.getPluginManager().apply("jacoco");
        project.getPluginManager().apply("java");

        Task task = project.getTasks().findByName("mergeCoverageReports");
        Task buildTask = project.getTasks().getByName("check");

        assertMergeTask(project, task, true);

        Assert.assertTrue(buildTask.getDependsOn().contains(task));
    }

    @Test
    public void singleProjectJacocoAndJavaLibrary() throws Exception {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(PLUGIN_ID);
        project.getPluginManager().apply("jacoco");
        project.getPluginManager().apply("java-library");

        Task task = project.getTasks().findByName("mergeCoverageReports");
        Task buildTask = project.getTasks().getByName("check");

        assertMergeTask(project, task, true);

        Assert.assertTrue(buildTask.getDependsOn().contains(task));
    }

    @Test
    public void multiModuleProjectWithoutJacoco() throws Exception {
        Project rootProject = ProjectBuilder.builder().build();
        rootProject.getPluginManager().apply(PLUGIN_ID);

        ProjectBuilder.builder()
        .withName("subProject1")
        .withParent(rootProject)
        .build();
        ProjectBuilder.builder()
        .withName("subProject2")
        .withParent(rootProject)
        .build();

        Task task = rootProject.getTasks().findByName("mergeCoverageReports");

        assertMergeTask(rootProject, task, false);
    }

    private void assertMergeTask(Project rootProject, Task task, boolean javaApplied) {
        Assert.assertNotNull(task);
        Assert.assertTrue(task instanceof JacocoReport);
        Assert.assertEquals(task.getGroup(), "Verification");
        Assert.assertEquals(task.getDescription(),
                "Merges Jacoco coverage reports from multiple modules into a single report");

        JacocoReport jacocoReport = (JacocoReport) task;

        Assert.assertNotNull(jacocoReport.getExecutionData());

        Assert.assertEquals(jacocoReport.getReports().getXml().getDestination(),
                rootProject.file(rootProject.getBuildDir().toString() + "/reports/jacoco/report.xml"));
        Assert.assertEquals(jacocoReport.getReports().getXml().isEnabled(), true);
        Assert.assertEquals(jacocoReport.getReports().getHtml().isEnabled(), false);
        Assert.assertEquals(jacocoReport.getReports().getCsv().isEnabled(), false);

        if (javaApplied) {
            Assert.assertFalse(jacocoReport.getSourceDirectories().isEmpty());
        }
    }
}
