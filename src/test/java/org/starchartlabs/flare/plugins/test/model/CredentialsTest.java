/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.model;

import org.starchartlabs.flare.plugins.model.Credentials;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CredentialsTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullUsername() throws Exception {
        new Credentials(null, "password");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullPassword() throws Exception {
        new Credentials("username", null);
    }

    @Test
    public void getTest() throws Exception {
        Credentials result = new Credentials("username", "password");

        Assert.assertEquals(result.getUsername(), "username");
        Assert.assertEquals(result.getPassword(), "password");
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        Credentials result1 = new Credentials("username", "password");
        Credentials result2 = new Credentials("username", "password");

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        Credentials result = new Credentials("username", "password");

        Assert.assertFalse(result.equals(null));
    }

    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void equalsDifferentClass() throws Exception {
        Credentials result = new Credentials("username", "password");

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        Credentials result = new Credentials("username", "password");

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        Credentials result1 = new Credentials("username1", "password");
        Credentials result2 = new Credentials("username2", "password");

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        Credentials result1 = new Credentials("username", "password");
        Credentials result2 = new Credentials("username", "password");

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        Credentials obj = new Credentials("username", "password");

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("username=username"));
        Assert.assertTrue(result.contains("password=<password obscured>"));
    }

}
