package chat.protocol.response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import chat.ChatRoom;
import chat.protocol.GenericResponse;

public class ResLeaveRoom extends GenericResponse {

	private int destRoomId;
	private List<ChatRoom> extraRooms = new ArrayList<ChatRoom>();
	private int roomId;
	private boolean gotRoom = true;

	public ResLeaveRoom() {
		type = RESPONSE_LEAVEROOM;
	}

	@Override
	public ByteBuffer serialize() {
		ByteBuffer buf = ByteBuffer.allocate(18).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(track);
		buf.putInt(result.ordinal());
		//buf.putInt(srcAvatarId);
		buf.putInt(roomId);
		writeSize(buf);
		return buf;
	}

	@Override
	public void deserialize(ByteBuffer buf) {

	}

	public int getDestRoomId() {
		return destRoomId;
	}

	public void setDestRoomId(int destRoomId) {
		this.destRoomId = destRoomId;
	}

	public void setGotRoom(boolean b) {
		this.gotRoom = gotRoom;
		
	}
	public boolean isGotRoom() {
		return gotRoom;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	
}
