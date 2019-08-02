# Migrating from flare-publishing-plugins

Previously, StarChart Labs provided the flare-publishing-plugins project for a sub-set of the Gradle plug-ins which have since been moved into the flare-plugins project. This guide will help transition from the old project to flare-plugins

It is recommended that users evaluate if one of the pre-defined conventions fits their needs - using one of these conventions is the recommended use pattern. If not, individual plug-ins can be migrated as follows:

## org.starchartlabs.flare.source-jars

No change aside from switch the imported library is required - the plug-in ID is unchanged

## org.starchartlabs.flare.pom-source-jar-artifacts

The functionality of the `pom-source-jar-artifacts` plug-in has been combined with the `source-jars` plug-in, and will be conditionally applied if the `maven-publish` plug-in is used
