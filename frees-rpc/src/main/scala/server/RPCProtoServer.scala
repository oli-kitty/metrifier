package metrifier
package rpc
package server

import cats.effect.IO
import higherkindness.mu.rpc.server.{AddService, GrpcConfig, GrpcServer}
import org.log4s.Logger
import metrifier.rpc.server.implicits._
import higherkindness.mu.rpc.config.server.BuildServerFromConfig
import metrifier.rpc.protocols.PersonServicePB
import cats.instances.list._
import cats.syntax.traverse._

object RPCProtoServer {

  val logger: Logger = org.log4s.getLogger

  implicit private val personServicePBHandler: RPCProtoHandler[IO] = new RPCProtoHandler[IO]

  def main(args: Array[String]): Unit = {

    logger.info(s"Server is starting ...")

    val grpcConfig: IO[List[GrpcConfig]] = List(PersonServicePB.bindService[IO].map(AddService)).sequence

    val runServer: IO[Unit] = for {
      config <- grpcConfig
      server <- BuildServerFromConfig[IO]("rpc.server.port", config)
      _      <- GrpcServer.server[IO](server)
    } yield ()

    runServer.unsafeRunSync
  }

}
