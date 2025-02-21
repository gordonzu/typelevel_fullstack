package com.gordonyx.http 

import com.gordonyx.core.*
import com.gordonyx.domain.job.*

import org.http4s.* 
import org.http4s.implicits.* 
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.circe.CirceEntityCodec.*
import io.circe.generic.auto.* 
import cats.*
import cats.effect.* 
import cats.syntax.all.*

class JobRoutes[F[_]: Concurrent] private (jobs: Jobs[F]) extends Http4sDsl[F] {
  private val prefix = "/jobs"

  // post /jobs/create { Job }
  private val createJobRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "create" =>
      for {
        job   <- req.as[Job]
        id    <- jobs.create(job)
        resp  <- Created(id)
      } yield resp
  }

  // get /jobs
  private val getAllRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root => 
      jobs.all.flatMap(jobs => Ok(jobs)) 
  }

  val routes: HttpRoutes[F] = Router(
    prefix -> (createJobRoute <+> getAllRoute)
  )
}


