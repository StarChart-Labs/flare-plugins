/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.model;

import java.util.Optional;

/**
 * Represents an available mechanism to load a set of credentials
 *
 * @author romeara
 * @since 0.1.0
 */
public interface CredentialSource {

    /**
     * @return Loaded credentials, or empty if either the username or password could not be loaded
     * @since 0.1.0
     */
    Optional<Credentials> loadCredentials();

}
