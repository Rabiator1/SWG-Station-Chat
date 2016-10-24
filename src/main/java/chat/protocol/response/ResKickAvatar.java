package chat.protocol.response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.protocol.GenericResponse;

public class ResKickAvatar extends GenericResponse {

	private int destRoomId;

	public ResKickAvatar() {
		type = RESPONSE_KICKAVATAR;
	}

	@Override
	public ByteBuffer serialize() {
		ByteBuffer buf = ByteBuffer.allocate(18).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(track);
		buf.putInt(result.ordinal());
		buf.putInt(destRoomId);
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

}
