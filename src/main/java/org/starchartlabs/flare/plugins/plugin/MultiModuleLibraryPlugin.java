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
import org.starchartlabs.flare.plugins.model.ProjectMetaData;

/**
 * Plug-in that applies all Flare plug-ins applicable to StarChart Labs multi-module library configurations, and sets
 * standard conventions for their use in that context
 *
 * @author romeara
 * @since 0.1.0
 */
public class MultiModuleLibraryPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("org.starchartlabs.flare.merge-coverage-reports");

        project.allprojects(p -> {
            p.getPluginManager().apply("org.starchartlabs.flare.managed-credentials");
            p.getPluginManager().apply("org.starchartlabs.flare.increased-test-logging");
            p.getPluginManager().apply("org.starchartlabs.flare.source-jars");
            p.getPluginManager().apply("org.starchartlabs.flare.metadata-base");
            p.getPluginManager().apply("org.starchartlabs.flare.metadata-pom");

            ProjectMetaData projectMetaData = p.getExtensions().getByType(ProjectMetaData.class);

            File developersFile = project.file(project.getProjectDir().toPath().resolve("developers.properties"));
            File contributorsFile = project.file(project.getProjectDir().toPath().resolve("contributors.properties"));

            if (developersFile.exists()) {
                projectMetaData.github(gh -> gh.developers(developersFile));
            }

            if (contributorsFile.exists()) {
                projectMetaData.github(gh -> gh.contributors(contributorsFile));
            }

        });
    }

}
