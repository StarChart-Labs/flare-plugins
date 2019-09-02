/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.model;

import java.util.Objects;

import javax.annotation.Nullable;

import org.gradle.api.Action;
import org.gradle.api.publish.maven.MavenPomContributor;
import org.starchartlabs.alloy.core.MoreObjects;

import groovy.lang.Closure;

/**
 * Represents an individual who does not hold responsibilities for maintaining a project, but has contributed work
 * towards it
 *
 * <p>
 * Based of Maven POM definition of a <a href="https://maven.apache.org/pom.html#Contributors">contributor</a>
 *
 * @author romeara
 * @since 0.2.0
 */
public class Contributor {

    private String name;

    private String url;

    /**
     * Initializes an empty contributor record
     *
     * @since 0.2.0
     */
    public Contributor() {
        this(null, null);
    }

    /**
     * @param name
     *            Name of the contributor
     * @param url
     *            URL where information about the contributor is available
     * @since 0.2.0
     */
    public Contributor(@Nullable String name, @Nullable String url) {
        this.name = name;
        this.url = url;
    }

    /**
     * Allows external configuration of the contributor. Mainly used in Gradle DSLs (Groovy) for in-line configuration
     *
     * @param closure
     *            Closure to configure the contributor
     * @return This (configured) contributor
     * @since 0.2.0
     */
    public Contributor configure(Closure<Contributor> closure) {
        Objects.requireNonNull(closure);

        closure.setDelegate(this);
        closure.call();

        return this;
    }

    /**
     * Allows external configuration of the contributor. Mainly used in Gradle DSLs (Groovy) for in-line configuration
     *
     * @param action
     *            Action to configure the contributor
     * @return This (configured) contributor
     * @since 0.2.0
     */
    public Contributor configure(Action<Contributor> action) {
        Objects.requireNonNull(action);

        action.execute(this);

        return this;
    }

    /**
     * @return Name of the contributor
     * @since 0.2.0
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            Name of the contributor
     * @since 0.2.0
     */
    public void setName(@Nullable String name) {
        this.name = name;
    }

    /**
     * @return URL where information about the contributor is available
     * @since 0.2.0
     */
    @Nullable
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            URL where information about the contributor is available
     * @since 0.2.0
     */
    public void setUrl(@Nullable String url) {
        this.url = url;
    }

    /**
     * @return An action which configures meta-data values on a generated Maven POM's "contributor" properties
     * @since 0.2.0
     */
    public Action<MavenPomContributor> getPomConfiguration() {
        return (pomContributor -> {
            pomContributor.getName().set(getName());
            pomContributor.getUrl().set(getUrl());
        });
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(),
                getUrl());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof Contributor) {
            Contributor compare = (Contributor) obj;

            result = Objects.equals(compare.getName(), getName())
                    && Objects.equals(compare.getUrl(), getUrl());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("name", getName())
                .add("url", getUrl())
                .toString();
    }

}
