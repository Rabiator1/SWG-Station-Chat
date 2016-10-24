package chat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.util.ChatUnicodeString;

public class ChatFriend {
	
	private int avatarId;
	private ChatUnicodeString name;
	private ChatUnicodeString address;
	private ChatUnicodeString comment;
	private short status;
	
	public byte[] serialize() {
		ByteBuffer buf = ByteBuffer.allocate(14 + name.getStringLength() * 2 + address.getStringLength() * 2 + comment.getStringLength() * 2).order(ByteOrder.LITTLE_ENDIAN);
		buf.put(name.serialize());
		buf.put(address.serialize());
		buf.put(comment.serialize());
		buf.putShort(status);
		return buf.array();
	}
	
	public ChatUnicodeString getComment() {
		return comment;
	}
	
	public void setComment(ChatUnicodeString comment) {
		this.comment = comment;
	}

	public int getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(int avatarId) {
		this.avatarId = avatarId;
	}

	public ChatUnicodeString getName() {
		return name;
	}

	public void setName(ChatUnicodeString name) {
		this.name = name;
	}

	public ChatUnicodeString getAddress() {
		return address;
	}

	public void setAddress(ChatUnicodeString address) {
		this.address = address;
	}

	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	public String getFullAddress() {
		return address.getString() + "+" + name.getString();
	}

}
