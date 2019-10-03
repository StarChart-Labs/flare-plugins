# flare-plugins

[![Travis CI](https://img.shields.io/travis/com/StarChart-Labs/flare-plugins.svg?branch=master)](https://travis-ci.com/StarChart-Labs/flare-plugins) [![Code Coverage](https://img.shields.io/codecov/c/github/StarChart-Labs/flare-plugins.svg)](https://codecov.io/github/StarChart-Labs/flare-plugins) [![Black Duck Security Risk](https://copilot.blackducksoftware.com/github/repos/StarChart-Labs/flare-plugins/branches/master/badge-risk.svg)](https://copilot.blackducksoftware.com/github/repos/StarChart-Labs/flare-plugins/branches/master) [![Changelog validated by Chronicler](https://chronicler.starchartlabs.org/images/changelog-chronicler-success.png)](https://chronicler.starchartlabs.org/) [![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

Gradle plug-ins with opinionated defaults to allow streamlined, consistent operations

## Contributing

Information for how to contribute can be found in [the contribution guidelines](./docs/CONTRIBUTING.md)

## Legal

The Flare Operations Plug-ins are distributed under the [MIT License](https://opensource.org/licenses/MIT). There are no requirements for using it in your own project (a line in a NOTICES file is appreciated but not necessary for use)

The requirement for a copy of the license being included in distributions is fulfilled by a copy of the [LICENSE](./LICENSE) file being included in constructed JAR archives

## Reporting Vulnerabilities

If you discover a security vulnerability, contact the development team by e-mail at `vulnerabilities@starchartlabs.org`

## Gradle Compatibility

For all current releases, the minimum required Gradle version is 5.0

## Major Version Changes

When a major version is released, this is usually due to changes which are not backwards compatible. See [the major version migration guide](./docs/MAJOR_VERSION_MIGRATION_GUIDE.md) for information on adjustments which will be necesssary for your build to upgrade between major versions

## Use

The intended use for `flare-plugins` involves selecting a set of conventions for common uses, implemented as a single plug-in. However, individual plug-ins can still be applied if none of the current pre-defined conventions meet your use case. Instructions for each individual plug-in can be found [here](./docs/PLUGINS.md).

Currently, the only pre-defined convention available is `multi-module-library`

### Convention: multi-module-library

The `multi-module-library` convention is intended for gradle projects which define a set of related libraries as modules of a root project. This convention assumes that the root project is present mainly for overall build configuration, and that library code is all in sub-modules. This convention can be applied via the configuration documented in the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/org.starchartlabs.flare.multi-module-library)

Applying this convention has the following effects:

- Applies a task to the root project which will create a merged Jacoco XML report in `${rootProject.buildDir}/reports/jacoco/report.xml`
- Reads dependency versions from file `"${rootDir}/dependencies.properties"` and applies these as dependency constraints
  - This file may have empty lines, lines starting with `#` which act as comments, or lines of the form `group:artifact:version[,configurations,...]`
- Add a reference to credentials read from environment variables `BINTRAY_USER` and `BINTRAY_API_KEY`. These variables must be defined if the credentials are used
  - These credentials can be referenced by `${credentials.bintray.username}` and `${credentials.bintray.password}`
- Increases the logging level of test events in sub-modules
- Adds tasks to generate sources and javadoc jars, and adds them to the project's `archives` artifact configuration
- Adds DSL for configuration project meta data
- Loads developers into project meta data from file `"${rootDir}/developers.properties"`, if it exists
- Loads contributors into project meta data from file `"${rootDir}/contributors.properties"`, if it exists
- Applies project meta data to generated Maven POM files

Individual plug-ins used to apply these behaviors:

- org.starchartlabs.flare.dependency-constraints
- org.starchartlabs.flare.increased-test-logging
- org.starchartlabs.flare.managed-credentials
- org.starchartlabs.flare.bintray-credentials
- org.starchartlabs.flare.merge-coverage-reports
- org.starchartlabs.flare.source-jars
- org.starchartlabs.flare.metadata-base
- org.starchartlabs.flare.metadata-pom

## Migrating From Previous Plug-ins

StarChart Labs previously provided two libraries for Gradle plug-ins. Migration guides are provided for the following projects:

- [flare-operations-plugins](./docs/FLARE_OPERATIONS_MIGRATION.md)
- [flare-publishing-plugins](./docs/FLARE_PUBLISHING_MIGRATION.md)
