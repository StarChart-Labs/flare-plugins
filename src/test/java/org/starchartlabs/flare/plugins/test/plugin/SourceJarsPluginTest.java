/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.plugin;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.jvm.tasks.Jar;
import org.gradle.testfixtures.ProjectBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SourceJarsPluginTest {

    private static final String PLUGIN_ID = "org.starchartlabs.flare.source-jars";

    @Test
    public void singleProjectWithoutJava() throws Exception {
        Project project = ProjectBuilder.builder()
                .withName("noJava")
                .build();
        project.getPluginManager().apply(PLUGIN_ID);

        Task sourcesJarTask = project.getTasks().findByName("sourcesJar");
        Task javadocJarTask = project.getTasks().findByName("javadocJar");

        Assert.assertNull(sourcesJarTask);
        Assert.assertNull(javadocJarTask);
    }

    @Test
    public void singleProjectJava() throws Exception {
        Project project = ProjectBuilder.builder()
                .withName("withJava")
                .build();
        project.getPluginManager().apply(PLUGIN_ID);
        project.getPluginManager().apply("java");

        Task sourcesJarTask = project.getTasks().findByName("sourcesJar");
        Task javadocJarTask = project.getTasks().findByName("javadocJar");

        assertSourcesJarTask(project, sourcesJarTask);
        assertJavadocJarTask(project, javadocJarTask);
    }

    @Test
    public void singleProjectJavaLibrary() throws Exception {
        Project project = ProjectBuilder.builder()
                .withName("withJavaLibrary")
                .build();
        project.getPluginManager().apply("java-library");
        project.getPluginManager().apply(PLUGIN_ID);

        Task sourcesJarTask = project.getTasks().findByName("sourcesJar");
        Task javadocJarTask = project.getTasks().findByName("javadocJar");

        assertSourcesJarTask(project, sourcesJarTask);
        assertJavadocJarTask(project, javadocJarTask);
    }

    private void assertSourcesJarTask(Project project, Task task) {
        Assert.assertNotNull(task);
        Assert.assertTrue(task instanceof Jar);
        Assert.assertEquals(task.getGroup(), "Build");
        Assert.assertEquals(task.getDescription(),
                "Creates a jar containing the source code of the project");

        Jar jar = (Jar) task;

        Assert.assertEquals(jar.getClassifier(), "sources");

        Assert.assertTrue(task.getDependsOn().contains(project.getTasks().getByName("classes")));
    }

    private void assertJavadocJarTask(Project project, Task task) {
        Assert.assertNotNull(task);
        Assert.assertTrue(task instanceof Jar);
        Assert.assertEquals(task.getGroup(), "Build");
        Assert.assertEquals(task.getDescription(),
                "Creates a jar containing the javadoc of the project");

        Jar jar = (Jar) task;

        Assert.assertEquals(jar.getClassifier(), "javadoc");

        Assert.assertTrue(task.getDependsOn().contains(project.getTasks().getByName("javadoc")));
    }
}
