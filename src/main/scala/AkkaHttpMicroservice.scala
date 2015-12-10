import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.{Config, ConfigFactory}
import spray.json.{DefaultJsonProtocol, _}

import scala.concurrent.{ExecutionContextExecutor, Future}

import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._


case class Invitation(invitee: String, email: String)


trait InvitationDatabase {
  private val db = new ConcurrentHashMap[String, Invitation]
  def addInvitation(invitation: Invitation): Invitation = {
    db.put(invitation.email, invitation)
    invitation
  }
  def getInvitations(): Seq[Invitation] = db.values().asScala.toSeq
}

trait InvitationPersistence {
  val db = new InvitationDatabase {}
}

trait Protocols extends DefaultJsonProtocol {

  implicit val invitationFormat = jsonFormat2(Invitation.apply)
}

trait Service extends InvitationPersistence with Protocols {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def config: Config
  val logger: LoggingAdapter

  def getInvitations(): Future[Seq[Invitation]] = Future {
    db.getInvitations()
  }

  def addInvitation(invitation: Invitation): Future[Invitation] = Future {
    db.addInvitation(invitation)
  }


  val routes = {
    logRequestResult("akka-http-invitation") {
      pathPrefix("invitation") {
        get {

          complete(getInvitations().map(_.toJson))

        } ~
        post {
          entity(as[Invitation]) { invitation =>
            complete {
              addInvitation(invitation).map { x => Created -> "Created"}
            }
          }
        }
      }

    }

  }
}

object AkkaHttpMicroservice extends App with Service {
  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
