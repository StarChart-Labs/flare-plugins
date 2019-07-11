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
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ManagedCredentialsPluginTest {

    private static final String PLUGIN_ID = "org.starchartlabs.flare.managed-credentials";

    private Project project;

    @BeforeClass
    public void setupProject() {
        project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(PLUGIN_ID);
    }

    @Test
    public void configurationApplied() throws Exception {
        Object found = project.getExtensions().findByName("credentials");

        Assert.assertNotNull(found);
        Assert.assertTrue(found instanceof NamedDomainObjectContainer);
    }

}
