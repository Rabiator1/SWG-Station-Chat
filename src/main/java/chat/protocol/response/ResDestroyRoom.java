package chat.protocol.response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.protocol.GenericResponse;

public class ResDestroyRoom extends GenericResponse {

	private int destRoomId;

	public ResDestroyRoom() {
		type = RESPONSE_DESTROYROOM;
	}

	@Override
	public ByteBuffer serialize() {
		ByteBuffer buf = ByteBuffer.allocate(18).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(track);
		buf.putInt(result.ordinal());
		buf.putInt(destRoomId);
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

	public void setDestRoomId(int destRoomId) {
		this.destRoomId = destRoomId;
	}

}
