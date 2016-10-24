package chat.protocol.message;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.ChatAvatar;
import chat.protocol.GenericMessage;
import chat.util.ChatUnicodeString;

public class MRoomMessage extends GenericMessage {
	
	private ChatUnicodeString message;
	private ChatUnicodeString oob;
	private int roomId;
	private TIntList destAvatarIdList = new TIntArrayList();
	private int messageId;
	private ChatAvatar avatar;
	
	public MRoomMessage() {
		type = MESSAGE_ROOMMESSAGE;
	}

	@Override
	public ByteBuffer serialize() {
		byte[] avatarBuf = avatar.serialize();
		ByteBuffer buf = ByteBuffer.allocate(30 + avatarBuf.length + message.getStringLength() * 2 + oob.getStringLength() * 2 + destAvatarIdList.size() * 4).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(0);
		buf.put(avatarBuf);
		buf.putInt(roomId);
		buf.putInt(destAvatarIdList.size());
		for(int avatarId : destAvatarIdList.toArray()) {
			buf.putInt(avatarId);
		}
		buf.put(message.serialize());
		buf.put(oob.serialize());
		buf.putInt(messageId);
		writeSize(buf);
		return buf;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		// TODO Auto-generated method stub
		
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

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public TIntList getDestAvatarIdList() {
		return destAvatarIdList;
	}

	public void setDestAvatarIdList(TIntList destAvatarIdList) {
		this.destAvatarIdList = destAvatarIdList;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public ChatAvatar getAvatar() {
		return avatar;
	}

	public void setAvatar(ChatAvatar avatar) {
		this.avatar = avatar;
	}

}
