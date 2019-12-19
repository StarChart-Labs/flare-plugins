/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.plugin;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.starchartlabs.flare.plugins.model.CredentialSet;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jfrog.bintray.gradle.BintrayExtension;

public class BintrayCredentialsPluginTest {

    private static final String PLUGIN_ID = "org.starchartlabs.flare.bintray-credentials";

    private Project project;

    @BeforeClass
    public void setupProject() {
        project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("com.jfrog.bintray");
        project.getPluginManager().apply(PLUGIN_ID);
        project.getPluginManager().apply("org.starchartlabs.flare.managed-credentials");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void credentialsConfigurationApplied() throws Exception {
        NamedDomainObjectContainer<CredentialSet> found = (NamedDomainObjectContainer<CredentialSet>) project
                .getExtensions().getByName("credentials");

        found.getByName("bintray");

        Assert.assertNotNull(found);

        CredentialSet bintray = found.getByName("bintray");

        // Bintray defaults to blank in an environment with the proper ENV set
        Assert.assertNotNull(bintray);
        Assert.assertEquals(bintray.getUsername(), "");
        Assert.assertEquals(bintray.getPassword(), "");
    }

    @Test
    public void bintrayConfigurationApplied() throws Exception {
        BintrayExtension found = project.getExtensions().getByType(BintrayExtension.class);

        Assert.assertNotNull(found);

        Assert.assertEquals(found.getUser(), "");
        Assert.assertEquals(found.getKey(), "");
    }

}
