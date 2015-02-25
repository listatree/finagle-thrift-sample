import java.util.concurrent.atomic.AtomicInteger

import com.twitter.finagle.Thrift
import com.twitter.finagle.util.DefaultTimer
import com.twitter.util.{Await, Duration, Future, Stopwatch}

import com.sample.thrift._

object FinagleThriftClientSampleApp extends App {

  val client = Thrift.newIface[SampleService.FutureIface]("localhost:8080")
  val request = SampleRequest(name = "Fulgencio")

  val atomicCounter = new AtomicInteger(0)
  var nonAtomicCounter = 0

  def greetLoop: Future[Nothing] = client.greet(request) flatMap { _ =>
    atomicCounter.incrementAndGet()
    nonAtomicCounter = nonAtomicCounter + 1
    greetLoop
  }

  val pause = Duration.fromMilliseconds(1000)
  implicit val timer = DefaultTimer.twitter
  def statsLoop: Future[Nothing] = Future.sleep(pause) flatMap { _ =>
    val delta = atomicCounter.get - nonAtomicCounter
    println("" + atomicCounter.get + " rps (atomic), " + nonAtomicCounter + " rps (non atomic), delta: " + delta)
    atomicCounter.set(0)
    nonAtomicCounter = 0
    statsLoop
  }

  def parallelGreetLoops(acc: List[Future[Nothing]], counter: Int, max: Int): List[Future[Nothing]] =
    if (counter < max) parallelGreetLoops(greetLoop :: acc, counter + 1, max)
    else statsLoop :: acc

  Await.result(Future.join(parallelGreetLoops(Nil, 0, 1000)))
  
}
