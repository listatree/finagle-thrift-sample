import com.twitter.finagle.ListeningServer
import com.twitter.finagle.Thrift
import com.twitter.util.{Await, Future, Promise}

import com.sample.thrift._

object FinagleThriftServerSampleApp extends App {

  val service = new SampleService[Future] {
    def greet(request: SampleRequest): Future[SampleResponse] = {
      val response = SampleResponse(greeting = "Hello " + request.name)
      Future.value(response)
    }
  }

  val server = Thrift.serveIface(":8080", service)

  Await.ready(server)

}