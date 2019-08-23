/*
 * Copyright (C) 2019 StarChart-Labs@github.com Authors
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */
package org.starchartlabs.flare.plugins.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.starchartlabs.flare.plugins.model.ProjectMetaData;

/**
 * Configuration plug-in that adds structures for defining project meta data which can be referenced throughout the
 * build system, primarily as properties on published elements
 *
 * @author romeara
 * @since 0.2.0
 */
public class MetaDataBasePlugin implements Plugin<Project> {

    private static final String DSL_EXTENSION = "projectMetaData";

    @Override
    public void apply(Project project) {
        project.getExtensions().add(DSL_EXTENSION, new ProjectMetaData());
    }

}
