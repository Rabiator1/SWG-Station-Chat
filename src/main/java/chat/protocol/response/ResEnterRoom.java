package chat.protocol.response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import chat.ChatRoom;
import chat.protocol.GenericResponse;

public class ResEnterRoom extends GenericResponse {

	private ChatRoom room;
	private List<ChatRoom> extraRooms = new ArrayList<ChatRoom>();
	private int roomId;
	private boolean gotRoom = false;
	
	public ResEnterRoom() {
		type = RESPONSE_ENTERROOM;
	}

	@Override
	public ByteBuffer serialize() {
		List<byte[]> roomBufs = new ArrayList<>();
		int roomBufsSize = 0;
		for(ChatRoom room : extraRooms) {
			byte[] roomBuf = room.serialize();
			roomBufsSize += roomBuf.length;
			roomBufs.add(roomBuf);
		}
		int roomSize = 0; 
		byte[] roomBuf = null;
		if(gotRoom) {
			roomBuf = room.serialize();
			roomSize = roomBuf.length;
		}
		ByteBuffer buf = ByteBuffer.allocate(23 + roomBufsSize + roomSize).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(track);
		buf.putInt(result.ordinal());
		buf.putInt(roomId);
		buf.put((byte) (gotRoom ? 1 : 0));
		if(gotRoom) {
			buf.put(roomBuf);
			buf.putInt(extraRooms.size());
			for(byte[] sumBuf : roomBufs) {
				buf.put(sumBuf);
			}
		}
		writeSize(buf);
		return buf;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		// TODO Auto-generated method stub

	}

	public ChatRoom getRoom() {
		return room;
	}

	public void setRoom(ChatRoom room) {
		this.room = room;
	}

	public List<ChatRoom> getExtraRooms() {
		return extraRooms;
	}

	public void setExtraRooms(List<ChatRoom> extraRooms) {
		this.extraRooms = extraRooms;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public boolean isGotRoom() {
		return gotRoom;
	}

	public void setGotRoom(boolean gotRoom) {
		this.gotRoom = gotRoom;
	}

}
