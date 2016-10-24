package chat.protocol.response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.ChatAvatar;
import chat.protocol.GenericResponse;

public class ResSetAvatarAttributes extends GenericResponse {
	
	private ChatAvatar avatar;
	
	public ResSetAvatarAttributes() {
		type = RESPONSE_SETAVATARATTRIBUTES;
	}

	@Override
	public ByteBuffer serialize() {
		if(result == ResponseResult.CHATRESULT_SUCCESS) {
			byte[] avatarBuf = avatar.serialize();
			ByteBuffer buf = ByteBuffer.allocate(14 + avatarBuf.length).order(ByteOrder.LITTLE_ENDIAN);
			buf.putInt(0);
			buf.putShort(type);
			buf.putInt(track);
			buf.putInt(result.ordinal());
			buf.put(avatarBuf);
			writeSize(buf);
			return buf;
		} else {
			ByteBuffer buf = ByteBuffer.allocate(14).order(ByteOrder.LITTLE_ENDIAN);
			buf.putInt(0);
			buf.putShort(type);
			buf.putInt(track);
			buf.putInt(result.ordinal());
			writeSize(buf);
			return buf;
		}
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
