/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.model;

import java.util.Optional;

import org.starchartlabs.flare.plugins.model.Credentials;
import org.starchartlabs.flare.plugins.model.DefaultCredentialSource;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DefaultCredentialSourceTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullUsername() throws Exception {
        new DefaultCredentialSource(null, "password");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullPassword() throws Exception {
        new DefaultCredentialSource("username", null);
    }

    @Test
    public void loadCredentials() throws Exception {
        DefaultCredentialSource result = new DefaultCredentialSource("username", "password");

        Optional<Credentials> credentials = result.loadCredentials();

        Assert.assertNotNull(credentials);
        Assert.assertTrue(credentials.isPresent());
        Assert.assertEquals(credentials.get().getUsername(), "username");
        Assert.assertEquals(credentials.get().getPassword(), "password");
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        DefaultCredentialSource result1 = new DefaultCredentialSource("username", "password");
        DefaultCredentialSource result2 = new DefaultCredentialSource("username", "password");

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        DefaultCredentialSource result = new DefaultCredentialSource("username", "password");

        Assert.assertFalse(result.equals(null));
    }

    // Test is specifically for the mis-matched type case - warning is invalid
    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void equalsDifferentClass() throws Exception {
        DefaultCredentialSource result = new DefaultCredentialSource("username", "password");

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        DefaultCredentialSource result = new DefaultCredentialSource("username", "password");

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        DefaultCredentialSource result1 = new DefaultCredentialSource("username1", "password");
        DefaultCredentialSource result2 = new DefaultCredentialSource("username2", "password");

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        DefaultCredentialSource result1 = new DefaultCredentialSource("username", "password");
        DefaultCredentialSource result2 = new DefaultCredentialSource("username", "password");

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        DefaultCredentialSource obj = new DefaultCredentialSource("username", "password");

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("credentials=" + obj.loadCredentials().get().toString()));
    }

}
