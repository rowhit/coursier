package coursier.test

import coursier.util.TestEscape
import coursier.{Fetch, Task}

import scala.concurrent.{Promise, ExecutionContext, Future}
import scala.scalajs.js
import js.Dynamic.{global => g}
import scalaz.{-\/, EitherT, \/-}

package object compatibility {

  implicit val executionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  lazy val fs = g.require("fs")

  private def textResource0(path: String)(implicit ec: ExecutionContext): Future[String] = {
    val p = Promise[String]()

    fs.readFile(path, "utf-8", {
      (err: js.Dynamic, data: js.Dynamic) =>
        if (js.isUndefined(err) || err == null) p.success(data.asInstanceOf[String])
        else p.failure(new Exception(err.toString))
        ()
    }: js.Function2[js.Dynamic, js.Dynamic, Unit])

    p.future
  }

  def textResource(path: String)(implicit ec: ExecutionContext): Future[String] =
    textResource0("tests/shared/src/test/resources/" + path)

  private val baseRepo = "tests/metadata"

  val artifact: Fetch.Content[Task] = { artifact =>
    EitherT {
      assert(artifact.authentication.isEmpty)

      val path = baseRepo + "/" + TestEscape.urlAsPath(artifact.url)

      Task { implicit ec =>
        textResource0(path)
          .map(\/-(_))
          .recoverWith {
            case e: Exception =>
              Future.successful(-\/(e.getMessage))
          }
      }
    }
  }
}
