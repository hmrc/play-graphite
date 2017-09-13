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

import java.util.concurrent.TimeUnit

import com.typesafe.config.ConfigException
import org.scalatest.{MustMatchers, WordSpec}
import play.api.Configuration

class GraphiteReporterProviderSpec extends WordSpec with MustMatchers {

  "GraphiteReporterProviderConfig.fromConfig" must {

    "return a valid `GraphiteReporterProviderConfig` when given a prefix" in {

      val rootConfiguration = Configuration("appName" -> "testApp")

      val metricsConfiguration = Configuration("graphite.prefix" -> "test")

      val config: GraphiteReporterProviderConfig =
        GraphiteReporterProviderConfig.fromConfig(rootConfiguration, metricsConfiguration)

      config.prefix mustEqual "test"
      config.rates mustBe None
      config.durations mustBe None
    }

    "return a valid `GraphiteReporterProviderConfig` when given a prefix and optional config" in {

      val rootConfiguration = Configuration()

      val metricsConfiguration = Configuration(
        "graphite.prefix" -> "test",
        "graphite.durations" -> "SECONDS",
        "graphite.rates" -> "SECONDS"
      )

      val config: GraphiteReporterProviderConfig =
        GraphiteReporterProviderConfig.fromConfig(rootConfiguration, metricsConfiguration)

      config.prefix mustEqual "test"
      config.rates mustBe Some(TimeUnit.SECONDS)
      config.durations mustBe Some(TimeUnit.SECONDS)
    }


    "return a valid `GraphiteReporterProviderConfig` when given an appName" in {

      val rootConfiguration = Configuration("appName" -> "testApp")

      val config: GraphiteReporterProviderConfig =
        GraphiteReporterProviderConfig.fromConfig(rootConfiguration, Configuration())

      config.prefix mustEqual "tax.testApp"
      config.rates mustBe None
      config.durations mustBe None
    }

    "throw a configuration exception when relevant keys are missing" in {

      val rootConfiguration = Configuration()

      val exception = intercept[ConfigException.Generic] {
        GraphiteReporterProviderConfig.fromConfig(rootConfiguration, Configuration())
      }

      exception.getMessage mustEqual "`metrics.graphite.prefix` in config or `appName` as parameter required"
    }
  }
}
