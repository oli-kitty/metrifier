package metrifier
package benchmark

import cats.effect.{IO, Resource}
import higherkindness.mu.rpc.protocol.Empty
import java.util.concurrent.TimeUnit
import metrifier.benchmark.Utils._
import metrifier.rpc.client.implicits._
import metrifier.rpc.protocols._
import metrifier.shared.model._
import org.openjdk.jmh.annotations._

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
class RPCAvroBenchmark {

  val client: Resource[IO, PersonServiceAvro[IO]] = implicitly[Resource[IO, PersonServiceAvro[IO]]]

  @Benchmark
  def listPersons: PersonList = client.use(_.listPersons(Empty)).unsafeRunTimed(defaultTimeOut).get

  @Benchmark
  def getPerson: Person = client.use(_.getPerson(PersonId("1"))).unsafeRunTimed(defaultTimeOut).get

  @Benchmark
  def getPersonLinks: PersonLinkList =
    client.use(_.getPersonLinks(PersonId("1"))).unsafeRunTimed(defaultTimeOut).get

  @Benchmark
  def createPerson: Person =
    client.use(_.createPerson(person)).unsafeRunTimed(defaultTimeOut).get

  @Benchmark
  def programComposition: PersonAggregation = {

    def clientProgram: IO[PersonAggregation] = {
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

    clientProgram.unsafeRunTimed(defaultTimeOut).get
  }

}
