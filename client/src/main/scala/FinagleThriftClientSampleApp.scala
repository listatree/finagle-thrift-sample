import java.util.concurrent.atomic.AtomicInteger

import com.twitter.conversions.DurationOps._
import com.twitter.finagle.Thrift
import com.twitter.finagle.util.DefaultTimer
import com.twitter.util.{Await, Duration, Future, Stopwatch}

import com.sample.thrift._

object FinagleThriftClientSampleApp extends App {

  // Let's create a client for connecting to the sample Thrift server
  // (remember to start the sample Thrift server first).
  // Q: what may happened if the server is not started ? ... after using
  // and understanding better the code, try to add error handling for the
  // case that the server is not available
  val client = Thrift.client.newIface[SampleService.FutureIface]("localhost:8080")

  // For having some fun, let's create a counter for tracing how many
  // requests we are performing. An atomic object is required to
  // make the correct counting since we are potentially using several
  // threads.
  // Q: what happened if we use a normal var Int here ? The counter
  // won't be accurate at all ? ... or the error will be reasonable ? ... or
  // maybe, there won't be any error at all ?
  val requestsCounter = new AtomicInteger(0)

  // Now let's create a sample request. This normally varies per server call,
  // but for our test purposes we will keep it as a single preconstructed
  // value.
  // Q: How the performance will be impacted if a new request is created per
  // server call ? ... what about the memory usage about it ? ... what
  // about the garbage collector work ?
  val request = SampleRequest(name = "Fulgencio")

  // OK, now to the point: let's make the server calls passing the request to
  // the method greet, and do this is in a loop to meassure the performance
  // increasing the counter once per each call.
  // Have you notice the use of flatMap here ? ... the Futures are
  // a very elegant way to compose processing.
  // Q: Instead of flatMap, try to use something else, like an explicit
  // Promise by instance. Maybe the code will be more verbose, but give it a
  // try and see what happens with the performance and the code readability
  // compared with the use of flatMap
  def requestLoop: Future[Nothing] = client.greet(request) flatMap { _ =>
    requestsCounter.incrementAndGet()
    requestLoop
  }

  // Run 1000 requests loops in parallel (this is Fun * 1000)
  // Q: what happens if we use only 2 parallel loops ? ... or 20000 ?
  // what about 16 compared with 1000 ? ... how the performance
  // is impacted and what is the best value ? ... this depends on
  // the number of the processors the system has ? ... or it depends more
  // on the memory the system has ? ... or both ?
  def parallelRequestLoops = List.fill(1000)(requestLoop)

  // Report the number of requests per second that are performed by
  // the requests. See the easy to express a one second
  // pauses for these stats thanks to the Future.sleep feature
  // Q: What happend if we use a Thread.sleep here instead ? ... it
  // would be better ? ... what happens to the performance ?
  implicit val timer = DefaultTimer.twitter
  def statsLoop: Future[Nothing] = Future.sleep(1.second) flatMap { _ =>
    println("" + requestsCounter.get + " rps")
    requestsCounter.set(0)
    statsLoop
  }

  // OK, so now, let's join all the parallel request loops with the stats and
  // let the fun begins !! (Y) :)
  // Q: What may happend if we just remove the Await.result ?
  Await.result(Future.join(statsLoop :: parallelRequestLoops))
  
}
