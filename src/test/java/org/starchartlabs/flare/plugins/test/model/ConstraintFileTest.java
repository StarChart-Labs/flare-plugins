/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.starchartlabs.flare.plugins.model.ConstraintFile;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConstraintFileTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullPath() throws Exception {
        new ConstraintFile(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void getDependencyNotationsNullConfiguration() throws Exception {
        List<String> lines = new ArrayList<>();
        lines.add("group1:artifact1:1.0");
        lines.add("group2:artifact2:2.0");
        lines.add("group3:artifact3:3.0");

        Path path = Files.createTempFile("constraintFileTest", "no-discards");

        Files.write(path, lines);

        ConstraintFile file = new ConstraintFile(path);

        file.getDependencyNotations(null);
    }

    @Test
    public void getDependencyNotationsNoDiscardedLines() throws Exception {
        List<String> lines = new ArrayList<>();
        lines.add("group1:artifact1:1.0");
        lines.add("group2:artifact2:2.0");
        lines.add("group3:artifact3:3.0");

        Path path = Files.createTempFile("constraintFileTest", "no-discards");

        Files.write(path, lines);

        ConstraintFile file = new ConstraintFile(path);

        Set<String> result = file.getDependencyNotations("compile");

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 3);
        Assert.assertTrue(result.contains("group1:artifact1:1.0"));
        Assert.assertTrue(result.contains("group2:artifact2:2.0"));
        Assert.assertTrue(result.contains("group3:artifact3:3.0"));
    }

    @Test
    public void getDependencyNotationsCommentsBlanksAndWhitespace() throws Exception {
        List<String> lines = new ArrayList<>();
        lines.add("group1:artifact1:1.0");
        lines.add("");
        lines.add("# Comment");
        lines.add("group2:artifact2:2.0 ");
        lines.add(" # Comment");
        lines.add(" group3:artifact3:3.0");

        Path path = Files.createTempFile("constraintFileTest", "no-discards");

        Files.write(path, lines);

        ConstraintFile file = new ConstraintFile(path);

        Set<String> result = file.getDependencyNotations("compile");

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 3);
        Assert.assertTrue(result.contains("group1:artifact1:1.0"));
        Assert.assertTrue(result.contains("group2:artifact2:2.0"));
        Assert.assertTrue(result.contains("group3:artifact3:3.0"));
    }

    @Test
    public void getDependencyNotationsSpecificConfigurations() throws Exception {
        List<String> lines = new ArrayList<>();
        lines.add("group1:artifact1:1.0,first");
        lines.add("group2:artifact2:2.0,first, second");
        lines.add("group3:artifact3:3.0");
        lines.add("group4:artifact3:4.0,third");

        Path path = Files.createTempFile("constraintFileTest", "no-discards");

        Files.write(path, lines);

        ConstraintFile file = new ConstraintFile(path);

        Set<String> result = file.getDependencyNotations("first");

        // Should contain first-only, first & second, and unspecified (all)
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 3);
        Assert.assertTrue(result.contains("group1:artifact1:1.0"));
        Assert.assertTrue(result.contains("group2:artifact2:2.0"));
        Assert.assertTrue(result.contains("group3:artifact3:3.0"));
    }

    @Test
    public void hashCodeEqualWhenDataEqual() throws Exception {
        ConstraintFile result1 = new ConstraintFile(Paths.get("path"));
        ConstraintFile result2 = new ConstraintFile(Paths.get("path"));

        Assert.assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    public void equalsNull() throws Exception {
        ConstraintFile result = new ConstraintFile(Paths.get("path"));

        Assert.assertFalse(result.equals(null));
    }

    // Test is specifically for the mis-matched type case - warning is invalid
    @Test
    @SuppressWarnings("unlikely-arg-type")
    public void equalsDifferentClass() throws Exception {
        ConstraintFile result = new ConstraintFile(Paths.get("path"));

        Assert.assertFalse(result.equals("string"));
    }

    @Test
    public void equalsSelf() throws Exception {
        ConstraintFile result = new ConstraintFile(Paths.get("path"));

        Assert.assertTrue(result.equals(result));
    }

    @Test
    public void equalsDifferentData() throws Exception {
        ConstraintFile result1 = new ConstraintFile(Paths.get("path1"));
        ConstraintFile result2 = new ConstraintFile(Paths.get("path2"));

        Assert.assertFalse(result1.equals(result2));
    }

    @Test
    public void equalsSameData() throws Exception {
        ConstraintFile result1 = new ConstraintFile(Paths.get("path"));
        ConstraintFile result2 = new ConstraintFile(Paths.get("path"));

        Assert.assertTrue(result1.equals(result2));
    }

    @Test
    public void toStringTest() throws Exception {
        Path path = Paths.get("path");
        ConstraintFile obj = new ConstraintFile(path);

        String result = obj.toString();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("path=" + path.toString()));
    }

}
