/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.model;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.annotation.Nullable;

import org.starchartlabs.alloy.core.MoreObjects;

/**
 * Represents a loaded set of credentials for use by the build system
 *
 * @author romeara
 * @since 0.1.0
 */
public class Credentials {

    private final String username;

    private final byte[] password;

    public Credentials(String username, String password) {
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password).getBytes(StandardCharsets.UTF_8);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return new String(password, StandardCharsets.UTF_8);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(),
                getPassword());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;

        if (obj instanceof Credentials) {
            Credentials compare = (Credentials) obj;

            result = Objects.equals(compare.getUsername(), getUsername())
                    && Objects.equals(compare.getPassword(), getPassword());
        }

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass()).omitNullValues()
                .add("username", getUsername())
                .add("password", "<password obscured>")
                .toString();
    }

}
