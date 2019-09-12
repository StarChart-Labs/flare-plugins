/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.model;

import org.starchartlabs.flare.plugins.model.Constraint;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConstraintTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullLine() throws Exception {
        new Constraint(null);
    }

    @Test
    public void constructNoSpecificConfigurations() throws Exception {
        Constraint result = new Constraint("group:artifact:1.0");

        Assert.assertEquals(result.getGav(), "group:artifact:1.0");
        Assert.assertTrue(result.getConfigurations().isEmpty());
        Assert.assertTrue(result.isConfigurationApplicable("anything"));
    }

    @Test
    public void constructSingleConfiguration() throws Exception {
        Constraint result = new Constraint("group:artifact:1.0,config1");

        Assert.assertEquals(result.getGav(), "group:artifact:1.0");
        Assert.assertEquals(result.getConfigurations().size(), 1);
        Assert.assertTrue(result.getConfigurations().contains("config1"));
        Assert.assertTrue(result.isConfigurationApplicable("config1"));
        Assert.assertFalse(result.isConfigurationApplicable("other"));
    }

    @Test
    public void constructMultipleConfigurations() throws Exception {
        Constraint result = new Constraint("group:artifact:1.0,config1,config2");

        Assert.assertEquals(result.getGav(), "group:artifact:1.0");
        Assert.assertEquals(result.getConfigurations().size(), 2);
        Assert.assertTrue(result.getConfigurations().contains("config1"));
        Assert.assertTrue(result.getConfigurations().contains("config2"));
        Assert.assertTrue(result.isConfigurationApplicable("config1"));
        Assert.assertTrue(result.isConfigurationApplicable("config2"));
        Assert.assertFalse(result.isConfigurationApplicable("other"));
    }

}
