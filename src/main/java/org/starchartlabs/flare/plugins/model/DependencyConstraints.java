/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
            ConstraintFile constraints = getArtifactConstraints(file);

            project.getConfigurations().all(configuration -> {
                try {
                    constraints.getDependencyNotations(configuration.getName()).forEach(gav -> {
                        project.getDependencies().getConstraints().add(configuration.getName(), gav,
                                this::configureConstraint);

                        project.getLogger().info("Applied {} dependency constraint: {}", configuration, gav);
                    });
                } catch (IOException e) {
                    throw new GradleException("Error loading dependency entries", e);
                }
            });
        } catch (IOException e) {
            throw new GradleException("Error reading dependency file attributes", e);
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
     *             If there is an error reading the file's attributes
     */
    private synchronized ConstraintFile getArtifactConstraints(File file) throws IOException {
        Objects.requireNonNull(file);

        Path key = generateCacheKey(file);

        synchronized (loadedFileCache) {
            if (!loadedFileCache.containsKey(key)) {
                project.getLogger().info("Loaded dependency constraints file {}", file.toPath());

                loadedFileCache.put(key, new ConstraintFile(file.toPath()));
            }
        }

        return loadedFileCache.get(key);
    }

    /**
     * Generates a consistent cache key for the contents of a loaded dependency file
     *
     * @param file
     *            The file to make a deterministic cache key for
     * @return A deterministic cache key which is consistent for the file's location and contents
     * @throws IOException
     *             If there is an error reading the file's attributes
     */
    private Path generateCacheKey(File file) throws IOException {
        Objects.requireNonNull(file);

        Path filePath = file.toPath();

        // Use the file path AND access time as the key, so if a different version is checked out, or a modification
        // made locally, stale cache values will not be used
        // This is a concern between builds because, as documented in GH-23, Gradle keeps class loaders (and therefore
        // static variable contents) between runs within a given Daemon
        BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
        FileTime modifiedTime = attrs.lastModifiedTime();

        return filePath.resolve(Long.toString(modifiedTime.toMillis()));
    }

}
