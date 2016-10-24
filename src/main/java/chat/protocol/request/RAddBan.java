package chat.protocol.request;

import java.nio.ByteBuffer;

import chat.protocol.GenericRequest;
import chat.util.ChatUnicodeString;

public class RAddBan extends GenericRequest {
	
	private int srcAvatarId;
	private ChatUnicodeString srcAddress = new ChatUnicodeString();
	private ChatUnicodeString destAvatarName = new ChatUnicodeString();
	private ChatUnicodeString destAvatarAddress = new ChatUnicodeString();
	private ChatUnicodeString destRoomAddress = new ChatUnicodeString();


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
		destAvatarName.deserialize(buf);
		destAvatarAddress.deserialize(buf);
		destRoomAddress.deserialize(buf);
		srcAddress.deserialize(buf);
	}

	public int getSrcAvatarId() {
		return srcAvatarId;
	}
	
	public void setSrcAvatarId(int srcAvatarId) {
		this.srcAvatarId = srcAvatarId;
	}

	public ChatUnicodeString getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(ChatUnicodeString srcAddress) {
		this.srcAddress = srcAddress;
	}

	public ChatUnicodeString getDestAvatarName() {
		return destAvatarName;
	}

	public void setDestAvatarName(ChatUnicodeString destAvatarName) {
		this.destAvatarName = destAvatarName;
	}

	public ChatUnicodeString getDestAvatarAddress() {
		return destAvatarAddress;
	}

	public void setDestAvatarAddress(ChatUnicodeString destAvatarAddress) {
		this.destAvatarAddress = destAvatarAddress;
	}

	public ChatUnicodeString getDestRoomAddress() {
		return destRoomAddress;
	}

	public void setDestRoomAddress(ChatUnicodeString destRoomAddress) {
		this.destRoomAddress = destRoomAddress;
	}

}
