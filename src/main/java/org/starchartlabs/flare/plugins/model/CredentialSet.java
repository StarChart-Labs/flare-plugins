/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.gradle.api.GradleException;
import org.gradle.api.Named;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

/**
 * Configuration for a single named set of credentials
 *
 * <p>
 * Credentials may be drawn from sources such as environment variables, system properties, and other values provided by
 * the build system. Configurations allow specification of an ordered list of sources to attempt to draw credentials
 * from during a build, and access to the read credentials by the build system
 *
 * @author romeara
 * @since 0.1.0
 */
public class CredentialSet implements Named {

    private String name;

    private List<CredentialSource> sources;

    private Credentials credentials;

    public CredentialSet(String name) {
        this.name = Objects.requireNonNull(name);

        sources = new ArrayList<>();
        credentials = null;
    }

    @Override
    public String getName() {
        return name;
    }

    public CredentialSet configure(@DelegatesTo(CredentialSet.class) Closure<CredentialSet> closure) {
        Objects.requireNonNull(closure);

        closure.setDelegate(this);
        closure.call();

        return this;
    }

    public String getUsername() {
        return getCredentials().getUsername();
    }

    public String getPassword() {
        return getCredentials().getPassword();
    }

    /**
     * Allows reading credentials from environment variables
     *
     * @param usernameVariable
     *            The name of the environment variable to read a username from
     * @param passwordVariable
     *            The name of the environment variable to read a password from
     * @return This credential set representation
     * @since 0.1.0
     */
    public CredentialSet environment(String usernameVariable, String passwordVariable) {
        sources.add(new EnvironmentCredentialSource(usernameVariable, passwordVariable));

        return this;
    }

    /**
     * Allows reading credentials from system properties
     *
     * @param usernameVariable
     *            The name of the system property to read a username from
     * @param passwordVariable
     *            The name of the system property to read a password from
     * @return This credential set representation
     * @since 0.1.0
     */
    public CredentialSet systemProperties(String usernameVariable, String passwordVariable) {
        sources.add(new SystemPropertyCredentialSource(usernameVariable, passwordVariable));

        return this;
    }

    /**
     * Allows reading credentials provided to the build system directly
     *
     * @param username
     *            The username to assign to the credentials
     * @param password
     *            The password to assign to the credentials
     * @return This credential set representation
     * @since 0.1.0
     */
    public CredentialSet defaultCredentials(String username, String password) {
        sources.add(new DefaultCredentialSource(username, password));

        return this;
    }

    /**
     * @return A loaded set of credentials from the first source which could provide them
     */
    private synchronized Credentials getCredentials() {
        if (credentials == null) {
            credentials = sources.stream()
                    .map(CredentialSource::loadCredentials)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst()
                    .orElseThrow(
                            () -> new GradleException("Could not load '" + getName()
                            + "' credentials - no sources provided a complete credential set"));
        }

        return credentials;
    }

}
