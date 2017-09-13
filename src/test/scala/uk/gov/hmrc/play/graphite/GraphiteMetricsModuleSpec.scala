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
import play.api.inject.guice.GuiceApplicationBuilder

class GraphiteMetricsModuleSpec extends WordSpec with MustMatchers with BeforeAndAfterEach {

  def app: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .bindings(new GraphiteMetricsModule)

  override def beforeEach(): Unit = {
    super.beforeEach()
    SharedMetricRegistries.clear()
  }

  ".bindings" when {

    "`metrics.graphite.legacy` is true" must {

      def app: GuiceApplicationBuilder =
        new GuiceApplicationBuilder()
          .bindings(new GraphiteMetricsModule)

      def verifyLegacyMetricsEnabled(builder: => GuiceApplicationBuilder): Unit = {

        // NOTE: Even though this is being done in the `beforeEach` it doesn't
        // seem to help with mixed in behaviours, so it must be here too.
        SharedMetricRegistries.clear()

        val injector = builder.build().injector

        "have default bindings" in {

          injector.instanceOf[MetricsFilter] mustBe a[MetricsFilterImpl]
          injector.instanceOf[Metrics] mustBe a[GraphiteMetricsImpl]
        }
      }

      def verifyLegacyMetricsDisabled(builder: => GuiceApplicationBuilder): Unit = {

        // NOTE: Even though this is being done in the `beforeEach` it doesn't
        // seem to help with mixed in behaviours, so it must be here too.
        SharedMetricRegistries.clear()

        val injector = builder.build().injector

        "have legacy metrics disabled" in {
          injector.instanceOf[MetricsFilter] mustBe a[DisabledMetricsFilter]
          injector.instanceOf[Metrics] mustBe a[DisabledMetrics]
        }
      }

      "providing no configuration" must {
        behave like verifyLegacyMetricsDisabled(app)
      }

      "setting `metrics.enabled` to true and `microservice.metrics.graphite.enabled` to true" must {
        behave like verifyLegacyMetricsEnabled(
          app.configure("metrics.enabled" -> "true", "microservice.metrics.graphite.enabled" -> "true")
        )
      }

      "setting `metrics.enabled` to false and `microservice.metrics.graphite.enabled` to false" must {
        behave like verifyLegacyMetricsDisabled(
          app.configure("metrics.enabled" -> "false", "microservice.metrics.graphite.enabled" -> "false")
        )

      }

      "setting `metrics.enabled` to false and `microservice.metrics.graphite.enabled` to true" must {
        behave like verifyLegacyMetricsDisabled(
          app.configure("metrics.enabled" -> "false", "microservice.metrics.graphite.enabled" -> "true")
        )
      }

      "setting `metrics.enabled` to true and `microservice.metrics.graphite.enabled` to false" must {
        behave like verifyLegacyMetricsDisabled(
          app.configure("metrics.enabled" -> "true", "microservice.metrics.graphite.enabled" -> "false")
        )

      }
    }

    "`metrics.graphite.legacy` is false" must {

      def app: GuiceApplicationBuilder =
        new GuiceApplicationBuilder()
          .bindings(new GraphiteMetricsModule)
          .configure(
            "microservice.metrics.graphite.legacy" -> "false",
            "microservice.metrics.graphite.host" -> "test",
            "microservice.metrics.graphite.port" -> "9999",
            "appName" -> "test"
          )

      def verifyNonLegacyMetricsEnabled(builder: => GuiceApplicationBuilder): Unit = {

        // NOTE: Even though this is being done in the `beforeEach` it doesn't
        // seem to help with mixed in behaviours, so it must be here too.
        SharedMetricRegistries.clear()

        val injector = builder.build().injector

        "have default bindings" in {

          injector.instanceOf[MetricFilter] mustEqual MetricFilter.ALL
          injector.instanceOf[MetricsFilter] mustBe a[MetricsFilterImpl]
          injector.instanceOf[Metrics] mustBe a[MetricsImpl]
          injector.instanceOf[GraphiteReporting] mustBe a[EnabledGraphiteReporting]
        }
      }

      def verifyNonLegacyMetricsDisabled(app: => GuiceApplicationBuilder) = {
        val injector = app.build().injector

        injector.instanceOf[MetricFilter] mustEqual MetricFilter.ALL
        injector.instanceOf[MetricsFilter] mustBe a[DisabledMetricsFilter]
        injector.instanceOf[Metrics] mustBe a[DisabledMetrics]
        injector.instanceOf[GraphiteReporting] mustBe a[DisabledGraphiteReporting]
      }


      "providing no configuration" must {
        behave like verifyNonLegacyMetricsDisabled(app)
      }

      "setting `metrics.enabled` to true and `microservice.metrics.graphite.enabled` to true" must {
        behave like verifyNonLegacyMetricsEnabled(
          app.configure(
            "metrics.enabled" -> "true",
            "microservice.metrics.graphite.enabled" -> "true"
          )
        )
      }

      "setting `metrics.enabled` to false and `microservice.metrics.graphite.enabled` to true" in {
        behave like verifyNonLegacyMetricsDisabled(app.configure(
          "metrics.enabled" -> "false",
          "microservice.metrics.graphite.enabled" -> "true"
        ))
      }

      "setting `metrics.enabled` to true and `microservice.metrics.graphite.enabled` to false" in {
        behave like verifyNonLegacyMetricsDisabled(app.configure(
          "metrics.enabled" -> "true",
          "microservice.metrics.graphite.enabled" -> "false"
        ))
      }

      "setting `metrics.enabled` to true and `microservice.metrics.graphite.enabled` missing" in {
        behave like verifyNonLegacyMetricsDisabled(app.configure(
          "metrics.enabled" -> "true"
        ))
      }

      "`metrics.enabled` missing and setting `microservice.metrics.graphite.enabled` to true" in {
        behave like verifyNonLegacyMetricsDisabled(app.configure(
          "microservice.metrics.graphite.enabled" -> "true"
        ))
      }

    }
  }

}
