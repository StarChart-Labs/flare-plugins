/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.DependencyConstraint;
import org.gradle.api.internal.artifacts.dependencies.DependencyConstraintInternal;
import org.gradle.internal.impldep.aQute.lib.strings.Strings;
import org.starchartlabs.alloy.core.Preconditions;

/**
 * Represents domain-specific language applied to Gradle build files
 *
 * <p>
 * This DSL allows reading dependency constraints from an external file and application of them to all configurations
 * within a project
 *
 * @author romeara
 * @since 0.1.0
 */
public class DependencyConstraints {

    private static final Map<Path, ConstraintFile> loadedFileCache = new HashMap<>();

    private final Project project;

    /**
     * @param project
     *            The project the DSL is being added to
     * @since 0.1.0
     */
    public DependencyConstraints(Project project) {
        this.project = Objects.requireNonNull(project);
    }

    /**
     * Applies dependency constraints to all configurations from an external file
     *
     * <p>
     * Files are expected to specific a single GAV (group:artifact:version) per line
     *
     * @param file
     *            External files to read constraints from
     * @return This constraints object
     * @since 0.1.0
     */
    public DependencyConstraints file(File file) {
        Objects.requireNonNull(file);
        Preconditions.checkArgument(file.exists(),
                () -> Strings.format("Constraint file at %s does not exist", file.toPath().toString()));

        try {
            Set<String> constraints = getArtifactConstraints(file);

            project.getConfigurations().all(configuration -> {
                constraints.forEach(gav -> {
                    project.getDependencies().getConstraints().add(configuration.getName(), gav,
                            this::configureConstraint);

                    project.getLogger().info("Applied {} dependency constraint: {}", configuration, gav);
                });
            });
        } catch (IOException e) {
            throw new GradleException("Error loading dependency properties", e);
        }

        return this;
    }

    /**
     * Applies post-creation configuration to a generated dependency constraint
     *
     * <p>
     * This includes forcing the constraint on transitive dependencies, if possible via the supplied implementation
     *
     * @param constraint
     *            The constraint to configure
     */
    private void configureConstraint(DependencyConstraint constraint) {
        // This is because the force flag is not part of public API - an issue has been filed at
        // https://github.com/gradle/gradle/issues/9934 for Gradle to consider changing this
        if (constraint instanceof DependencyConstraintInternal) {
            ((DependencyConstraintInternal) constraint).setForce(true);

            project.getLogger().debug("Forced constraint {}", constraint.getName());
        } else {
            project.getLogger().debug("Unable to force constraint {}", constraint.getName());
        }
    }

    /**
     * Loads constraints from the provided file, using previously read and cached values if available
     *
     * @param file
     *            The file to read values from
     * @return The dependency constraints to apply
     * @throws IOException
     *             If there is an error loading the file
     */
    private synchronized Set<String> getArtifactConstraints(File file) throws IOException {
        Objects.requireNonNull(file);

        Path key = file.toPath();

        if (!loadedFileCache.containsKey(key)) {
            loadedFileCache.put(key, new ConstraintFile(key));
        }

        return loadedFileCache.get(key).getDependencyNotations();
    }

}
