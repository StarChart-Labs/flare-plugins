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
 * Configuration plug-in that adds default configuration to read credentials from environment variables BINTRAY_USER and
 * BINTRAY_API_KEY for use within the Gradle build
 *
 * @author romeara
 * @since 1.0.0
 */
public class BintrayCredentialsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPluginManager().withPlugin("com.jfrog.bintray", bintrayPlugin -> {
            setupManagedCredentials(project);
        });
    }

    private CredentialSet setupManagedCredentials(Project project) {
        project.getPluginManager().apply("org.starchartlabs.flare.managed-credentials");

        @SuppressWarnings("unchecked")
        NamedDomainObjectContainer<CredentialSet> credentials = (NamedDomainObjectContainer<CredentialSet>) project
        .getExtensions().getByName("credentials");

        // Setup reading BinTray credentials from the environment, with defaults of blank to allow non-publishing
        // tasks to be run in environments where the environment variables are not set
        CredentialSet result = new CredentialSet("bintray")
                .environment("BINTRAY_USER", "BINTRAY_API_KEY")
                .defaultCredentials("", "");

        credentials.add(result);

        return result;
    }

}
