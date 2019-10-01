/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.plugin;

import java.io.File;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.starchartlabs.flare.plugins.model.CredentialSet;
import org.starchartlabs.flare.plugins.model.DependencyConstraints;
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
            p.getPluginManager().apply("org.starchartlabs.flare.dependency-constraints");
            p.getPluginManager().apply("org.starchartlabs.flare.managed-credentials");
            p.getPluginManager().apply("org.starchartlabs.flare.increased-test-logging");
            p.getPluginManager().apply("org.starchartlabs.flare.source-jars");
            p.getPluginManager().apply("org.starchartlabs.flare.metadata-base");
            p.getPluginManager().apply("org.starchartlabs.flare.metadata-pom");

            DependencyConstraints dependencyConstraints = p.getExtensions().getByType(DependencyConstraints.class);
            ProjectMetaData projectMetaData = p.getExtensions().getByType(ProjectMetaData.class);

            File dependenciesFile = project.file(project.getProjectDir().toPath().resolve("dependencies.properties"));
            File developersFile = project.file(project.getProjectDir().toPath().resolve("developers.properties"));
            File contributorsFile = project.file(project.getProjectDir().toPath().resolve("contributors.properties"));

            if (dependenciesFile.exists()) {
                dependencyConstraints.file(dependenciesFile);
            }

            if (developersFile.exists()) {
                projectMetaData.github(gh -> gh.developers(developersFile));
            }

            if (contributorsFile.exists()) {
                projectMetaData.github(gh -> gh.contributors(contributorsFile));
            }

            @SuppressWarnings("unchecked")
            NamedDomainObjectContainer<CredentialSet> credentials = (NamedDomainObjectContainer<CredentialSet>) p
            .getExtensions().getByName("credentials");

            // Setup reading BinTray credentials from the environment, with defaults of blank to allow non-publishing
            // tasks to be run in environments where the environment variables are not set
            credentials.add(new CredentialSet("bintray")
                    .environment("BINTRAY_USER", "BINTRAY_API_KEY")
                    .defaultCredentials("", ""));

        });
    }

}
