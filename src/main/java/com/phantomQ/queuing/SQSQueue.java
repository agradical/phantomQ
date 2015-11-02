package com.phantomQ.queuing;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

public class SQSQueue<T extends Queuable> implements Queue<T> {

	private Logger logger = LoggerFactory.getLogger(SQSQueue.class);

	protected AmazonSQS sqs;
	protected String name;
	protected String url;

	public SQSQueue(String name) {
		this.name = name;
		this.sqs = new AmazonSQSClient(
				new ClasspathPropertiesFileCredentialsProvider());
		sqs.setRegion(Region.getRegion(Regions.US_EAST_1));
		List<String> queues = sqs.listQueues("").getQueueUrls();
		for (String queue : queues)
			if (queue.split("/")[queue.split("/").length - 1].equals(name))
				this.url = queue;
	}

	protected <Output, Arg1> Output request(
			AmazonRequestCallable<Arg1, Output> req, Arg1 arg)
			throws AmazonClientException {
		try {
			return req.request(sqs, url, arg);
		} catch (AmazonServiceException ase) {
			logger.error("Caught an AmazonServiceException.");
			logger.error("=======================Error Message:    "
					+ ase.getMessage());
			logger.error("=======================HTTP Status Code: "
					+ ase.getStatusCode());
			logger.error("=======================AWS Error Code:   "
					+ ase.getErrorCode());
			logger.error("=======================Error Type:       "
					+ ase.getErrorType());
			logger.error("=======================Request ID:       "
					+ ase.getRequestId());
			throw ase;
		} catch (AmazonClientException ace) {
			logger.error("Caught an AmazonClientException.");
			logger.error("=======================Error Message: "
					+ ace.getMessage());
			throw ace;
		}
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return this.url;
	}

	@Override
	public QueueItem<T> enqueue(T item) {

		QueueItem<T> msg = new QueueItem<>(item);
		SendMessageResult rs = request(new AmazonSendMessageCallable(),
				msg.toJson());
		msg.setQueueId(rs.getMessageId());
		return msg;
	}

	@Override
	public Boolean delete(QueueItem<? extends Queuable> msg) {
		return request(new AmazonDeleteMessageCallable(),
				msg.getReceiptHandle());
	}

	@Override
	public List<QueueItem<T>> get() {
		return request(new AmazonReceiveMessageCallable<T>(), null);
	}

	public static interface AmazonRequestCallable<Arg1, Output> {
		public Output request(AmazonSQS sqs, String queueUrl, Arg1 arg);
	}

	public static class AmazonSendMessageCallable implements
			AmazonRequestCallable<String, SendMessageResult> {
		@Override
		public SendMessageResult request(AmazonSQS sqs, String queueUrl,
				String arg) {
			return sqs.sendMessage(new SendMessageRequest(queueUrl, arg));
		}

	}

	public static class AmazonDeleteMessageCallable implements
			AmazonRequestCallable<String, Boolean> {
		@Override
		public Boolean request(AmazonSQS sqs, String queueUrl, String arg) {
			sqs.deleteMessage(queueUrl, arg);
			return true;
		}

	}

	public static class AmazonReceiveMessageCallable<I extends Queuable> implements
			AmazonRequestCallable<Void, List<QueueItem<I>>> {
		@Override
		public List<QueueItem<I>> request(AmazonSQS sqs, String queueUrl,
				Void arg) {
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(
					queueUrl);
			List<Message> messages = sqs.receiveMessage(receiveMessageRequest)
					.getMessages();
			List<QueueItem<I>> pwMessages = new LinkedList<>();
			for (Message msg : messages) {
				QueueItem<I> pwMsg;
				try {
					pwMsg = QueueItem.fromJson(msg.getBody());
				} catch (ClassNotFoundException e) {
					continue;
				}
				pwMsg.setQueueId(msg.getMessageId());
				pwMsg.setReceiptHandle(msg.getReceiptHandle());
				pwMessages.add(pwMsg);
			}
			return pwMessages;
		}

	}

}
