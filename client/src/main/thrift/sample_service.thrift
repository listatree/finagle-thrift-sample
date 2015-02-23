namespace java com.sample.thrift.java
#@namespace scala com.sample.thrift

include "sample_response.thrift"
include "sample_request.thrift"

service SampleService {
   sample_response.SampleResponse greet(1:sample_request.SampleRequest request);
}
