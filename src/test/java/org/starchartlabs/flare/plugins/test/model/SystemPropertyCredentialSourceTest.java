/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.model;

import java.util.Optional;

import org.starchartlabs.flare.plugins.model.Credentials;
import org.starchartlabs.flare.plugins.model.SystemPropertyCredentialSource;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SystemPropertyCredentialSourceTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullUsernameVariable() throws Exception {
        new SystemPropertyCredentialSource(null, "passwordVar");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullPasswordVariable() throws Exception {
        new SystemPropertyCredentialSource("usernameVar", null);
    }

    @Test
    public void loadCredentialsNoUsernameValue() throws Exception {
        String usernameVar = SystemPropertyCredentialSource.class.getSimpleName() + ".test.usernameVar";
        String passwordVar = SystemPropertyCredentialSource.class.getSimpleName() + ".test.passwordVar";

        String originalUsernameValue = System.getProperty(usernameVar);
        String originalPasswordValue = System.getProperty(passwordVar);

        try {
            System.clearProperty(usernameVar);
            System.setProperty(passwordVar, "password");

            SystemPropertyCredentialSource source = new SystemPropertyCredentialSource(usernameVar, passwordVar);

            Optional<Credentials> result = source.loadCredentials();

            Assert.assertNotNull(result);
            Assert.assertFalse(result.isPresent());
        } finally {
            if (originalUsernameValue != null) {
                System.setProperty(usernameVar, originalUsernameValue);
            } else {
                System.clearProperty(usernameVar);
            }

            if (originalPasswordValue != null) {
                System.setProperty(passwordVar, originalPasswordValue);
            } else {
                System.clearProperty(passwordVar);
            }
        }
    }

    @Test
    public void loadCredentialsNoPasswordValue() throws Exception {
        String usernameVar = SystemPropertyCredentialSource.class.getSimpleName() + ".test.usernameVar";
        String passwordVar = SystemPropertyCredentialSource.class.getSimpleName() + ".test.passwordVar";

        String originalUsernameValue = System.getProperty(usernameVar);
        String originalPasswordValue = System.getProperty(passwordVar);

        try {
            System.setProperty(usernameVar, "username");
            System.clearProperty(passwordVar);

            SystemPropertyCredentialSource source = new SystemPropertyCredentialSource(usernameVar, passwordVar);

            Optional<Credentials> result = source.loadCredentials();

            Assert.assertNotNull(result);
            Assert.assertFalse(result.isPresent());
        } finally {
            if (originalUsernameValue != null) {
                System.setProperty(usernameVar, originalUsernameValue);
            } else {
                System.clearProperty(usernameVar);
            }

            if (originalPasswordValue != null) {
                System.setProperty(passwordVar, originalPasswordValue);
            } else {
                System.clearProperty(passwordVar);
            }
        }
    }

    @Test
    public void loadCredentials() throws Exception {
        String usernameVar = SystemPropertyCredentialSource.class.getSimpleName() + ".test.usernameVar";
        String passwordVar = SystemPropertyCredentialSource.class.getSimpleName() + ".test.passwordVar";

        String originalUsernameValue = System.getProperty(usernameVar);
        String originalPasswordValue = System.getProperty(passwordVar);

        try {
            System.setProperty(usernameVar, "username");
            System.setProperty(passwordVar, "password");

            SystemPropertyCredentialSource source = new SystemPropertyCredentialSource(usernameVar, passwordVar);

            Optional<Credentials> result = source.loadCredentials();

            Assert.assertNotNull(result);
            Assert.assertTrue(result.isPresent());
            Assert.assertEquals(result.get().getUsername(), "username");
            Assert.assertEquals(result.get().getPassword(), "password");
        } finally {
            if (originalUsernameValue != null) {
                System.setProperty(usernameVar, originalUsernameValue);
            } else {
                System.clearProperty(usernameVar);
            }

            if (originalPasswordValue != null) {
                System.setProperty(passwordVar, originalPasswordValue);
            } else {
                System.clearProperty(passwordVar);
            }
        }
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        SystemPropertyCredentialSource result1 = new SystemPropertyCredentialSource("usernameVar", "passwordVar");
        SystemPropertyCredentialSource result2 = new SystemPropertyCredentialSource("usernameVar", "passwordVar");

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        SystemPropertyCredentialSource result = new SystemPropertyCredentialSource("usernameVar", "passwordVar");

        Assert.assertFalse(result.equals(null));
    }

    // Test is specifically for the mis-matched type case - warning is invalid
    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void equalsDifferentClass() throws Exception {
        SystemPropertyCredentialSource result = new SystemPropertyCredentialSource("usernameVar", "passwordVar");

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        SystemPropertyCredentialSource result = new SystemPropertyCredentialSource("usernameVar", "passwordVar");

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        SystemPropertyCredentialSource result1 = new SystemPropertyCredentialSource("usernameVar1", "passwordVar");
        SystemPropertyCredentialSource result2 = new SystemPropertyCredentialSource("usernameVar2", "passwordVar");

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        SystemPropertyCredentialSource result1 = new SystemPropertyCredentialSource("usernameVar", "passwordVar");
        SystemPropertyCredentialSource result2 = new SystemPropertyCredentialSource("usernameVar", "passwordVar");

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        SystemPropertyCredentialSource obj = new SystemPropertyCredentialSource("usernameVar", "passwordVar");

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("usernameVariable=usernameVar"));
        Assert.assertTrue(result.contains("passwordVariable=passwordVar"));
    }

}
