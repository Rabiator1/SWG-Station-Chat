package chat.protocol.request;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.ChatApiTcpHandler;
import chat.ChatAvatar;
import chat.protocol.GenericRequest;
import chat.util.ChatUnicodeString;

public class RGetRoom extends GenericRequest {
	
	private ChatUnicodeString roomAddress = new ChatUnicodeString();
	private ChatAvatar avatar;

	@Override
	public ByteBuffer serialize() {
		ByteBuffer buf = ByteBuffer.allocate(10  + roomAddress.getString().length() * 2).order(ByteOrder.LITTLE_ENDIAN);
		buf.putShort(type);
		buf.putInt(track);
		buf.put(roomAddress.serialize());
		//buf.putInt(roomId);
		writeSize(buf);freeNative(buf);
		return buf;
	}

	private void freeNative(ByteBuffer buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		type = buf.getShort();
		track = buf.getInt();
		roomAddress.deserialize(buf);
		//System.out.println(ChatApiTcpHandler.bytesToHex(buf.array()));
	}

	public ChatUnicodeString getRoomAddress() {
		return roomAddress;
	}

	public void setRoomAddress(ChatUnicodeString roomAddress) {
		this.roomAddress = roomAddress;
	}

	public ChatAvatar getAvatarAddress() {
		// TODO Auto-generated method stub
		return avatar;
	}
	
	public int setTrack() {
		return track;
	}

}
