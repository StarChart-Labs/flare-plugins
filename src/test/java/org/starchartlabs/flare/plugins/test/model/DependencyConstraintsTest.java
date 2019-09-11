/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.test.model;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencyConstraint;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.dsl.DependencyConstraintHandler;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.internal.artifacts.dependencies.DependencyConstraintInternal;
import org.gradle.api.logging.Logger;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.starchartlabs.flare.plugins.model.DependencyConstraints;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DependencyConstraintsTest {

    private File file;

    @BeforeClass
    public void setupFile() throws Exception {
        // 1-3 are setup in tests as "used" dependencies, 4 simulates an unused constraint that should not be applied
        List<String> lines = new ArrayList<>();
        lines.add("group1:artifact1:1.0");
        lines.add("group2:artifact2:2.0");
        lines.add("group3:artifact3:3.0");
        lines.add("group4:artifact4:4.0");

        Path path = Files.createTempFile("constraintFileTest", "no-discards");

        Files.write(path, lines);

        file = path.toFile();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void constructNullProject() throws Exception {
        new DependencyConstraints(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void fileNullFile() throws Exception {
        Project project = Mockito.mock(Project.class);

        DependencyConstraints dependencyConstraints = new DependencyConstraints(project);

        dependencyConstraints.file(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void fileDoesntExist() throws Exception {
        Project project = Mockito.mock(Project.class);

        DependencyConstraints dependencyConstraints = new DependencyConstraints(project);

        dependencyConstraints.file(Paths.get("does-not-exist").toFile());
    }

    // Suppressing unchecked as its a product of mocking generics
    @Test
    @SuppressWarnings("unchecked")
    public void fileUnforcibleConstraints() throws Exception {
        Project project = Mockito.mock(Project.class);
        Logger logger = Mockito.mock(Logger.class);
        ConfigurationContainer configurationContainer = Mockito.mock(ConfigurationContainer.class);
        Configuration configuration = Mockito.mock(Configuration.class);
        DependencyHandler dependencyHandler = Mockito.mock(DependencyHandler.class);
        DependencyConstraintHandler dependencyConstraintHandler = Mockito.mock(DependencyConstraintHandler.class);
        DependencyConstraint constraint = Mockito.mock(DependencyConstraint.class);

        Dependency dependencyOne = getMockDependency("group1", "artifact1");
        Dependency dependencyTwo = getMockDependency("group2", "artifact2");
        Dependency dependencyThree = getMockDependency("group3", "artifact3");

        DependencySet dependencySet = Mockito.mock(DependencySet.class);
        Mockito.when(dependencySet.stream()).thenReturn(Stream.of(dependencyOne, dependencyTwo, dependencyThree));

        Mockito.when(project.getLogger()).thenReturn(logger);
        Mockito.when(project.getConfigurations()).thenReturn(configurationContainer);
        Mockito.when(configuration.getName()).thenReturn("configName");
        Mockito.when(project.getDependencies()).thenReturn(dependencyHandler);
        Mockito.when(dependencyHandler.getConstraints()).thenReturn(dependencyConstraintHandler);
        Mockito.when(constraint.getName()).thenReturn("constraintName");

        ArgumentCaptor<Action<Configuration>> configurationActionCapture = ArgumentCaptor.forClass(Action.class);
        ArgumentCaptor<Action<DependencySet>> dependencySetActionCapture = ArgumentCaptor.forClass(Action.class);
        ArgumentCaptor<Action<DependencyConstraint>> constraintActionsCapture = ArgumentCaptor.forClass(Action.class);

        DependencyConstraints dependencyConstraints = new DependencyConstraints(project);

        dependencyConstraints.file(file);

        Mockito.verify(project).getConfigurations();
        Mockito.verify(configurationContainer).all(configurationActionCapture.capture());


        List<Action<Configuration>> configurationActions = configurationActionCapture.getAllValues();

        Assert.assertEquals(configurationActions.size(), 1);
        configurationActions.get(0).execute(configuration);

        Mockito.verify(configuration).withDependencies(dependencySetActionCapture.capture());

        List<Action<DependencySet>> dependencySetActions = dependencySetActionCapture.getAllValues();

        Assert.assertEquals(dependencySetActions.size(), 1);
        dependencySetActions.get(0).execute(dependencySet);

        Mockito.verify(dependencySet).stream();
        Mockito.verify(dependencyOne).getGroup();
        Mockito.verify(dependencyOne).getName();
        Mockito.verify(dependencyTwo).getGroup();
        Mockito.verify(dependencyTwo).getName();
        Mockito.verify(dependencyThree).getGroup();
        Mockito.verify(dependencyThree).getName();

        Mockito.verify(project, Mockito.times(3)).getDependencies();
        Mockito.verify(dependencyHandler, Mockito.times(3)).getConstraints();
        Mockito.verify(configuration, Mockito.times(3)).getName();
        Mockito.verify(logger).info("Applied {} dependency constraint: {}", configuration, "group1:artifact1:1.0");
        Mockito.verify(logger).info("Applied {} dependency constraint: {}", configuration, "group2:artifact2:2.0");
        Mockito.verify(logger).info("Applied {} dependency constraint: {}", configuration, "group3:artifact3:3.0");

        Mockito.verify(dependencyConstraintHandler).add(Mockito.eq("configName"), Mockito.eq("group1:artifact1:1.0"),
                constraintActionsCapture.capture());
        Mockito.verify(dependencyConstraintHandler).add(Mockito.eq("configName"), Mockito.eq("group2:artifact2:2.0"),
                constraintActionsCapture.capture());
        Mockito.verify(dependencyConstraintHandler).add(Mockito.eq("configName"), Mockito.eq("group3:artifact3:3.0"),
                constraintActionsCapture.capture());

        List<Action<DependencyConstraint>> constraintActions = constraintActionsCapture.getAllValues();

        Assert.assertEquals(constraintActions.size(), 3);

        // Verify one of the actions
        constraintActions.get(0).execute(constraint);

        Mockito.verify(logger).debug("Unable to force constraint {}", constraint.getName());
    }

    // Suppressing unchecked as its a product of mocking generics
    @Test
    @SuppressWarnings("unchecked")
    public void fileForcibleContraints() throws Exception {
        Project project = Mockito.mock(Project.class);
        Logger logger = Mockito.mock(Logger.class);
        ConfigurationContainer configurationContainer = Mockito.mock(ConfigurationContainer.class);
        Configuration configuration = Mockito.mock(Configuration.class);
        DependencyHandler dependencyHandler = Mockito.mock(DependencyHandler.class);
        DependencyConstraintHandler dependencyConstraintHandler = Mockito.mock(DependencyConstraintHandler.class);
        DependencyConstraintInternal constraint = Mockito.mock(DependencyConstraintInternal.class);

        Dependency dependencyOne = getMockDependency("group1", "artifact1");
        Dependency dependencyTwo = getMockDependency("group2", "artifact2");
        Dependency dependencyThree = getMockDependency("group3", "artifact3");

        DependencySet dependencySet = Mockito.mock(DependencySet.class);
        Mockito.when(dependencySet.stream()).thenReturn(Stream.of(dependencyOne, dependencyTwo, dependencyThree));

        Mockito.when(project.getLogger()).thenReturn(logger);
        Mockito.when(project.getConfigurations()).thenReturn(configurationContainer);
        Mockito.when(configuration.getName()).thenReturn("configName");
        Mockito.when(project.getDependencies()).thenReturn(dependencyHandler);
        Mockito.when(dependencyHandler.getConstraints()).thenReturn(dependencyConstraintHandler);
        Mockito.when(constraint.getName()).thenReturn("constraintName");

        ArgumentCaptor<Action<Configuration>> configurationActionCapture = ArgumentCaptor.forClass(Action.class);
        ArgumentCaptor<Action<DependencySet>> dependencySetActionCapture = ArgumentCaptor.forClass(Action.class);
        ArgumentCaptor<Action<DependencyConstraint>> constraintActionsCapture = ArgumentCaptor.forClass(Action.class);

        DependencyConstraints dependencyConstraints = new DependencyConstraints(project);

        dependencyConstraints.file(file);

        Mockito.verify(project).getConfigurations();
        Mockito.verify(configurationContainer).all(configurationActionCapture.capture());

        List<Action<Configuration>> configurationActions = configurationActionCapture.getAllValues();

        Assert.assertEquals(configurationActions.size(), 1);
        configurationActions.get(0).execute(configuration);

        Mockito.verify(configuration).withDependencies(dependencySetActionCapture.capture());

        List<Action<DependencySet>> dependencySetActions = dependencySetActionCapture.getAllValues();

        Assert.assertEquals(dependencySetActions.size(), 1);
        dependencySetActions.get(0).execute(dependencySet);

        Mockito.verify(dependencySet).stream();
        Mockito.verify(dependencyOne).getGroup();
        Mockito.verify(dependencyOne).getName();
        Mockito.verify(dependencyTwo).getGroup();
        Mockito.verify(dependencyTwo).getName();
        Mockito.verify(dependencyThree).getGroup();
        Mockito.verify(dependencyThree).getName();

        Mockito.verify(project, Mockito.times(3)).getDependencies();
        Mockito.verify(dependencyHandler, Mockito.times(3)).getConstraints();
        Mockito.verify(configuration, Mockito.times(3)).getName();
        Mockito.verify(logger).info("Applied {} dependency constraint: {}", configuration, "group1:artifact1:1.0");
        Mockito.verify(logger).info("Applied {} dependency constraint: {}", configuration, "group2:artifact2:2.0");
        Mockito.verify(logger).info("Applied {} dependency constraint: {}", configuration, "group3:artifact3:3.0");

        Mockito.verify(dependencyConstraintHandler).add(Mockito.eq("configName"), Mockito.eq("group1:artifact1:1.0"),
                constraintActionsCapture.capture());
        Mockito.verify(dependencyConstraintHandler).add(Mockito.eq("configName"), Mockito.eq("group2:artifact2:2.0"),
                constraintActionsCapture.capture());
        Mockito.verify(dependencyConstraintHandler).add(Mockito.eq("configName"), Mockito.eq("group3:artifact3:3.0"),
                constraintActionsCapture.capture());

        List<Action<DependencyConstraint>> constraintActions = constraintActionsCapture.getAllValues();

        Assert.assertEquals(constraintActions.size(), 3);

        // Verify one of the actions
        constraintActions.get(0).execute(constraint);

        Mockito.verify(constraint).setForce(true);
        Mockito.verify(logger).debug("Forced constraint {}", constraint.getName());
    }

    // TODO test for skipping unused constraints (or build into above)

    private Dependency getMockDependency(String group, String artifact) {
        Dependency result = Mockito.mock(Dependency.class);
        Mockito.when(result.getGroup()).thenReturn(group);
        Mockito.when(result.getName()).thenReturn(artifact);

        return result;
    }

}
