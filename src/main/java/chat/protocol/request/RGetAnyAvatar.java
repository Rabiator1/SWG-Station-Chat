package chat.protocol.request;

import java.nio.ByteBuffer;

import chat.protocol.GenericRequest;
import chat.util.ChatUnicodeString;

public class RGetAnyAvatar extends GenericRequest {

	private ChatUnicodeString name = new ChatUnicodeString();
	private ChatUnicodeString address = new ChatUnicodeString();
	
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

	@Override
	public ByteBuffer serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		type = buf.getShort();
		track = buf.getInt();
		name.deserialize(buf);
		address.deserialize(buf);
	}
}
