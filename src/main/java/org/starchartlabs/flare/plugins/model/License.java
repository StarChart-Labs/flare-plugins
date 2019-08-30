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
import org.gradle.api.publish.maven.MavenPomLicense;
import org.starchartlabs.alloy.core.MoreObjects;

import groovy.lang.Closure;

/**
 * Represents a set of terms that govern legal use of the code within a project
 *
 * <p>
 * Based of Maven POM definition of a <a href="https://maven.apache.org/pom.html#Licenses">license</a>
 *
 * @author romeara
 * @since 0.2.0
 */
public class License {

    private String name;

    private String tag;

    private String url;

    private String distribution;

    /**
     * Initializes an empty license record
     *
     * @since 0.2.0
     */
    public License() {
        this(null, null, null, null);
    }

    /**
     * @param name
     *            Name of the license
     * @param tag
     *            Shorthand used to reference the license. (ex: "MIT" for The MIT License)
     * @param url
     *            The official URL for the license text
     * @param distribution
     *            The primary method by which this project may be distributed. "repo" for projects distributed via Maven
     *            Central, "manual" for manual download
     * @since 0.2.0
     */
    public License(String name, String tag, String url, String distribution) {
        this.name = name;
        this.tag = tag;
        this.url = url;
        this.distribution = distribution;
    }

    /**
     * Allows external configuration of the license. Mainly used in Gradle DSLs (Groovy) for in-line configuration
     *
     * @param closure
     *            Closure to configure the license
     * @return This (configured) license
     * @since 0.2.0
     */
    public License configure(Closure<License> closure) {
        Objects.requireNonNull(closure);

        closure.setDelegate(this);
        closure.call();

        return this;
    }

    /**
     * Allows external configuration of the license. Mainly used in Gradle DSLs (Groovy) for in-line configuration
     *
     * @param action
     *            Action to configure the license
     * @return This (configured) license
     * @since 0.2.0
     */
    public License configure(Action<License> action) {
        Objects.requireNonNull(action);

        action.execute(this);

        return this;
    }

    /**
     * @return Name of the license
     * @since 0.2.0
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            Name of the license
     * @since 0.2.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Shorthand used to reference the license. (ex: "MIT" for The MIT License)
     * @since 0.2.0
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag
     *            Shorthand used to reference the license. (ex: "MIT" for The MIT License)
     * @since 0.2.0
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * @return The official URL for the license text
     * @since 0.2.0
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            The official URL for the license text
     * @since 0.2.0
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The primary method by which this project may be distributed. "repo" for projects distributed via Maven
     *         Central, "manual" for manual download
     * @since 0.2.0
     */
    public String getDistribution() {
        return distribution;
    }

    /**
     * @param distribution
     *            The primary method by which this project may be distributed. "repo" for projects distributed via Maven
     *            Central, "manual" for manual download
     * @since 0.2.0
     */
    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    // TODO romeara
    public Action<MavenPomLicense> getPomConfiguration() {
        return (pomLicense -> {
            pomLicense.getName().set(getName());
            pomLicense.getUrl().set(getUrl());
            pomLicense.getDistribution().set(getDistribution());
        });
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(),
                getTag(),
                getUrl(),
                getDistribution());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof License) {
            License compare = (License) obj;

            result = Objects.equals(compare.getName(), getName())
                    && Objects.equals(compare.getTag(), getTag())
                    && Objects.equals(compare.getUrl(), getUrl())
                    && Objects.equals(compare.getDistribution(), getDistribution());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("name", getName())
                .add("tag", getTag())
                .add("url", getUrl())
                .add("distribution", getDistribution())
                .toString();
    }

}
