/*
 * Copyright 2015 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.play.graphite


import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit._

import com.codahale.metrics.graphite.{Graphite, GraphiteReporter}
import com.codahale.metrics.{MetricFilter, SharedMetricRegistries}
import play.api.{Application, Configuration, GlobalSettings, Logger}

trait GraphiteConfig extends GlobalSettings {

  def microserviceMetricsConfig(implicit app: Application) : Option[Configuration]

  private def metricsConfig(implicit app: Application) = microserviceMetricsConfig
    .getOrElse(app.configuration.getConfig(s"Dev.microservice.metrics")
    .getOrElse(throw new Exception("The application does not contain required metrics configuration")))

  override def onStart(app: Application) {

    super.onStart(app)

    if (enabled(app)) {
      startGraphite(app)
    }
  }

  override def onStop(app: Application) {
    super.onStop(app)
  }

  private def enabled(app: Application) : Boolean =  app.configuration.getBoolean("metrics.enabled").getOrElse(false) &&
      metricsConfig(app).getBoolean("graphite.enabled").getOrElse(false)

  private def registryName(app: Application) = app.configuration.getString("metrics.name").getOrElse("default")

  private def startGraphite(implicit app: Application) {
    Logger.info("Graphite metrics enabled, starting the reporter")

    val graphite = new Graphite(new InetSocketAddress(
      metricsConfig.getString("graphite.host").getOrElse("graphite"),
      metricsConfig.getInt("graphite.port").getOrElse(2003)))

    val prefix = metricsConfig.getString("graphite.prefix").getOrElse(s"tax.${app.configuration.getString("appName")}")

    val reporter = GraphiteReporter.forRegistry(
      SharedMetricRegistries.getOrCreate(registryName(app)))
      .prefixedWith(s"$prefix.${java.net.InetAddress.getLocalHost.getHostName}")
      .convertRatesTo(SECONDS)
      .convertDurationsTo(MILLISECONDS)
      .filter(MetricFilter.ALL)
      .build(graphite)

    reporter.start(metricsConfig.getLong("graphite.interval").getOrElse(10L), SECONDS)
  }
}
