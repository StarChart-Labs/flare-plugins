/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.model;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nullable;

import org.gradle.api.Action;
import org.starchartlabs.alloy.core.MoreObjects;

import groovy.lang.Closure;

/**
 * Represents project management meta data for a repository/project
 *
 * <p>
 * This includes information such as source control, individuals who maintain or contribute to its development, and
 * licensing
 *
 * @author romeara
 * @since 0.2.0
 */
public class ProjectMetaData {

    private String url;

    private Scm scm;

    private DeveloperContainer developers;

    private ContributorContainer contributors;

    private LicenseContainer licenses;

    /**
     * Initializes a new emty meta data instance
     *
     * @since 0.2.0
     */
    public ProjectMetaData() {
        this(null, new Scm(), new DeveloperContainer(), new ContributorContainer(), new LicenseContainer());
    }

    /**
     * @param url
     *            Primary URL of the project
     * @param scm
     *            Source control management information associated with the project
     * @param developers
     *            Configuration of developers associated with the project
     * @param contributors
     *            Configuration of any contributors to the project
     * @param licenses
     *            Licensing terms of the project
     * @since 0.2.0
     */
    public ProjectMetaData(@Nullable String url, Scm scm, DeveloperContainer developers,
            ContributorContainer contributors, LicenseContainer licenses) {
        this.url = url;
        this.scm = Objects.requireNonNull(scm);
        this.developers = Objects.requireNonNull(developers);
        this.contributors = Objects.requireNonNull(contributors);
        this.licenses = Objects.requireNonNull(licenses);
    }

    /**
     * Allows configuration of multiple values based on GitHub conventions
     *
     * @param closure
     *            A closure to apply to a GitHub DSL for configuration of project meta data
     * @return This project meta data with values applied from the GitHub DSL
     * @since 0.2.0
     */
    public ProjectMetaData github(Closure<GitHubMetaData> closure) {
        Objects.requireNonNull(closure);

        closure.setDelegate(new GitHubMetaData(this));
        closure.call();

        return this;
    }

    /**
     * Allows configuration of multiple values based on GitHub conventions
     *
     * @param action
     *            An action to apply to a GitHub DSL for configuration of project meta data
     * @return This project meta data with values applied from the GitHub DSL
     * @since 0.2.0
     */
    public ProjectMetaData github(Action<GitHubMetaData> action) {
        Objects.requireNonNull(action);

        action.execute(new GitHubMetaData(this));

        return this;
    }

    /**
     * @return Primary URL of the project
     * @since 0.2.0
     */
    @Nullable
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            Primary URL of the project
     * @since 0.2.0
     */
    public void setUrl(@Nullable String url) {
        this.url = url;
    }

    /**
     * @return Source control management information associated with the project
     * @since 0.2.0
     */
    public Scm getScm() {
        return scm;
    }

    /**
     * @param scm
     *            Source control management information associated with the project
     * @since 0.2.0
     */
    public void setScm(Scm scm) {
        this.scm = Objects.requireNonNull(scm);
    }

    /**
     * Allows external configuration of the scm. Mainly used in Gradle DSLs (Groovy) for in-line configuration
     *
     * @param closure
     *            Closure to configure the scm
     * @return This meta data instance with configuration applied
     * @since 0.2.0
     */
    public ProjectMetaData scm(Closure<Scm> closure) {
        Objects.requireNonNull(closure);

        scm.configure(closure);

        return this;
    }

    /**
     * Allows external configuration of the scm. Mainly used in Gradle DSLs (Groovy) for in-line configuration
     *
     * @param action
     *            Action to configure the scm
     * @return This meta data instance with configuration applied
     * @since 0.2.0
     */
    public ProjectMetaData scm(Action<Scm> action) {
        Objects.requireNonNull(action);

        scm.configure(action);

        return this;
    }

    /**
     * @return The developers currently configured on this meta data
     * @since 0.2.0
     */
    public Collection<Developer> getDevelopers() {
        return developers.getDevelopers();
    }

