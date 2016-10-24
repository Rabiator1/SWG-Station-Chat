package chat.protocol.response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.ChatRoom;
import chat.protocol.GenericResponse;
import chat.util.ChatUnicodeString;

public class ResUnregisterRoom extends GenericResponse {

	private int destRoomId;
	private ChatRoom destRoom;
	private ChatUnicodeString roomAddress = new ChatUnicodeString();

	public ResUnregisterRoom() {
		type = RESPONSE_UNREGISTERROOM;
	}

	@Override
	public ByteBuffer serialize() {
		ByteBuffer buf = ByteBuffer.allocate(18).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(track+1);
		buf.putInt(result.ordinal());
		roomAddress.deserialize(buf);
		writeSize(buf);freeNative(buf);
		return buf;
	}

	private void freeNative(ByteBuffer buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deserialize(ByteBuffer buf) {

	}

	public int getDestRoomId() {
		return destRoomId;
	}
	
	public ChatRoom getDestRoom() {
		return destRoom;
	}
	
	public ChatUnicodeString getRoomAddress() {
		return roomAddress;
	}

	public void setRoomAddress(ChatUnicodeString roomAddress) {
		this.roomAddress = roomAddress;
	}

	public void setDestRoomId(int destRoomId) {
		this.destRoomId = destRoomId;
	}

}
