package metrifier
package rpc

import cats.effect.{ContextShift, IO, Timer}
import monix.execution.Scheduler

trait PersonServiceRuntime {

  implicit val S: monix.execution.Scheduler = Scheduler.Implicits.global

  implicit val timer: Timer[IO]     = IO.timer(S)
  implicit val cs: ContextShift[IO] = IO.contextShift(S)
}