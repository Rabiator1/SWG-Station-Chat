package chat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.util.ChatUnicodeString;

public class ChatIgnore {
	
	private int avatarId;
	private ChatUnicodeString name;
	private ChatUnicodeString address;

	public byte[] serialize() {
		ByteBuffer buf = ByteBuffer.allocate(8 + name.getStringLength() * 2 + address.getStringLength() * 2).order(ByteOrder.LITTLE_ENDIAN);
		buf.put(name.serialize());
		buf.put(address.serialize());
		return buf.array();
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

}
