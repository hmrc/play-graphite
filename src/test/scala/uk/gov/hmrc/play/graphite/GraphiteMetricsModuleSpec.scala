/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.play.graphite

import com.codahale.metrics.{MetricFilter, SharedMetricRegistries}
import com.kenshoo.play.metrics._
import org.scalatest._
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder

class GraphiteMetricsModuleSpec extends WordSpec with MustMatchers with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    super.beforeEach()
    SharedMetricRegistries.clear()
  }

  ".bindings" when {

    "`$env.microservice.metrics.graphite.legacy` is true" when {

      "`$env.microservice.metrics.enabled` is not set" must {
        behave like haveLegacyBindings(Configuration())
      }

      "`metrics.enabled` is set to true" must {
        behave like haveLegacyBindings(Configuration("Test.microservice.metrics.enabled" -> "true"))
      }

      "`metrics.enabled` is set to false" must {

        val injector = buildInjectorWithMetrics(Configuration("Test.microservice.metrics.enabled" -> "false"))

        "create legacy bindings with reporting disabled" in {
          injector.instanceOf[MetricsFilter] mustBe a[DisabledMetricsFilter]
          injector.instanceOf[Metrics] mustBe a[DisabledMetrics]
        }
      }
    }

    "`$env.metrics.graphite.legacy` is false" when {

      val graphiteConfiguration = Configuration(
        "Test.microservice.metrics.graphite.legacy" -> "false",
        "Test.microservice.metrics.graphite.host" -> "test",
        "Test.microservice.metrics.graphite.port" -> "9999",
        "appName" -> "test"
      )

      "`$env.metrics.enabled` is not set" must {
        behave like haveDefaultBindings(graphiteConfiguration)
      }

      "`$env.metrics.enabled` is set to true" must {
        behave like haveDefaultBindings(graphiteConfiguration ++ Configuration("Test.microservice.metrics.enabled" -> "true"))
      }

      "`$env.metrics.enabled` is set to false" must {
        behave like haveDisabledNonLegacyMetrics(graphiteConfiguration ++ Configuration("Test.microservice.metrics.enabled" -> "false"))
      }

    }

    " environment specific configuration is not set but default configuration is provided " when {

      val graphiteConfiguration = Configuration(
        "microservice.metrics.graphite.legacy" -> "false",
        "microservice.metrics.graphite.host" -> "test",
        "microservice.metrics.graphite.port" -> "9999",
        "appName" -> "test"
      )

      "`metrics.enabled` is not set" must {
        behave like haveDefaultBindings(graphiteConfiguration)
      }

      "`metrics.enabled` is set to true" must {
        behave like haveDefaultBindings(graphiteConfiguration ++ Configuration("microservice.metrics.enabled" -> "true"))
      }

      "`metrics.enabled` is set to false" must {
        behave like haveDisabledNonLegacyMetrics(graphiteConfiguration ++ Configuration("microservice.metrics.enabled" -> "false"))
      }

    }

    " both environment configuration and default configuration specified" when {
      val graphiteConfiguration = Configuration(
        "Test.microservice.metrics.graphite.legacy" -> "false",
        "microservice.metrics.graphite.legacy" -> "true",
        "Test.microservice.metrics.graphite.host" -> "test",
        "Test.microservice.metrics.graphite.port" -> "9999",
        "appName" -> "test"
      )

      "`metrics.enabled` is not set" must {
        behave like haveDefaultBindings(graphiteConfiguration)
      }

      "`metrics.enabled` is set to true" must {
        behave like haveDefaultBindings(graphiteConfiguration ++ Configuration("Test.microservice.metrics.enabled" -> "true"))
      }

      "`metrics.enabled` is set to false" must {
        behave like haveDisabledNonLegacyMetrics(graphiteConfiguration ++ Configuration("Test.microservice.metrics.enabled" -> "false"))
      }
    }

    def buildInjectorWithMetrics(config : Configuration) = {

      // NOTE: Even though this is being done in the `beforeEach` it doesn't
      // seem to help with mixed in behaviours, so it must be here too.
      SharedMetricRegistries.clear()

      new GuiceApplicationBuilder()
        .bindings(new GraphiteMetricsModule)
        .configure(config)
        .build()
        .injector
    }

    def haveLegacyBindings(config : Configuration): Unit = {
      val injector = buildInjectorWithMetrics(config)

      "create legacy bindings" in {
        injector.instanceOf[MetricsFilter] mustBe a[MetricsFilterImpl]
        injector.instanceOf[Metrics] mustBe a[GraphiteMetricsImpl]
      }
    }

    def haveDefaultBindings(configuration : Configuration): Unit = {
      val injector = buildInjectorWithMetrics(configuration)

      "create new bindings" in {

        injector.instanceOf[MetricFilter] mustEqual MetricFilter.ALL
        injector.instanceOf[MetricsFilter] mustBe a[MetricsFilterImpl]
        injector.instanceOf[Metrics] mustBe a[MetricsImpl]
        injector.instanceOf[GraphiteReporting] mustBe a[EnabledGraphiteReporting]
      }
    }

    def haveDisabledNonLegacyMetrics(configuration : Configuration): Unit = {
      val injector = buildInjectorWithMetrics(configuration)

      "create new bindings with reporting disabled" in {
        injector.instanceOf[MetricFilter] mustEqual MetricFilter.ALL
        injector.instanceOf[MetricsFilter] mustBe a[DisabledMetricsFilter]
        injector.instanceOf[Metrics] mustBe a[DisabledMetrics]
        injector.instanceOf[GraphiteReporting] mustBe a[DisabledGraphiteReporting]
      }
    }
  }
}
