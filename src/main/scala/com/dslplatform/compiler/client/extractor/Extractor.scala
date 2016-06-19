package com.dslplatform.compiler.client.extractor

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import java.time.{ZoneId, ZonedDateTime}
import slick.jdbc.GetResult

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import scalax.file._

object Extractor extends App {
  if (args.length < 2) {
    println("Usage: java -jar dsl-migrations-extrator.jar [jdbcUrl] [outputDir]")
    sys.exit(1)
  }

  val jdbcUrl = args(0)
  val outputDir = args(1)

  val logger = Logger(LoggerFactory.getLogger("dsl-migrations-extrator"))
  logger.info("Starting extractor ...")

  val postgresApi = {
    import com.github.tminglei.slickpg._

    new ExPostgresDriver
        with PgHStoreSupport
        with PgDate2Support {
      override val api = new API with HStoreImplicits with DateTimeImplicits {}
      val plainApi = new API with SimpleHStorePlainImplicits with Date2DateTimePlainImplicits {}
    }.plainApi
  }

  import postgresApi._

  val db = Database.forURL(
    url = if (jdbcUrl startsWith "jdbc:postgresql://") jdbcUrl else "jdbc:postgresql://" + jdbcUrl
  , driver = "org.postgresql.Driver"
  )

  case class DatabaseMigration(
      ordinal: Int
    , dsls: Map[String, String]
    , implementations: Array[Byte]
    , version: String
    , appliedAt: ZonedDateTime)

  val query = sql"""
    SELECT *
    FROM "-NGS-"."database_migration"
    ORDER BY "ordinal"
""".as(GetResult(r => DatabaseMigration(
      ordinal = r.nextInt()
    , dsls = r.nextHStore()
    , implementations = r.nextBytes()
    , version = r.nextString()
    , appliedAt = r.nextZonedDateTime().withZoneSameInstant(ZoneId.of("Z"))
    )))

  val migrations = Await.result(db.run(query), Duration.Inf)

  val migrationFolder = Path(outputDir.replace('\\', '/'), '/')
  migrationFolder.deleteRecursively(force = true, continueOnFailure = false)

  for (migration <- migrations) {
    logger.info(s"Writing migration #${migration.ordinal} ...")

    val migrationId = "%04d" format migration.ordinal
    val currentFolder = migrationFolder / migrationId
    currentFolder.createDirectory(createParents = true)

    (currentFolder / "implementations").write(migration.implementations)
    (currentFolder / "version.txt").write(migration.version)
    (currentFolder / "appliedAt.txt").write(migration.appliedAt.toString)

    for ((dsl, body) <- migration.dsls) {
      val filename = dsl.replace('\\', '/')
      (currentFolder / (filename, '/')).write(body)
    }
  }

  logger.info("Stopping extractor!")
  sys.exit(0)
}
