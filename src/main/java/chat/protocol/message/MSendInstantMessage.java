package chat.protocol.message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.ChatAvatar;
import chat.protocol.GenericMessage;
import chat.util.ChatUnicodeString;

public class MSendInstantMessage extends GenericMessage {
	
	private ChatAvatar srcAvatar;
	private int destAvatarId;
	private ChatUnicodeString message;
	private ChatUnicodeString oob;
	
	public MSendInstantMessage() {
		type = MESSAGE_INSTANTMESSAGE;
	}

	@Override
	public ByteBuffer serialize() {
		byte[] avatarBuf = srcAvatar.serialize();
		ByteBuffer buf = ByteBuffer.allocate(22 + avatarBuf.length + message.getStringLength() * 2 + oob.getStringLength() * 2).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(0);
		buf.put(avatarBuf);
		buf.putInt(destAvatarId);
		buf.put(message.serialize());
		buf.put(oob.serialize());
		writeSize(buf);
		return buf;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		// TODO Auto-generated method stub
		
	}

	public ChatAvatar getSrcAvatar() {
		return srcAvatar;
	}

	public void setSrcAvatar(ChatAvatar srcAvatar) {
		this.srcAvatar = srcAvatar;
	}

	public int getDestAvatarId() {
		return destAvatarId;
	}

	public void setDestAvatarId(int destAvatarId) {
		this.destAvatarId = destAvatarId;
	}

	public ChatUnicodeString getMessage() {
		return message;
	}

	public void setMessage(ChatUnicodeString message) {
		this.message = message;
	}

	public ChatUnicodeString getOob() {
		return oob;
	}

	public void setOob(ChatUnicodeString oob) {
		this.oob = oob;
	}

}
