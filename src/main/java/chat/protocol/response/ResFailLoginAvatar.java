package chat.protocol.response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import chat.ChatAvatar;
import chat.protocol.GenericResponse;

public class ResFailLoginAvatar extends GenericResponse {
	
	private ChatAvatar avatar;
	
	public ResFailLoginAvatar() {
		type = RESPONSE_FAILOVER_RELOGINAVATAR;
	}

	@Override
	public ByteBuffer serialize() {
		byte[] avatarBuf = avatar.serialize();
		ByteBuffer buf = ByteBuffer.allocate(14 + avatarBuf.length).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(track);
		buf.putInt(result.ordinal());		
		if(result == ResponseResult.CHATRESULT_SUCCESS) {
			buf.put(avatarBuf);
			// These are optional and not used for SWG
			//buf.putInt(requiredLoginPriority);
			//buf.put(email.serialize());
			//buf.putInt(inboxLimit);
		}
		writeSize(buf);
		return buf;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		
	}

	public ChatAvatar getAvatar() {
		return avatar;
	}

	public void setAvatar(ChatAvatar avatar) {
		this.avatar = avatar;
	}	
	

}
