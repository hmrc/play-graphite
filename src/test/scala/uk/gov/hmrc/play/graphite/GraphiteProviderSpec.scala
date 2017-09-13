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

import com.typesafe.config.ConfigException
import org.scalatest.{MustMatchers, WordSpec}
import play.api.Configuration

class GraphiteProviderSpec extends WordSpec with MustMatchers {

  "GraphiteProviderConfigSpec.fromConfig" must {

    val metricsConfiguration: Map[String, Any] = Map(
      "graphite.host" -> "localhost",
      "graphite.port" -> "9999"
    )

    "return a valid `GraphiteProviderConfig`" in {

      val config: GraphiteProviderConfig =
        GraphiteProviderConfig.fromConfig(Configuration.from(metricsConfiguration))

      config.host mustEqual "localhost"
      config.port mustEqual 9999
    }

    metricsConfiguration.keys.foreach {
      missingKey =>
        s"throw a configuration exception when config key: $missingKey, is missing" in {

          intercept[ConfigException.Missing] {
            GraphiteProviderConfig.fromConfig(Configuration.from(metricsConfiguration - missingKey))
          }
        }
    }
  }
}
