package chat.protocol.request;

import java.nio.ByteBuffer;

import chat.protocol.GenericRequest;
import chat.util.ChatUnicodeString;

public class RLeaveRoom extends GenericRequest {

	private int srcAvatarId;
	private ChatUnicodeString srcAddress = new ChatUnicodeString();
	private ChatUnicodeString roomAddress = new ChatUnicodeString();

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
		roomAddress.deserialize(buf);
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

	public ChatUnicodeString getRoomAddress() {
		return roomAddress;
	}

	public void setRoomAddress(ChatUnicodeString roomAddress) {
		this.roomAddress = roomAddress;
	}

}
