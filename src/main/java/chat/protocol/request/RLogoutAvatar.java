package chat.protocol.request;

import java.nio.ByteBuffer;

import chat.protocol.GenericRequest;
import chat.util.ChatUnicodeString;

public class RLogoutAvatar extends GenericRequest {
	
	private int avatarId;
	private ChatUnicodeString address = new ChatUnicodeString();
	
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
		address.deserialize(buf);
	}
	

}
