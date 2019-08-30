/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nullable;

import org.gradle.api.Action;
import org.gradle.api.publish.maven.MavenPomDeveloperSpec;
import org.starchartlabs.alloy.core.MoreObjects;

import groovy.lang.Closure;

/**
 * Represents management and operations for the developers associated with a repository
 *
 * @author romeara
 * @since 0.2.0
 */
public class DeveloperContainer {

    private Collection<Developer> developers;

    /**
     * Initializes an empty developer management container
     *
     * @since 0.2.0
     */
    public DeveloperContainer() {
        this.developers = new ArrayList<>();
    }

    /**
     * Configures this container via the specified closure
     *
     * @param closure
     *            An closure used to configure the container
     * @return This container instance, with the provided configuration applied
     * @since 0.2.0
     */
    public DeveloperContainer configure(Closure<DeveloperContainer> closure) {
        Objects.requireNonNull(closure);

        closure.setDelegate(this);
        closure.call();

        return this;
    }

    /**
     * Configures this container via the specified action
     *
     * @param action
     *            An action used to configure the container
     * @return This container instance, with the provided configuration applied
     * @since 0.2.0
     */
    public DeveloperContainer configure(Action<DeveloperContainer> action) {
        Objects.requireNonNull(action);

        action.execute(this);

        return this;
    }

    /**
     * @return The developers currently managed by the container
     * @since 0.2.0
     */
    public Collection<Developer> getDevelopers() {
        return developers;
    }

    /**
     * Overwrites the current developers within this container with the provided collection
     *
     * @param developers
     *            A collection of developers to set this container to
     * @since 0.2.0
     */
    public void setDevelopers(Collection<Developer> developers) {
        Objects.requireNonNull(developers);

        this.developers = developers;
    }

    /**
     * Configures a new developer to add to the set of developers
     *
     * @param id
     *            Unique identifier of the developer within the organization
     * @param name
     *            Name of the developer
     * @param url
     *            URL where information about the developer is available
     * @return This developer container, with the specified developer added
     * @since 0.2.0
     */
    public DeveloperContainer developer(String id, String name, String url) {
        developers.add(new Developer(id, name, url));

        return this;
    }

    /**
     * Configures a new developer to add to the set of developers
     *
     * @param closure
     *            A closure used to configure a single developer
     * @return This developer container, with the specified developer added
     * @since 0.2.0
     */
    public DeveloperContainer developer(Closure<Developer> closure) {
        Objects.requireNonNull(closure);

        developers.add(new Developer().configure(closure));

        return this;
    }

    /**
     * Configures a new developer to add to the set of developers
     *
     * @param action
     *            An action used to configure a single developer
     * @return This developer container, with the specified developer added
     * @since 0.2.0
     */
    public DeveloperContainer developer(Action<Developer> action) {
        Objects.requireNonNull(action);

        developers.add(new Developer().configure(action));

        return this;
    }

    // TODO romeara
    public Action<MavenPomDeveloperSpec> getPomConfiguation() {
        return (pomDevelopers -> {
            getDevelopers().stream()
                    .map(Developer::getPomConfiguration)
                    .forEach(pomDevelopers::developer);
        });
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDevelopers());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof DeveloperContainer) {
            DeveloperContainer compare = (DeveloperContainer) obj;

            result = Objects.equals(compare.getDevelopers(), getDevelopers());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("developers", getDevelopers())
                .toString();
    }

}
