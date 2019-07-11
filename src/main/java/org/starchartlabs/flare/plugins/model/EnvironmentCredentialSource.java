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

import org.starchartlabs.alloy.core.MoreObjects;

/**
 * Credential source which allows loading from environment variables
 *
 * @author romeara
 * @since 0.1.0
 */
public class EnvironmentCredentialSource implements CredentialSource {

    private final String usernameVariable;

    private final String passwordVariable;

    public EnvironmentCredentialSource(String usernameVariable, String passwordVariable) {
        this.usernameVariable = Objects.requireNonNull(usernameVariable);
        this.passwordVariable = Objects.requireNonNull(passwordVariable);
    }

    @Override
    public Optional<Credentials> loadCredentials() {
        Credentials result = null;

        String username = System.getenv(usernameVariable);
        String password = System.getenv(passwordVariable);

        if (username != null && password != null) {
            result = new Credentials(username, password);
        }

        return Optional.ofNullable(result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usernameVariable, passwordVariable);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof EnvironmentCredentialSource) {
            EnvironmentCredentialSource compare = (EnvironmentCredentialSource) obj;

            result = Objects.equals(usernameVariable, compare.usernameVariable)
                    && Objects.equals(passwordVariable, compare.passwordVariable);
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("usernameVariable", usernameVariable)
                .add("passwordVariable", passwordVariable)
                .toString();
    }

}
