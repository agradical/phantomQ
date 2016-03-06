# phantomQ
Its a generic remote queue push broker and worker service which provides abstraction layer for different implementations of queue services. Pushing and fetching an object to a remote queue can be done in simple steps.

Just extend your class to Queuable interface
and annotate it with @QueueConfig(queueName="testQ") to make your class queueable and 
call broker service to push to your desired remote queue.
