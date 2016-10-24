package chat.protocol.message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.ChatAvatar;
import chat.protocol.GenericMessage;

public class MDestroyRoom extends GenericMessage {
	
	private ChatAvatar srcAvatar;
	private int roomId;
	
	public MDestroyRoom() {
		type = MESSAGE_DESTROYROOM;
	}

	@Override
	public ByteBuffer serialize() {
		byte[] avatarBuf = srcAvatar.serialize();
		ByteBuffer buf = ByteBuffer.allocate(14 + avatarBuf.length).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(0);
		buf.put(avatarBuf);
		buf.putInt(roomId);
		writeSize(buf);freeNative(buf);
		return buf;
	}

	private void freeNative(ByteBuffer buf) {
		// TODO Auto-generated method stub
		
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

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

}
