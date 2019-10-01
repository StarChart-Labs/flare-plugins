# Migrating from flare-operations-plugins

Previously, StarChart Labs provided the flare-operations-plugins project for a sub-set of the Gradle plug-ins which have since been moved into the flare-plugins project. This guide will help transition from the old project to flare-plugins

It is recommended that users evaluate if one of the pre-defined conventions fits their needs - using one of these conventions is the recommended use pattern. If not, individual plug-ins can be migrated as follows:

## org.starchartlabs.flare.dependency-insight and org.starchartlabs.flare.dependency-reporting

Both of these plug-ins have been replaced by the default `dependencies` and `dependencyInsight` Gradle tasks. The `dependencyProjectInsight` task has not been re-implemented due to a lack of use - please feel free to file an issue if this functionality is desired

## org.starchartlabs.flare.dependency-versions

Two changes aside from updating the referenced library in the classpath are required. First, the plug-in ID has changed to `org.starchartlabs.flare.dependency-constraints`. Second, the DSL has changed to:

```
dependencyConstraints {
	file file('example.properties')
}
```

## org.starchartlabs.flare.increase-test-logging

Aside from switching the imported library, the plug-in ID has changed slightly to `org.starchartlabs.flare.increased-test-logging`

## org.starchartlabs.flare.managed-credentials

One change aside from updating the referenced library in the classpath is required. The `defaultLogin` function within the `credentials` DSL has been renamed to `defaultCredentials`. The plug-in ID is unchanged

## org.starchartlabs.flare.merge-coverage-reports

No change aside from switch the imported library is required - the plug-in ID is unchanged
