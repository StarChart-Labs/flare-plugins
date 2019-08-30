/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.starchartlabs.flare.plugins.model.ProjectMetaData;

/**
 * Convention plug-in that takes structures for defining project meta data from the meta-data base plug-in and applies
 * them to any POMs generated in relation to MavenPublication instances provided by the maven-publish plug-in
 *
 * @author romeara
 * @since 0.2.0
 */
public class MetaDataPomPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("org.starchartlabs.flare.metadata-base");

        ProjectMetaData projectMetaData = project.getExtensions().getByType(ProjectMetaData.class);

        // Another option was applying configuration to the GenerateMavenPom tasks in a doFirst block, but that removes
        // any ability to accurately output or alter the POM fields after this configuration is applied

        // After evaluate to all setting values in meta-data before applying them
        project.afterEvaluate(p -> {
            project.getPluginManager().withPlugin("maven-publish", mavenPublishPlugin -> {
                PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);

                publishing.getPublications().withType(MavenPublication.class).all(publication -> {
                    publication.pom(projectMetaData.getPomConfiguration());
                });
            });
        });
    }

}
