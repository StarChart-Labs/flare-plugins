# Plug-ins

Flare provides general conventions by mixing and matching a set of behaviors provided by individual plug-ins. This allows those whose builds do not precisely match a pre-defined convention to still make use of Flare's functionality

## org.starchartlabs.flare.dependency-constraints

The dependency-constraints plug-in allows defining version constraints for use across all sub-modules and configurations in a single properties file. This is done by adding a DSL to the project which allows specifying the file to read from.

### DSL

The DSL is named `dependencyConstraints`, and has a single `File` argument which references the file to read

```
dependencyConstraints {
	file file('example.properties')
}
```

### File Format

The file(s) currently supported by the plug-in follow a simple format. Lines may be empty, a comment, or a version definition. Comment lines start with the `#` character. Version lines are a GAV in the format `group:artifact:version`, and may optionally include the configurations the constraint should be limited to as comma-separated values (`group:artifact:version,config1,config2,...`)

## org.starchartlabs.flare.increased-test-logging

The increased-test-logging plug-in increases the test output for the Gradle test task. It is the equivalent of the boilerplate:

```
test {
    testLogging{
        exceptionFormat 'full'
        
        quiet{
            events 'failed', 'skipped'
        }
        
        info{
            events 'failed', 'skipped', 'passed', 'standard_out', 'standard_error'
        }
        
        debug{
            events 'failed', 'skipped', 'passed', 'standard_out', 'standard_error', 'started'
        }
    }
}
```

## org.starchartlabs.flare.managed-credentials

The managed-credentials plug-in adds a configuration DSL for defining re-usable username/password sets within the build. Credentials may be setup to read from multiple (ordered) sources. No attempt is made to read credentials until they are actively referenced within the build. Each ordered source is checked for both a username and password value, and the first source to provide both is used.

### DSL

The DSL is named `credentials`, and allows defining multiple named credential sets. Each named set has support for credentials via:

- `environment`, which reads a username and password from environment variables
- `systemProperties`, which reads a username and password from system properties
- `defaultCredentials`, which allows hard-coding a set of values. *It is HIGHLY recommended to avoid coding credentials into your build files directly*

```
credentials {
    example1 {
        environment('ENV1', 'ENV2')
        systemProperties('example_user', 'example_key')
        defaultLogin('defaultuser', 'defaultpassword')
    }
    
    example1 {
        environment('ENV3', 'ENV4')
    }
}
```

### Use

Created credentials may be reference via `${credentials.name1.username}` and `${credentials.name1.password}`

## org.starchartlabs.flare.merge-coverage-reports

The merge-coverage-reports plug-in adds a task which generates a single XML Jacoco report which includes coverage information from reports in all sub-modules. The added task (`mergeCoverageReports`), depends on the `test` tasks of all sub-modules, and is added as a dependency of the `check` task of the root project

## org.starchartlabs.flare.source-jars

The source-jars plug-in adds two tasks of type `Jar` - `sourcesJar`, which generates a jar file with the source files of the project, and `javadocJar`, which generates a jar file with the javadoc files of the project. These tasks are added to the `archives` artifact configuration of the project, and if the `maven-publish` plug-in is used, they are added as artifacts with the `sources` and `javadoc` classifiers.

These jars are intended to be uploaded alongside binary artifacts for open-source projects. They allow developers to trace through the associated code paths when debugging, and to view associated Javadoc documentation within their IDE.

## org.starchartlabs.flare.metadata-base

The metadata-base plug-in adds a configuration DSL for defining re-usable project meta data values within the build. The primary intended use for these values is as properties on generated artifacts

### DSL

The DSL is named `projectMetaData`, and allows defining information such as source control management locations, licensing, and individuals associated with the repository

Meta data can be defined manually, or via GitHub conventions:

#### Manual DSL

```
projectMetaData{
	url = 'http://...'
	
	scm {
		vcsUrl = 'http://...'
		connection = 'http://...'
		developerConnection = 'http://..'
	}
	
	developers {
		developer 'id', 'name', 'http://...'
	}
	
	contributors {
		contributor 'name', 'http://...'
	}
	
	licenses {
		mit('repo')
	}
}
```

#### GitHub DSL

```
projectMetaData{
	github {
		repository 'owner', 'repository'
		
		developer 'octocat', 'Octocat'
		contributor 'hue'
	}
	
	licenses {
		mit('repo')
	}
}
```

In addition to the standard DSL within the build file, the `github` configuration DSL allows loading developers and contributors from an external file. This file may have blank links, comment lines (starting with `#`), and configuration lines. Configuration lines are of the form `username` or `username, name`

#### License Meta Data

Regradless of use of the GitHub configuration DSL, meta data configuration includes specification of the project license. This can be done manually by providing name, tag, url, and maven distribution method (`repo` or `manual`), or by using a pre-specified license. Currently, the following licenses are pre-configured in teh DSL definition:

* Apache 2.0 (`apache2`)
* MIT (`mit`)
* Eclipse Public License 1.0 (`epl`)

All licenses have a DSL which omits distribution (defaulting to `repo`), and a DSL which accepts a distribution specification. Pull requests to add additional pre-configured licenses are welcome!

## org.starchartlabs.flare.metadata-pom

The meta data POM plug-in takes data defined in the DSL defined by the `org.starchartlabs.flare.metadata-base` plug-in and applies it to the generated POM file for any `MavenPublication` definitions provided by the `maven-publish` plug-in
