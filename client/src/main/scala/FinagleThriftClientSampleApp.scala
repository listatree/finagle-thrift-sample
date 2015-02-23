import com.twitter.finagle.Thrift
import com.twitter.util.Await

import com.sample.thrift._

object FinagleThriftClientSampleApp extends App {

  val client = Thrift.newIface[SampleService.FutureIface]("localhost:8080")

  val request = SampleRequest(name = "Fulgencio")
  val futureResponse = client.greet(request)

  futureResponse onSuccess { response =>
    println(response.greeting)
  } onFailure { e => 
    println("ERROR: " + e)
  }

  Await.ready(futureResponse)
  
}