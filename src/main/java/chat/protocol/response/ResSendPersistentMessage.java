package chat.protocol.response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.protocol.GenericResponse;

public class ResSendPersistentMessage extends GenericResponse {
	
	private int messageId;
	
	public ResSendPersistentMessage() {
		type = RESPONSE_SENDPERSISTENTMESSAGE;
	}

	@Override
	public ByteBuffer serialize() {
		ByteBuffer buf = ByteBuffer.allocate(result == ResponseResult.CHATRESULT_SUCCESS ? 18 : 14).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(track);
		buf.putInt(result.ordinal());
		if(result == ResponseResult.CHATRESULT_SUCCESS)
			buf.putInt(messageId);
		writeSize(buf);
		return buf;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

}
