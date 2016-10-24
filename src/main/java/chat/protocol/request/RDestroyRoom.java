package chat.protocol.request;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.protocol.GenericRequest;
import chat.util.ChatUnicodeString;

public class RDestroyRoom extends GenericRequest {

	private int srcAvatarId;
	private int RoomId;
	private ChatUnicodeString srcAddress = new ChatUnicodeString();
	private ChatUnicodeString roomAddress = new ChatUnicodeString();



	@Override
	public void deserialize(ByteBuffer buf) {
		type = buf.getShort();
		track = buf.getInt();
		srcAvatarId = buf.getInt();
		roomAddress.deserialize(buf);
		srcAddress.deserialize(buf);
	}
	
	@Override
	public ByteBuffer serialize() {
		ByteBuffer buf = ByteBuffer.allocate(18 + roomAddress.getStringLength() * 2 + srcAddress.getStringLength() * 2).order(ByteOrder.LITTLE_ENDIAN);
		//buf.putInt(0);
		buf.putShort(type);
		buf.putInt(track);
		buf.putInt(srcAvatarId);
		buf.put(roomAddress.serialize());
		buf.put(srcAddress.serialize());
		writeSize(buf);
		return buf;
	}

	public int getSrcAvatarId() {
		return srcAvatarId;
	}

	public void setSrcAvatarId(int srcAvatarId) {
		this.srcAvatarId = srcAvatarId;
	}
	
	public void setRoomId(int RoomId) {
		this.RoomId = RoomId;
	}
	
	public void setTrack(int track) {
		this.track = track;
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
