package chat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import chat.util.ChatUnicodeString;

public class PersistentMessage {
	
	// Header
	private int messageId;
	private int avatarId; 
	private int timestamp;
	private int status;
	private ChatUnicodeString senderName;
	private ChatUnicodeString senderAddress;
	private ChatUnicodeString subject;
	private ChatUnicodeString folder; // not used in SWG and optional in API mail packets
	private ChatUnicodeString category; // not used in SWG and optional in API mail packets
	// + Body
	private ChatUnicodeString message;
	private ChatUnicodeString oob;
	private int fetchResult;
	
	public int getMessageId() {
		return messageId;
	}
	
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public int getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(int avatarId) {
		this.avatarId = avatarId;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public ChatUnicodeString getSenderName() {
		return senderName;
	}

	public void setSenderName(ChatUnicodeString senderName) {
		this.senderName = senderName;
	}

	public ChatUnicodeString getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(ChatUnicodeString senderAddress) {
		this.senderAddress = senderAddress;
	}

	public ChatUnicodeString getSubject() {
		return subject;
	}

	public void setSubject(ChatUnicodeString subject) {
		this.subject = subject;
	}

	public ChatUnicodeString getFolder() {
		return folder;
	}

	public void setFolder(ChatUnicodeString folder) {
		this.folder = folder;
	}

	public ChatUnicodeString getCategory() {
		return category;
	}

	public void setCategory(ChatUnicodeString category) {
		this.category = category;
	}

	public ChatUnicodeString getMessage() {
		return message;
	}

	public void setMessage(ChatUnicodeString message) {
		this.message = message;
	}

	public ChatUnicodeString getOob() {
		return oob;
	}

	public void setOob(ChatUnicodeString oob) {
		this.oob = oob;
	}

	public int getFetchResult() {
		return fetchResult;
	}

	public void setFetchResult(int fetchResult) {
		this.fetchResult = fetchResult;
	}
	
	public byte[] serializeHeader() {
		ByteBuffer buf = ByteBuffer.allocate(28 + senderName.getStringLength() * 2 + senderAddress.getStringLength() * 2 + subject.getStringLength() * 2).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(messageId);
		buf.putInt(avatarId);
		buf.put(senderName.serialize());
		buf.put(senderAddress.serialize());
		buf.put(subject.serialize());
		buf.putInt(timestamp);
		buf.putInt(status);
		return buf.array();
	}
	
	public byte[] serialize() {
		ByteBuffer buf =  ByteBuffer.allocate(36 + senderName.getStringLength() * 2 + senderAddress.getStringLength() * 2 + subject.getStringLength() * 2 + message.getStringLength() * 2 + oob.getStringLength() * 2).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(messageId);
		buf.putInt(avatarId);
		buf.put(senderName.serialize());
		buf.put(senderAddress.serialize());
		buf.put(subject.serialize());
		buf.putInt(timestamp);
		buf.putInt(status);
		buf.put(message.serialize());
		buf.put(oob.serialize());
		return buf.array();
	}

	

}
