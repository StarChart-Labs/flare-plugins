/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;
import org.starchartlabs.alloy.core.Preconditions;

/**
 * Represents a single line with a dependency constraint specification line
 *
 * <p>
 * Lines consist of a constraint to apply (group:artifact:version), and optionally one or more Gradle configuration
 * names to limit the constraint to. If no configurations are specified, the constraint applies to all configurations
 *
 * @author romeara
 * @since 0.2.0
 */
public class Constraint {

    private final String gav;

    private final Set<String> configurations;

    /**
     * @param line
     *            Single line from a constraint file to convert to a specification
     * @since 0.2.0
     */
    public Constraint(String line) {
        Objects.requireNonNull(line);

        List<String> elements = Arrays.asList(line.split(","));

        Preconditions.checkArgument(!elements.isEmpty(), "Empty constraint line provided");

        this.gav = elements.get(0);
        this.configurations = elements.stream()
                .skip(1)
                .collect(Collectors.toSet());
    }

    /**
     * @return The group/artifact/version constraint represented in the line
     * @since 0.2.0
     */
    public String getGav() {
        return gav;
    }

    /**
     * @return The specific configurations to apply the constraint to. Empty if the constraint should be applied to all
     *         configurations
     * @since 0.2.0
     */
    public Set<String> getConfigurations() {
        return configurations;
    }

    /**
     * @param configuration
     *            The name of a Gradle configuration
     * @return True if the constraint represented applies to the provided configuration, false otherwise
     * @since 0.2.0
     */
    public boolean isConfigurationApplicable(String configuration) {
        Objects.requireNonNull(configuration);

        return (configurations.isEmpty() || configurations.contains(configuration));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGav(),
                getConfigurations());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof Constraint) {
            Constraint compare = (Constraint) obj;

            result = Objects.equals(compare.getGav(), getGav())
                    && Objects.equals(compare.getConfigurations(), getConfigurations());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("gav", getGav())
                .add("configurations", getConfigurations())
                .toString();
    }

}
