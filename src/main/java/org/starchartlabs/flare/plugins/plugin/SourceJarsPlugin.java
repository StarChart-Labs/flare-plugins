/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.jvm.tasks.Jar;

/**
 * Plug-in which adds Jar tasks which contain the source code and JavaDoc of the project
 *
 * <p>
 * This is desirable for open-source projects, allowing upload of these archives alongside binary artifacts, making it
 * easier for developers to use the associated libraries
 *
 * @author romeara
 * @since 0.2.0
 */
public class SourceJarsPlugin implements Plugin<Project> {

    private static final String SOURCE_JAR_TASK_NAME = "sourcesJar";

    private static final String JAVADOC_JAR_TASK_NAME = "javadocJar";

    @Override
    public void apply(Project project) {
        project.getPluginManager().withPlugin("java", javaPlugin -> {
            Jar sourcesJarTask = project.getTasks().create(SOURCE_JAR_TASK_NAME, Jar.class);
            Jar javadocJarTask = project.getTasks().create(JAVADOC_JAR_TASK_NAME, Jar.class);

            configureSourcesJarTask(project, sourcesJarTask);
            configureJavadocJarTask(project, javadocJarTask);

            project.getArtifacts().add("archives", sourcesJarTask);
            project.getArtifacts().add("archives", javadocJarTask);

            project.getPluginManager().withPlugin("maven-publish", mavenPublishPlugin -> {
                configureMavenPublish(project, sourcesJarTask, javadocJarTask);
            });
        });
    }

    private void configureSourcesJarTask(Project project, Jar sourcesJarTask) {
        sourcesJarTask.setGroup("Build");
        sourcesJarTask.setDescription("Creates a jar containing the source code of the project");
        sourcesJarTask.setClassifier("sources");
        sourcesJarTask.dependsOn(project.getTasks().getByName("classes"));

        JavaPluginConvention javaPluginConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        SourceSetContainer sourceSets = javaPluginConvention.getSourceSets();
        SourceSet mainSourceSet = sourceSets.findByName("main");

        sourcesJarTask.from(mainSourceSet.getAllSource());
    }

    private void configureJavadocJarTask(Project project, Jar javadocJarTask) {
        Javadoc javadocTask = project.getTasks().withType(Javadoc.class).findByName("javadoc");

        javadocJarTask.setGroup("Build");
        javadocJarTask.setDescription("Creates a jar containing the javadoc of the project");
        javadocJarTask.setClassifier("javadoc");
        javadocJarTask.dependsOn(javadocTask);

        javadocJarTask.from(javadocTask.getDestinationDir());
    }

    private void configureMavenPublish(Project project, Task sourcesJarTask, Task javadocJarTask) {
        // Find all MavenPublications, and add a correction to compile dependencies to be of compile scope
        PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);

        publishing.getPublications()
        .withType(MavenPublication.class)
        .all(publication -> configureMavenPublication(publication, sourcesJarTask, javadocJarTask));
    }

    private void configureMavenPublication(MavenPublication publication, Task sourcesJarTask, Task javadocJarTask) {
        publication.artifact(sourcesJarTask, ma -> ma.setClassifier("sources"));
        publication.artifact(javadocJarTask, ma -> ma.setClassifier("javadoc"));
    }

}
