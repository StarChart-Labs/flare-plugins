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
import org.gradle.api.publish.maven.MavenPomLicenseSpec;
import org.starchartlabs.alloy.core.MoreObjects;

import groovy.lang.Closure;

/**
 * Represents management and operations for the licenses associated with a repository
 *
 * @author romeara
 * @since 0.2.0
 */
public class LicenseContainer {

    private static final String REPOSITORY_DISTRIBUTION = "repo";

    private Collection<License> licenses;

    /**
     * Initializes an empty license management container
     *
     * @since 0.2.0
     */
    public LicenseContainer() {
        this.licenses = new ArrayList<>();
    }

    /**
     * Configures this container via the specified closure
     *
     * @param closure
     *            An closure used to configure the container
     * @return This container instance, with the provided configuration applied
     * @since 0.2.0
     */
    public LicenseContainer configure(Closure<LicenseContainer> closure) {
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
    public LicenseContainer configure(Action<LicenseContainer> action) {
        Objects.requireNonNull(action);

        action.execute(this);

        return this;
    }

    /**
     * @return The licenses currently managed by the container
     * @since 0.2.0
     */
    public Collection<License> getLicenses() {
        return licenses;
    }

    /**
     * Overwrites the current licenses within this container with the provided collection
     *
     * @param licenses
     *            A collection of licenses to set this container to
     * @since 0.2.0
     */
    public void setLicenses(Collection<License> licenses) {
        Objects.requireNonNull(licenses);

        this.licenses = licenses;
    }

    /**
     * Adds a license to the project
     *
     * @param name
     *            The full legal name of the license
     * @param tag
     *            Shorthand used to reference the license. (ex: "MIT" for The MIT License)
     * @param url
     *            The official URL for the license text
     * @param distribution
     *            The primary method by which this project may be distributed. "repo" for projects distributed via Maven
     *            Central, "manual" for manual download
     * @return This container with configuration applied
     * @since 0.2.0
     */
    public LicenseContainer license(String name, String tag, String url, String distribution) {
        licenses.add(new License(name, tag, url, distribution));

        return this;
    }

    /**
     * Configures a new license to add to the set of licenses
     *
     * @param closure
     *            A closure used to configure a single license
     * @return This license container, with the specified license added
     * @since 0.2.0
     */
    public LicenseContainer license(Closure<License> closure) {
        Objects.requireNonNull(closure);

        licenses.add(new License().configure(closure));

        return this;
    }

    /**
     * Configures a new license to add to the set of licenses
     *
     * @param action
     *            An action used to configure a single license
     * @return This license container, with the specified license added
     * @since 0.2.0
     */
    public LicenseContainer license(Action<License> action) {
        Objects.requireNonNull(action);

        licenses.add(new License().configure(action));

        return this;
    }

    /**
     * Adds the Apache 2.0 license to the licenses the project may be used under with the "repo" distribution
     *
     * @return This container with configuration applied
     * @since 0.2.0
     */
    public LicenseContainer apache2() {
        return apache2(REPOSITORY_DISTRIBUTION);
    }

    /**
     * Adds the Apache 2.0 license to the licenses the project may be used under
     *
     * @param distribution
     *            The distribution method ("repo" or "manual") the project may use with this license
     * @return This container with configuration applied
     * @since 0.2.0
     */
    public LicenseContainer apache2(String distribution) {
        licenses.add(new License("The Apache Software License, Version 2.0", "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0.txt", distribution));

        return this;
    }

    /**
     * Adds the MIT license to the licenses the project may be used under with the "repo" distribution
     *
     * @return This container with configuration applied
     * @since 0.2.0
     */
    public LicenseContainer mit() {
        return mit(REPOSITORY_DISTRIBUTION);
    }

    /**
     * Adds the MIT license to the licenses the project may be used under
     *
     * @param distribution
     *            The distribution method ("repo" or "manual") the project may use with this license
     * @return This container with configuration applied
     * @since 0.2.0
     */
    public LicenseContainer mit(String distribution) {
        licenses.add(new License("The MIT License", "MIT", "https://opensource.org/licenses/MIT", distribution));

        return this;
    }

    /**
     * Adds the EPL 1.0 license to the licenses the project may be used under with the "repo" distribution
     *
     * @return This container with configuration applied
     * @since 0.2.0
     */
    public LicenseContainer epl() {
        return epl(REPOSITORY_DISTRIBUTION);
    }

    /**
     * Adds the EPL 1.0 license to the licenses the project may be used under
     *
     * @param distribution
     *            The distribution method ("repo" or "manual") the project may use with this license
     * @return This container with configuration applied
     * @since 0.2.0
     */
    public LicenseContainer epl(String distribution) {
        licenses.add(new License("Eclipse Public License 1.0", "EPL", "https://opensource.org/licenses/EPL-1.0",
                distribution));

        return this;
    }

    // TODO romeara
    public Action<MavenPomLicenseSpec> getPomConfiguration() {
        return (pomLicenses -> {
            getLicenses().stream()
            .map(License::getPomConfiguration)
            .forEach(pomLicenses::license);
        });
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLicenses());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof LicenseContainer) {
            LicenseContainer compare = (LicenseContainer) obj;

            result = Objects.equals(compare.getLicenses(), getLicenses());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("licenses", getLicenses())
                .toString();
    }

}
