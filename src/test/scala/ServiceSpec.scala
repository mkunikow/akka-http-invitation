import akka.event.NoLogging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.scaladsl.Flow
import org.scalatest._

class ServiceSpec extends FlatSpec with Matchers with ScalatestRouteTest with Service {
  override def testConfigSource = "akka.loglevel = WARNING"
  override def config = testConfig
  override val logger = NoLogging

  val invitation = Invitation("John Smith", "john@smith.mx")

  "Service" should "create invitation" in {
    Post(s"/invitation", invitation) ~> routes ~> check {
      status shouldBe Created
      responseAs[String] shouldBe "Created"
    }
  }

  "Service" should "create and get invitation" in {

    Post(s"/invitation", invitation) ~> routes ~> check {
      status shouldBe Created
      responseAs[String] shouldBe "Created"
    }

    Get(s"/invitation", invitation) ~> routes ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[Seq[Invitation]] shouldBe Seq(invitation)
    }
  }

}
