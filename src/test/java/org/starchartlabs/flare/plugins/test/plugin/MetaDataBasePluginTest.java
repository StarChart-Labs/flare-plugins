/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.plugin;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.starchartlabs.flare.plugins.model.ProjectMetaData;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MetaDataBasePluginTest {

    private static final String PLUGIN_ID = "org.starchartlabs.flare.metadata-base";

    private Project project;

    @BeforeClass
    public void setupProject() {
        project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(PLUGIN_ID);
    }

    @Test
    public void configurationApplied() throws Exception {
        Object found = project.getExtensions().findByName("projectMetaData");

        Assert.assertNotNull(found);
        Assert.assertTrue(found instanceof ProjectMetaData);
    }

}
