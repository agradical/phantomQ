package com.phantomQ.queuing;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class QueueItem<T extends Queuable> {

	protected T item;

	protected Long createdAt;

	protected String itemTypeIdentifier;

	protected String queueId;
	
	protected String receiptHandle;
	
	protected static Gson gson = new Gson();

	public QueueItem(T item) {
		this.item = item;
		this.createdAt = System.currentTimeMillis();
		this.itemTypeIdentifier = item.getClass().getName();
	}

	public T getItem() {
		return item;
	}

	public void setItem(T item) {
		this.item = item;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public String getItemTypeIdentifier() {
		return itemTypeIdentifier;
	}

	public void setItemTypeIdentifier(String itemTypeIdentifier) {
		this.itemTypeIdentifier = itemTypeIdentifier;
	}

	public String getQueueId() {
		return queueId;
	}

	public void setQueueId(String queueId) {
		this.queueId = queueId;
	}

	public String getReceiptHandle() {
		return receiptHandle;
	}

	public void setReceiptHandle(String receiptHandle) {
		this.receiptHandle = receiptHandle;
	}

	public String toJson(){
		return gson.toJson(this);
	}
	
	@SuppressWarnings("unchecked")
	public static <I extends Queuable> QueueItem<I> fromJson(String jsonStr)
			throws ClassNotFoundException {
		QueueItem<I> obj = null;
		JsonElement json = gson.fromJson(jsonStr, JsonElement.class);
		if (json.isJsonObject() && json.getAsJsonObject().has("item")
				&& json.getAsJsonObject().has("itemTypeIdentifier")) {
			Class<I> itemClass = (Class<I>) Class.forName(json
					.getAsJsonObject().get("itemTypeIdentifier").getAsString());
			I item = gson.fromJson(json.getAsJsonObject().get("item"), itemClass);
			obj = new QueueItem<I>(item);
			obj.setCreatedAt(json.getAsJsonObject().get("createdAt")
					.getAsLong());
		}
		return obj;
	}

}
