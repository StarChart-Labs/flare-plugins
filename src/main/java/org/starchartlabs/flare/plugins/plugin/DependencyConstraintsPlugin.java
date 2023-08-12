/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.starchartlabs.flare.plugins.model.DependencyConstraints;

/**
 * Configuration plug-in that adds structures for loading dependency version constraints and applying them to all
 * configurations
 *
 * @author romeara
 * @since 0.1.0
 */
public class DependencyConstraintsPlugin implements Plugin<Project> {

    private static final String CONSTRAINTS_DSL_EXTENSION = "dependencyConstraints";

    @Override
    public void apply(Project project) {
        project.getExtensions().add(CONSTRAINTS_DSL_EXTENSION, new DependencyConstraints(project));

        project.getLogger()
                .warn("Flare dependency management plug-in is deprecated - use Gradle platform constraints instead");
    }

}
