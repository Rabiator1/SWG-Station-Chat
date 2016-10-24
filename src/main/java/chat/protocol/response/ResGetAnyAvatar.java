package chat.protocol.response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.ChatAvatar;
import chat.protocol.GenericResponse;

public class ResGetAnyAvatar extends GenericResponse {
	
	private ChatAvatar avatar;
	private boolean isLoggedIn;
	
	public ResGetAnyAvatar() {
		type = RESPONSE_GETANYAVATAR;
	}

	@Override
	public ByteBuffer serialize() {
		if(result == ResponseResult.CHATRESULT_SUCCESS) {
			byte[] avatarBuf = avatar.serialize();
			ByteBuffer buf = ByteBuffer.allocate(15 + avatarBuf.length).order(ByteOrder.LITTLE_ENDIAN);
			buf.putInt(0);
			buf.putShort(type);
			buf.putInt(track);
			buf.putInt(result.ordinal());
			buf.put((byte) (isLoggedIn ? 1 : 0));
			buf.put(avatarBuf);
			writeSize(buf);
			return buf;
		} else {
			ByteBuffer buf = ByteBuffer.allocate(15).order(ByteOrder.LITTLE_ENDIAN);
			buf.putInt(0);
			buf.putShort(type);
			buf.putInt(track);
			buf.putInt(result.ordinal());
			buf.put((byte) (isLoggedIn ? 1 : 0));
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

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}
	
}
