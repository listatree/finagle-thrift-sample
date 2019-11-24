import com.sample.thrift.{SampleRequest, SampleResponse, SampleService}
import com.twitter.finagle.Thrift
import com.twitter.util.{Await, Future}



object FinagleThriftServerSampleApp extends App {

  // The server part is easy in this sample, so let's just
  // create a simple implementation
  val service: SampleService[Future] = new SampleService[Future] {
    def greet(request: SampleRequest): Future[SampleResponse] = {
      val response = SampleResponse(greeting = "Hello " + request.name)
      Future.value(response)
    }
  }
  // Run the service implemented on the port 8080
  val server = Thrift.server.serveIface(":8080",service)

  // Keep waiting for the server and prevent the java process to exit
  // Q: What happens if we remove the await ?
  Await.ready(server)

}