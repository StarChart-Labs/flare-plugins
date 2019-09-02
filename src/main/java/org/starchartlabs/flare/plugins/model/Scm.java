/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.model;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import org.gradle.api.Action;
import org.gradle.api.publish.maven.MavenPomScm;
import org.starchartlabs.alloy.core.MoreObjects;

import groovy.lang.Closure;

/**
 * Represents source control information for a repository
 *
 * <p>
 * Based of Maven POM definition of a <a href="https://maven.apache.org/pom.html#SCM">SCM</a>
 *
 * @author romeara
 * @since 0.2.0
 */
public class Scm {

    private String vcsUrl;

    private String connection;

    private String developerConnection;

    /**
     * Initializes an empty SCM record
     *
     * @since 0.2.0
     */
    public Scm() {
        this(null, null, null);
    }

    /**
     * @param vcsUrl
     *            URL for the version control system
     * @param connection
     *            Version control connection usable by contributors/consumers to obtain source code
     * @param developerConnection
     *            Version control connection usable by developers to obtain source code
     * @since 0.2.0
     */
    public Scm(String vcsUrl, String connection, String developerConnection) {
        this.vcsUrl = vcsUrl;
        this.connection = connection;
        this.developerConnection = developerConnection;
    }

    /**
     * Allows external configuration of the scm. Mainly used in Gradle DSLs (Groovy) for in-line configuration
     *
     * @param closure
     *            Closure to configure the scm
     * @return This (configured) scm
     * @since 0.2.0
     */
    public Scm configure(Closure<Scm> closure) {
        Objects.requireNonNull(closure);

        closure.setDelegate(this);
        closure.call();

        return this;
    }

    /**
     * Allows external configuration of the scm. Mainly used in Gradle DSLs (Groovy) for in-line configuration
     *
     * @param action
     *            Action to configure the scm
     * @return This (configured) scm
     * @since 0.2.0
     */
    public Scm configure(Action<Scm> action) {
        Objects.requireNonNull(action);

        action.execute(this);

        return this;
    }

    /**
     * @return URL for the version control system
     * @since 0.2.0
     */
    @Nullable
    public String getVcsUrl() {
        return vcsUrl;
    }

    /**
     * @param vcsUrl
     *            URL for the version control system
     * @since 0.2.0
     */
    public void setVcsUrl(@Nullable String vcsUrl) {
        this.vcsUrl = vcsUrl;
    }

    /**
     * @return Version control connection usable by contributors/consumers to obtain source code
     * @since 0.2.0
     */
    @Nullable
    public String getConnection() {
        return connection;
    }

    /**
     * @param connection
     *            Version control connection usable by contributors/consumers to obtain source code
     * @since 0.2.0
     */
    public void setConnection(@Nullable String connection) {
        this.connection = connection;
    }

    /**
     * @return Version control connection usable by developers to obtain source code
     * @since 0.2.0
     */
    @Nullable
    public String getDeveloperConnection() {
        return developerConnection;
    }

    /**
     * @param developerConnection
     *            Version control connection usable by developers to obtain source code
     * @since 0.2.0
     */
    public void setDeveloperConnection(@Nullable String developerConnection) {
        this.developerConnection = developerConnection;
    }

    /**
     * @return An action which configures meta-data values on a generated Maven POM's "scm" properties
     * @since 0.2.0
     */
    public Action<MavenPomScm> getPomConfiguration() {
        return (pomScm -> {
            Optional.ofNullable(getVcsUrl())
            .ifPresent(url -> pomScm.getUrl().set(url));

            Optional.ofNullable(getConnection())
            .ifPresent(connection -> pomScm.getConnection().set(connection));

            Optional.ofNullable(getDeveloperConnection())
            .ifPresent(developerConnection -> pomScm.getDeveloperConnection().set(developerConnection));
        });
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVcsUrl(),
                getConnection(),
                getDeveloperConnection());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof Scm) {
            Scm compare = (Scm) obj;

            result = Objects.equals(compare.getVcsUrl(), getVcsUrl())
                    && Objects.equals(compare.getConnection(), getConnection())
                    && Objects.equals(compare.getDeveloperConnection(), getDeveloperConnection());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("vcsUrl", getVcsUrl())
                .add("connection", getConnection())
                .add("developerConnection", getDeveloperConnection())
                .toString();
    }

}
