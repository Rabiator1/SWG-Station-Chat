package chat.protocol.response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import chat.ChatRoom;
import chat.protocol.GenericResponse;

public class ResFailoverRecreateRoom extends GenericResponse {

	private ChatRoom room;
	private List<ChatRoom> extraRooms = new ArrayList<ChatRoom>();
	private boolean gotRoom = false;
	
	public ResFailoverRecreateRoom() {
		type = RESPONSE_FAILOVER_RECREATEROOM;
	}

	@Override
	public ByteBuffer serialize() {
		List<byte[]> roomBufs = new ArrayList<>();
		int roomBufsSize = 0;
		for(ChatRoom room : extraRooms) {
			byte[] roomBuf = room.serialize();
			roomBufsSize += roomBuf.length;
			roomBufs.add(roomBuf);
			//System.out.println("roomBuf  " + roomBuf);
		}
		int roomSize = 0; 
		byte[] roomBuf = null;
		if(result == ResponseResult.CHATRESULT_SUCCESS) {
			roomBuf = room.serialize();
			roomSize = roomBuf.length;
		}
		ByteBuffer buf;
		if(result == ResponseResult.CHATRESULT_SUCCESS)
			buf = ByteBuffer.allocate(18 + roomBufsSize + roomSize).order(ByteOrder.LITTLE_ENDIAN);
		else
			buf = ByteBuffer.allocate(14 + roomBufsSize + roomSize).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(track);
		buf.putInt(result.ordinal());
		if(result == ResponseResult.CHATRESULT_SUCCESS) {
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
	
	public void setGotRoom(boolean gotRoom) {
		this.gotRoom = gotRoom;
	}

}
