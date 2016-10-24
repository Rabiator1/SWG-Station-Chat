package chat.protocol.request;

import java.nio.ByteBuffer;

import chat.protocol.GenericRequest;
import chat.util.ChatUnicodeString;

public class RSetAvatarAttributes extends GenericRequest {
	
	private int avatarId;
	private ChatUnicodeString address = new ChatUnicodeString();
	private int avatarAttributes;
	private boolean persistent;

	@Override
	public ByteBuffer serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		type = buf.getShort();
		track = buf.getInt();
		avatarId = buf.getInt();
		avatarAttributes = buf.getInt();
		persistent = buf.get() != 0;
		//address.deserialize(buf);
	}

	public int getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(int avatarId) {
		this.avatarId = avatarId;
	}

	public ChatUnicodeString getAddress() {
		return address;
	}

	public void setAddress(ChatUnicodeString address) {
		this.address = address;
	}

	public int getAvatarAttributes() {
		return avatarAttributes;
	}

	public void setAvatarAttributes(int avatarAttributes) {
		this.avatarAttributes = avatarAttributes;
	}

	public boolean isPersistent() {
		return persistent;
	}

	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

}
