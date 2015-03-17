play-graphite
=============

A library that can hook to a Play Global object to start the Graphite reporter.

For this to work, we also need to use the [MetricsPlugin](https://github.com/kenshoo/metrics-play) which actually sends the metrics data to a MetricsRepository. The GraphiteReporter then picks it up from there and sends the data to the Graphite server.

## Adding to your service

Include the following dependency in your SBT build

```scala
resolvers += Resolver.bintrayRepo("hmrc", "releases")

libraryDependencies += "uk.gov.hmrc" %% "play-graphite" % "1.0.0"
```

## Required configuration

This library shares a part of its configuration with the MetricsPlugin (in the configuration root), namely

```javascript
metrics {
    name = metrics-repository-name
    enabled = true
}```

It also has its own configuration at a location given by overriding `microserviceMetricsConfig`

```javascript
metrics {
    graphite {
        host = graphite
        port = 2003
        prefix = appMetricsPrefix
        enabled = true
    }
}```
