/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.model;

import org.gradle.api.GradleException;
import org.starchartlabs.flare.plugins.model.CredentialSet;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CredentialSetTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullName() throws Exception {
        new CredentialSet(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void configureNullClosure() throws Exception {
        new CredentialSet("name").configure(null);
    }

    @Test(expectedExceptions = GradleException.class)
    public void getNoSources() throws Exception {
        new CredentialSet("name").getUsername();
    }

    @Test
    public void environment() throws Exception {
        new CredentialSet("name").environment("ENV_1", "ENV_2");
    }

    @Test
    public void systemProperties() throws Exception {
        new CredentialSet("name").systemProperties("property_1", "property_2");
    }

    @Test
    public void defaultCredentials() throws Exception {
        CredentialSet credentialSet = new CredentialSet("name")
                .defaultCredentials("user", "pass");

        Assert.assertEquals(credentialSet.getUsername(), "user");
        Assert.assertEquals(credentialSet.getPassword(), "pass");
    }

}
