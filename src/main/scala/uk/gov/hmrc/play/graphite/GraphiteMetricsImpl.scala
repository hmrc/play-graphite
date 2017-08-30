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

import javax.inject.{Inject, Singleton}

import com.codahale.metrics
import com.codahale.metrics.MetricRegistry
import com.kenshoo.play.metrics.MetricsImpl
import play.api.Configuration
import play.api.inject.ApplicationLifecycle

/**
  * Class prevents the default behaviour of removing shared registries when the application stops
  * @param lifecycle
  * @param configuration
  */
@deprecated("This class prevents correct application shutdown processes and should be avoided, use `MetricsImpl` instead", "-")
@Singleton
class GraphiteMetricsImpl @Inject() (
                                      lifecycle: ApplicationLifecycle,
                                      configuration: Configuration
                                    ) extends MetricsImpl(lifecycle, configuration) {
  override def onStop() = {}
}
