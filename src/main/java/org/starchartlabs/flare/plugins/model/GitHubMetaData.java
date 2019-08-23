/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.function.Consumer;

import org.gradle.api.GradleException;
import org.starchartlabs.alloy.core.Preconditions;
import org.starchartlabs.alloy.core.Strings;

/**
 * Represent various domain-specific language definitions configuration of GitHub repository projects
 *
 * <p>
 * Applies GitHub-standard values for a variety of project meta data
 *
 * @author romeara
 * @since 0.2.0
 */
public class GitHubMetaData {

    private static final String REPOSITORY_URL_TEMPLATE = "https://github.com/%s/%s";

    private static final String CONNECTION_TEMPLATE = "scm:git:git://github.com/%s/%s.git";

    private static final String DEVELOPER_CONNECTION_TEMPLATE = "scm:git:ssh://github.com/%s/%s.git";

    private static final String USER_URL_TEMPLATE = "https://github.com/%s";

    private final ProjectMetaData delegate;

    /**
     * @param delegate
     *            The project meta data to configure when DSL methods are invoked
     * @since 0.2.0
     */
    public GitHubMetaData(ProjectMetaData delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    /**
     * Configures project URL and SCM meta data based on GitHub standards for a hosted repository
     *
     * @param owner
     *            The name of the repository owner
     * @param repository
     *            The name of the repository
     * @since 0.2.0
     */
    public void repository(String owner, String repository) {
        Objects.requireNonNull(owner);
        Objects.requireNonNull(repository);

        String url = Strings.format(REPOSITORY_URL_TEMPLATE, owner, repository);

        delegate.setUrl(url);

        delegate.getScm().setVcsUrl(url);
        delegate.getScm().setConnection(Strings.format(CONNECTION_TEMPLATE, owner, repository));
        delegate.getScm().setDeveloperConnection(Strings.format(DEVELOPER_CONNECTION_TEMPLATE, owner, repository));
    }

    /**
     * Adds a developer to project meta data
     *
     * @param username
     *            Username to use as the developer's id, name, and when constructing the GitHub user URL to assign to
     *            the developer
     * @since 0.2.0
     */
    public void developer(String username) {
        Objects.requireNonNull(username);

        developer(username, username);
    }

    /**
     * Adds a developer to project meta data
     *
     * @param username
     *            Username to use as the developer's id, and when constructing the GitHub user URL to assign to the
     *            developer
     * @param name
     *            Name to assign the developer
     * @since 0.2.0
     */
    public void developer(String username, String name) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(name);

        delegate.developers(
                container -> container.developer(username, name, Strings.format(USER_URL_TEMPLATE, username)));
    }

    /**
     * Applies GitHub developers from a file
     *
     * <p>
     * The provided file may have blank lines, comment lines (starting with '#'), and developer lines. Developer lines
     * must be of the form 'username' or 'username, name'. Developers will be added to the project meta data with an id
     * of the provider username, name of the provided name or username, and a GitHub user URL for the provided username
     *
     * @param file
     *            File describing developers to add
     * @since 0.2.0
     */
    public void developers(File file) {
        Objects.requireNonNull(file);

        loadUserFile(file, this::addDeveloper);
    }

    /**
     * Adds a contributor to project meta data
     *
     * @param username
     *            Username to use as the contributors name, and when constructing the GitHub user URL to assign to the
     *            contributor
     * @since 0.2.0
     */
    public void contributor(String username) {
        Objects.requireNonNull(username);

        contributor(username, username);
    }

    /**
     * Adds a contributor to project meta data
     *
     * @param username
     *            Username to use when constructing the GitHub user URL to assign to the contributor
     * @param name
     *            Name to assign the contributor
     * @since 0.2.0
     */
    public void contributor(String username, String name) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(name);

        delegate.contributors(
                container -> container.contributor(name, Strings.format(USER_URL_TEMPLATE, username)));
    }

    /**
     * Applies GitHub contributors from a file
     *
     * <p>
     * The provided file may have blank lines, comment lines (starting with '#'), and contributor lines. Contributor
     * lines must be of the form 'username' or 'username, name'. Contributors will be added to the project meta data
     * with a name of the provided name or username, and a GitHub user URL for the provided username
     *
     * @param file
     *            File describing contributors to add
     * @since 0.2.0
     */
    public void contributors(File file) {
        Objects.requireNonNull(file);

        loadUserFile(file, this::addContributor);
    }

    /**
     * Loads lines from a file to apply as configuration to project meta data
     *
     * <p>
     * Files may have configuration lines, blank lines, and lines preceded with a "#" (comment lines). Blank and comment
     * lines will be discarded. Configuration lines will be split on "," before being supplied to the provided consumer
     *
     * @param file
     *            The file to process
     * @param lineHandler
     *            Function to apply to parsed configuration lines
     */
    private void loadUserFile(File file, Consumer<String[]> lineHandler) {
        Objects.requireNonNull(file);
        Preconditions.checkArgument(file.exists(),
                () -> Strings.format("User file does not exist at '%s'", file.getAbsolutePath()));

        try {
            Files.lines(file.toPath(), StandardCharsets.UTF_8)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .filter(s -> !s.startsWith("#"))
            .map(s -> s.split(","))
            .forEach(lineHandler);
        } catch (IOException e) {
            throw new GradleException(String.format("Error reading user file at '%s'", file.getAbsolutePath()));
        }
    }

    /**
     * Parses a file line representing a GitHub developer to a project
     *
     * @param line
     *            A line of the form "name" or "username, name"
     */
    private void addDeveloper(String[] line) {
        Objects.requireNonNull(line);
        Preconditions.checkArgument(line.length > 0 && line.length < 3,
                "Developer specifications must be of form 'username' or 'username, name'");

        if (line.length > 1) {
            developer(line[0].trim(), line[1].trim());
        } else {
            developer(line[0].trim());
        }
    }

    /**
     * Parses a file line representing a GitHub contributor to a project
     *
     * @param line
     *            A line of the form "name" or "username, name"
     */
    private void addContributor(String[] line) {
        Objects.requireNonNull(line);
        Preconditions.checkArgument(line.length > 0 && line.length < 3,
                "Contributor specifications must be of form 'username' or 'username, name'");

        if (line.length > 1) {
            contributor(line[0].trim(), line[1].trim());
        } else {
            contributor(line[0].trim());
        }
    }

}
