/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

/**
 * Represents a file which specifies version constraints for dependencies
 *
 * <p>
 * Processed files may have lines which are blank, start with '#' (comments), and which contain
 * 'group:artifact:version[,configs,...]' to apply constraints
 *
 * @author romeara
 * @since 0.1.0
 */
public class ConstraintFile {

    private final Path path;

    private Set<Constraint> loaded;

    /**
     * @param path
     *            Path to the file to process
     * @since 0.1.0
     */
    public ConstraintFile(Path path) {
        this.path = Objects.requireNonNull(path);
        this.loaded = null;
    }

    /**
     * Parses the represented file, providing the specified dependency constraints for a given configuration
     *
     * @param configuration
     *            The configuration to limit the returned dependency notations for
     * @return A set of the dependency GAV's to constrain for the selected configuration
     * @throws IOException
     *             If there is an error reading the file
     * @since 0.1.0
     */
    public Set<String> getDependencyNotations(String configuration) throws IOException {
        Objects.requireNonNull(configuration);

        if (loaded == null) {
            loaded = Files.lines(path)
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> !isComment(line))
                    .map(Constraint::new)
                    .collect(Collectors.toSet());
        }

        return loaded.stream()
                .filter(constraint -> constraint.isConfigurationApplicable(configuration))
                .map(Constraint::getGav)
                .collect(Collectors.toSet());
    }

    /**
     * @param line
     *            Line to analyze
     * @return True if the line provided is a comment line, false otherwise
     */
    private boolean isComment(String line) {
        return line.startsWith("#");
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof ConstraintFile) {
            ConstraintFile compare = (ConstraintFile) obj;

            result = Objects.equals(path, compare.path);
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("path", path)
                .toString();
    }

}
