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
import org.gradle.api.publish.maven.MavenPomDeveloper;
import org.starchartlabs.alloy.core.MoreObjects;

import groovy.lang.Closure;

/**
 * Represents an individual who holds responsibilities for maintaining a project
 *
 * <p>
 * Based of Maven POM definition of a <a href="https://maven.apache.org/pom.html#Developers">developer</a>
 *
 * @author romeara
 * @since 0.2.0
 */
public class Developer {

    private String id;

    private String name;

    private String url;

    /**
     * Initializes an empty developer record
     *
     * @since 0.2.0
     */
    public Developer() {
        this(null, null, null);
    }

    /**
     * @param id
     *            Unique identifier of the developer within the organization
     * @param name
     *            Name of the developer
     * @param url
     *            URL where information about the developer is available
     * @since 0.2.0
     */
    public Developer(@Nullable String id, @Nullable String name, @Nullable String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    /**
     * Allows external configuration of the developer. Mainly used in Gradle DSLs (Groovy) for in-line configuration
     *
     * @param closure
     *            Closure to configure the developer
     * @return This (configured) developer
     * @since 0.2.0
     */
    public Developer configure(Closure<Developer> closure) {
        Objects.requireNonNull(closure);

        closure.setDelegate(this);
        closure.call();

        return this;
    }

    /**
     * Allows external configuration of the developer. Mainly used in Gradle DSLs (Groovy) for in-line configuration
     *
     * @param action
     *            Action to configure the developer
     * @return This (configured) developer
     * @since 0.2.0
     */
    public Developer configure(Action<Developer> action) {
        Objects.requireNonNull(action);

        action.execute(this);

        return this;
    }

    /**
     * @return Unique identifier of the developer within the organization
     * @since 0.2.0
     */
    @Nullable
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            Unique identifier of the developer within the organization
     * @since 0.2.0
     */
    public void setId(@Nullable String id) {
        this.id = id;
    }

    /**
     * @return Name of the developer
     * @since 0.2.0
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            Name of the developer
     * @since 0.2.0
     */
    public void setName(@Nullable String name) {
        this.name = name;
    }

    /**
     * @return URL where information about the developer is available
     * @since 0.2.0
     */
    @Nullable
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            URL where information about the developer is available
     * @since 0.2.0
     */
    public void setUrl(@Nullable String url) {
        this.url = url;
    }

    // TODO romeara
    public Action<MavenPomDeveloper> getPomConfiguration() {
        return (pomDeveloper -> {
            pomDeveloper.getId().set(getId());
            pomDeveloper.getName().set(getName());
            pomDeveloper.getUrl().set(getUrl());
        });
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                getName(),
                getUrl());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof Developer) {
            Developer compare = (Developer) obj;

            result = Objects.equals(compare.getId(), getId())
                    && Objects.equals(compare.getName(), getName())
                    && Objects.equals(compare.getUrl(), getUrl());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("id", getId())
                .add("name", getName())
                .add("url", getUrl())
                .toString();
    }

}
