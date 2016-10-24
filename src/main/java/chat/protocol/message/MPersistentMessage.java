package chat.protocol.message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.PersistentMessage;
import chat.protocol.GenericMessage;

public class MPersistentMessage extends GenericMessage {
	
	private PersistentMessage pm;
	
	public MPersistentMessage() {
		type = MESSAGE_PERSISTENTMESSAGE;
	}

	@Override
	public ByteBuffer serialize() {
		byte[] pmBuf = pm.serializeHeader();
		ByteBuffer buf = ByteBuffer.allocate(14 + pmBuf.length).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(0);
		buf.putInt(pm.getAvatarId());
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
