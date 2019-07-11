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
 * Credential source which allows loading of default hard-coded credentials
 *
 * <p>
 * It is not recommended to provide hard-coded credential values - use environment values whenever possible
 *
 * @author romeara
 * @since 0.1.0
 */
public class DefaultCredentialSource implements CredentialSource {

    private final Credentials credentials;

    public DefaultCredentialSource(String username, String password) {
        this.credentials = new Credentials(username, password);
    }

    @Override
    public Optional<Credentials> loadCredentials() {
        return Optional.of(credentials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(credentials.hashCode());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof DefaultCredentialSource) {
            DefaultCredentialSource compare = (DefaultCredentialSource) obj;

            result = Objects.equals(credentials, compare.credentials);
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("credentials", credentials)
                .toString();
    }

}
