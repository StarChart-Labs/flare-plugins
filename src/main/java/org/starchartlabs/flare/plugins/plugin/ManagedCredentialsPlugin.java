/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.plugin;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.starchartlabs.flare.plugins.model.CredentialSet;

/**
 * Configuration plug-in that adds structures for defining credentials which can be referenced throughout the build
 * system
 *
 * @author romeara
 * @since 0.1.0
 */
public class ManagedCredentialsPlugin implements Plugin<Project> {

    private static final String CREDENTIALS_DSL_EXTENSION = "credentials";

    @Override
    public void apply(Project project) {
        NamedDomainObjectContainer<CredentialSet> credentials = project.container(CredentialSet.class,
                name -> new CredentialSet(name));

        project.getExtensions().add(CREDENTIALS_DSL_EXTENSION, credentials);
    }

}
