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
import org.starchartlabs.alloy.core.MoreObjects;

import groovy.lang.Closure;

/**
 * Represents management and operations for the contributors associated with a repository
 *
 * @author romeara
 * @since 0.2.0
 */
public class ContributorContainer {

    private Collection<Contributor> contributors;

    /**
     * Initializes an empty contributor management container
     *
     * @since 0.2.0
     */
    public ContributorContainer() {
        this.contributors = new ArrayList<>();
    }

    /**
     * Configures this container via the specified closure
     *
     * @param closure
     *            An closure used to configure the container
     * @return This container instance, with the provided configuration applied
     * @since 0.2.0
     */
    public ContributorContainer configure(Closure<ContributorContainer> closure) {
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
    public ContributorContainer configure(Action<ContributorContainer> action) {
        Objects.requireNonNull(action);

        action.execute(this);

        return this;
    }

    /**
     * @return The contributors currently managed by the container
     * @since 0.2.0
     */
    public Collection<Contributor> getContributors() {
        return contributors;
    }

    /**
     * Overwrites the current contributors within this container with the provided collection
     *
     * @param contributors
     *            A collection of contributors to set this container to
     * @since 0.2.0
     */
    public void setContributors(Collection<Contributor> contributors) {
        Objects.requireNonNull(contributors);

        this.contributors = contributors;
    }

    /**
     * Configures a new contributor to add to the set of contributors
     *
     * @param name
     *            Name of the contributor
     * @param url
     *            URL where information about the contributor is available
     * @return This contributor container, with the specified contributor added
     * @since 0.2.0
     */
    public ContributorContainer contributor(String name, String url) {
        contributors.add(new Contributor(name, url));

        return this;
    }

    /**
     * Configures a new contributor to add to the set of contributors
     *
     * @param closure
     *            A closure used to configure a single contributor
     * @return This contributor container, with the specified contributor added
     * @since 0.2.0
     */
    public ContributorContainer contributor(Closure<Contributor> closure) {
        Objects.requireNonNull(closure);

        contributors.add(new Contributor().configure(closure));

        return this;
    }

    /**
     * Configures a new contributor to add to the set of contributors
     *
     * @param action
     *            An action used to configure a single contributor
     * @return This contributor container, with the specified contributor added
     * @since 0.2.0
     */
    public ContributorContainer contributor(Action<Contributor> action) {
        Objects.requireNonNull(action);

        contributors.add(new Contributor().configure(action));

        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContributors());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof ContributorContainer) {
            ContributorContainer compare = (ContributorContainer) obj;

            result = Objects.equals(compare.getContributors(), getContributors());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("contributors", getContributors())
                .toString();
    }

}
