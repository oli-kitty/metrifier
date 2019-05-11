package metrifier
package rpc

import cats.Applicative
import cats.effect._
import higherkindness.mu.rpc._
import higherkindness.mu.rpc.config.channel.ConfigForAddress
import higherkindness.mu.rpc.protocol.Empty
import metrifier.rpc.protocols._
import metrifier.shared.model._
import metrifier.shared.services

package object server {

  trait ServerConf {

    implicit val getConf: ChannelForAddress =
      ConfigForAddress[IO]("rpc.host", "rpc.port").unsafeRunSync()

  }

  abstract class HandlerImpl[F[_]: Applicative] {
    import cats.syntax.applicative._

    def listPersons(b: Empty.type): F[PersonList] =
      services.listPersons.pure

    def getPerson(id: PersonId): F[Person] =
      services.getPerson(id).pure

    def getPersonLinks(id: PersonId): F[PersonLinkList] =
      services.getPersonLinks(id).pure

    def createPerson(person: Person): F[Person] =
      services.createPerson(person).pure
  }

  class RPCProtoHandler[F[_]: Applicative] extends HandlerImpl[F] with PersonServicePB[F]

  class RPCAvroHandler[F[_]: Applicative] extends HandlerImpl[F] with PersonServiceAvro[F]

  trait CommonImplicits extends PersonServiceRuntime with ServerConf

  object implicits extends CommonImplicits
}