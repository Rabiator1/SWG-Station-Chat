package chat.protocol.response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.PersistentMessage;
import chat.protocol.GenericResponse;

public class ResGetPersistentMessage extends GenericResponse {

	private PersistentMessage pm;
	
	public ResGetPersistentMessage() {
		type = RESPONSE_GETPERSISTENTMESSAGE;
	}

	@Override
	public ByteBuffer serialize() {
		byte[] pmBuf = null;
		if(result == ResponseResult.CHATRESULT_SUCCESS)
			pmBuf = pm.serialize();
		ByteBuffer buf = ByteBuffer.allocate(pmBuf == null ? 14 : (14 + pmBuf.length)).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(track);
		buf.putInt(result.ordinal());
		if(result == ResponseResult.CHATRESULT_SUCCESS) 
			buf.put(pmBuf);
		writeSize(buf);
		return buf;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		
	}

	public PersistentMessage getPm() {
		return pm;
	}

	public void setPm(PersistentMessage pm) {
		this.pm = pm;
	}

}
