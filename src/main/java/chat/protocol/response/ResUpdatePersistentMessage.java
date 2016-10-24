package chat.protocol.response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.protocol.GenericResponse;

public class ResUpdatePersistentMessage extends GenericResponse {

	public ResUpdatePersistentMessage() {
		type = RESPONSE_UPDATEPERSISTENTMESSAGE;
	}

	@Override
	public ByteBuffer serialize() {
		ByteBuffer buf = ByteBuffer.allocate(14).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(track);
		buf.putInt(result.ordinal());
		writeSize(buf);
		return buf;
	}

	@Override
	public void deserialize(ByteBuffer buf) {

	}

}
