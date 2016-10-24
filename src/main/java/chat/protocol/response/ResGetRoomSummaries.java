package chat.protocol.response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import chat.ChatRoom;
import chat.protocol.GenericResponse;

public class ResGetRoomSummaries extends GenericResponse {
	
	private List<ChatRoom> rooms;
	
	public ResGetRoomSummaries() {
		type = RESPONSE_GETROOMSUMMARIES;
	}

	@Override
	public ByteBuffer serialize() {
		List<byte[]> sumBufs = new ArrayList<>();
		int sumBufsSize = 0;
		for(ChatRoom room : rooms) {
			byte[] sumBuf = room.serializeSummary();
			sumBufsSize += sumBuf.length;
			sumBufs.add(sumBuf);
		}
		ByteBuffer buf = ByteBuffer.allocate(18 + sumBufsSize).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(track);
		buf.putInt(result.ordinal());
		buf.putInt(rooms.size());
		for(byte[] sumBuf : sumBufs) {
			buf.put(sumBuf);
		}
		writeSize(buf);freeNative(buf);
		return buf;
	}
	
	private void freeNative(ByteBuffer buf) {
		// TODO Auto-generated method stub		
	}	

	@Override
	public void deserialize(ByteBuffer buf) {
		// TODO Auto-generated method stub
		
	}

	public List<ChatRoom> getRooms() {
		return rooms;
	}

	public void setRooms(List<ChatRoom> rooms) {
		this.rooms = rooms;
	}

}
