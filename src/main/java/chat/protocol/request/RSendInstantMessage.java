package chat.protocol.request;

import java.nio.ByteBuffer;
import chat.protocol.GenericRequest;
import chat.util.ChatUnicodeString;

public class RSendInstantMessage extends GenericRequest {
	
	private int srcAvatarId;
	private ChatUnicodeString destName = new ChatUnicodeString();
	private ChatUnicodeString destAddress = new ChatUnicodeString();
	private ChatUnicodeString message = new ChatUnicodeString();
	private ChatUnicodeString oob = new ChatUnicodeString();
	private ChatUnicodeString srcAddress = new ChatUnicodeString();
	
	@Override
	public ByteBuffer serialize() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void deserialize(ByteBuffer buf) {
		type = buf.getShort();
		track = buf.getInt();
		srcAvatarId = buf.getInt();
		destName.deserialize(buf);
		destAddress.deserialize(buf);
		message.deserialize(buf);
		oob.deserialize(buf);
		srcAddress.deserialize(buf);
	}

	public int getSrcAvatarId() {
		return srcAvatarId;
	}

	public void setSrcAvatarId(int srcAvatarId) {
		this.srcAvatarId = srcAvatarId;
	}

	public ChatUnicodeString getDestName() {
		return destName;
	}

	public void setDestName(ChatUnicodeString destName) {
		this.destName = destName;
	}

	public ChatUnicodeString getDestAddress() {
		return destAddress;
	}

	public void setDestAddress(ChatUnicodeString destAddress) {
		this.destAddress = destAddress;
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

	public ChatUnicodeString getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(ChatUnicodeString srcAddress) {
		this.srcAddress = srcAddress;
	}

}
