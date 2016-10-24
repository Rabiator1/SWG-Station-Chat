package chat.protocol.message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.ChatAvatar;
import chat.protocol.GenericRequest;
import chat.util.ChatUnicodeString;

public class MFriendLogin extends GenericRequest {
	
	private ChatAvatar friendAvatar;
	private ChatUnicodeString friendAddress;
	private int destAvatarId;
	private ChatUnicodeString friendStatus = new ChatUnicodeString();
	
	public MFriendLogin() {
		type = MESSAGE_FRIENDLOGIN;
	}

	@Override
	public ByteBuffer serialize() {
		byte[] avatarBuf = friendAvatar.serialize();
		ByteBuffer buf = ByteBuffer.allocate(22 + avatarBuf.length + friendAddress.getStringLength() * 2 + friendStatus.getStringLength() * 2).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(0);
		buf.put(avatarBuf);
		buf.put(friendAddress.serialize());
		buf.putInt(destAvatarId);
		buf.put(friendStatus.serialize());
		writeSize(buf);
		return buf;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		// TODO Auto-generated method stub
		
	}

	public ChatAvatar getFriendAvatar() {
		return friendAvatar;
	}

	public void setFriendAvatar(ChatAvatar friendAvatar) {
		this.friendAvatar = friendAvatar;
	}

	public ChatUnicodeString getFriendAddress() {
		return friendAddress;
	}

	public void setFriendAddress(ChatUnicodeString friendAddress) {
		this.friendAddress = friendAddress;
	}

	public int getDestAvatarId() {
		return destAvatarId;
	}

	public void setDestAvatarId(int destAvatarId) {
		this.destAvatarId = destAvatarId;
	}

	public ChatUnicodeString getFriendStatus() {
		return friendStatus;
	}

	public void setFriendStatus(ChatUnicodeString friendStatus) {
		this.friendStatus = friendStatus;
	}

}