    /**
     * Overwrites the current developers within this meta data with the provided collection
     *
     * @param developers
     *            A collection of developers to set this meta data to
     * @since 0.2.0
     */
    public void setDevelopers(Collection<Developer> developers) {
        this.developers.setDevelopers(developers);
    }

    /**
     * Configures developers for this meta data via the specified action
     *
     * @param closure
     *            A closure used to configure the developers
     * @return This meta data instance, with the provided configuration applied
     * @since 0.2.0
     */
    public ProjectMetaData developers(Closure<DeveloperContainer> closure) {
        Objects.requireNonNull(closure);

        developers.configure(closure);

        return this;
    }

    /**
     * Configures developers for this meta data via the specified action
     *
     * @param action
     *            An action used to configure the developers
     * @return This meta data instance, with the provided configuration applied
     * @since 0.2.0
     */
    public ProjectMetaData developers(Action<DeveloperContainer> action) {
        Objects.requireNonNull(action);

        developers.configure(action);

        return this;
    }

    /**
     * @return The contributors currently configured on this meta data
     * @since 0.2.0
     */
    public Collection<Contributor> getContributors() {
        return contributors.getContributors();
    }

    /**
     * Overwrites the current contributors within this meta data with the provided collection
     *
     * @param contributors
     *            A collection of contributors to set this meta data to
     * @since 0.2.0
     */
    public void setContributors(Collection<Contributor> contributors) {
        this.contributors.setContributors(contributors);
    }

    /**
     * Configures contributors for this meta data via the specified action
     *
     * @param closure
     *            A closure used to configure the contributors
     * @return This meta data instance, with the provided configuration applied
     * @since 0.2.0
     */
    public ProjectMetaData contributors(Closure<ContributorContainer> closure) {
        Objects.requireNonNull(closure);

        contributors.configure(closure);

        return this;
    }

    /**
     * Configures contributors for this meta data via the specified action
     *
     * @param action
     *            An action used to configure the contributors
     * @return This meta data instance, with the provided configuration applied
     * @since 0.2.0
     */
    public ProjectMetaData contributors(Action<ContributorContainer> action) {
        Objects.requireNonNull(action);

        contributors.configure(action);

        return this;
    }

    /**
     * @return The licenses currently configured on this meta data
     * @since 0.2.0
     */
    public Collection<License> getLicenses() {
        return licenses.getLicenses();
    }

    /**
     * Overwrites the current licenses within this meta data with the provided collection
     *
     * @param licenses
     *            A collection of licenses to set this meta data to
     * @since 0.2.0
     */
    public void setLicenses(Collection<License> licenses) {
        this.licenses.setLicenses(licenses);
    }

    /**
     * Configures the licenses for the project via the specified action
     *
     * @param closure
     *            A closure used to configure the licenses
     * @return This meta data instance, with the provided configuration applied
     * @since 0.2.0
     */
    public ProjectMetaData licenses(Closure<LicenseContainer> closure) {
        Objects.requireNonNull(closure);

        licenses.configure(closure);

        return this;
    }

    /**
     * Configures the licenses for the project via the specified action
     *
     * @param action
     *            An action used to configure the licenses
     * @return This meta data instance, with the provided configuration applied
     * @since 0.2.0
     */
    public ProjectMetaData licenses(Action<LicenseContainer> action) {
        Objects.requireNonNull(action);

        licenses.configure(action);

        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl(),
                getScm(),
                getDevelopers(),
                getContributors(),
                getLicenses());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof ProjectMetaData) {
            ProjectMetaData compare = (ProjectMetaData) obj;

            result = Objects.equals(compare.getUrl(), getUrl())
                    && Objects.equals(compare.getScm(), getScm())
                    && Objects.equals(compare.getDevelopers(), getDevelopers())
                    && Objects.equals(compare.getContributors(), getContributors())
                    && Objects.equals(compare.getLicenses(), getLicenses());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("url", getUrl())
                .add("scm", getScm())
                .add("developers", getDevelopers())
                .add("contributors", getContributors())
                .add("licenses", getLicenses())
                .toString();
    }

}
