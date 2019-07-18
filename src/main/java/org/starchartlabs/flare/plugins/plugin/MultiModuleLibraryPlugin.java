/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.plugin;

import java.io.IOException;

import org.gradle.api.GradleException;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.starchartlabs.flare.plugins.model.CredentialSet;
import org.starchartlabs.flare.plugins.model.DependencyConstraints;

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

            try {
                DependencyConstraints dependencyConstraints = p.getExtensions().getByType(DependencyConstraints.class);
                dependencyConstraints
                .file(project.file(project.getProjectDir().toPath().resolve("dependencies.properties")));
            } catch (IOException e) {
                throw new GradleException("Error loading dependency properties", e);
            }

            @SuppressWarnings("unchecked")
            NamedDomainObjectContainer<CredentialSet> credentials = (NamedDomainObjectContainer<CredentialSet>) p
            .getExtensions().getByName("credentials");
            credentials.add(new CredentialSet("bintray").environment("BINTRAY_USER", "BINTRAY_API_KEY"));
        });
    }

}