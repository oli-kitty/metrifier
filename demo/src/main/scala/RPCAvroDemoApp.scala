package metrifier
package demo

import cats.effect.{IO, Resource}
import higherkindness.mu.rpc.protocol.Empty
import metrifier.demo.Utils._
import metrifier.rpc.client.implicits._
import metrifier.rpc.protocols.PersonServiceAvro
import metrifier.shared.model._

object RPCAvroDemoApp {

  def clientProgram(implicit client: Resource[IO, PersonServiceAvro[IO]]): IO[PersonAggregation] = {
    client.use(c =>
      for {
        personList <- c.listPersons(Empty)
        p1         <- c.getPerson(PersonId("1"))
        p2         <- c.getPerson(PersonId("2"))
        p3         <- c.getPerson(PersonId("3"))
        p4         <- c.getPerson(PersonId("4"))
        p1Links    <- c.getPersonLinks(PersonId(p1.id))
        p3Links    <- c.getPersonLinks(PersonId(p3.id))
        pNew       <- c.createPerson(person)
      } yield (p1, p2, p3, p4, p1Links, p3Links, personList.add(pNew)))
  }

  def main(args: Array[String]): Unit = {

    val result: PersonAggregation =
      clientProgram.unsafeRunSync()

    println(s"Result = $result")

  }

}
