# phantomQ
Its generic remote queue push broker and worker service.
Pushing and fetching an object to a remote queue can be done in simple steps.
Just extend your class to Queuable interface
and annotate it with @QueueConfig(queueName="testQ")
