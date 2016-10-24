package chat.protocol.response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import chat.ChatFriend;
import chat.protocol.GenericResponse;

public class ResFriendStatus extends GenericResponse {
	
	private List<ChatFriend> friendsList;
	
	public ResFriendStatus() {
		type = RESPONSE_FRIENDSTATUS;
	}

	@Override
	public ByteBuffer serialize() {
		List<byte[]> friendBufs = new ArrayList<>();
		int friendBufsSize = 0;
		for(ChatFriend friend : friendsList) {
			byte[] friendBuf = friend.serialize();
			friendBufsSize += friendBuf.length;
			friendBufs.add(friendBuf);
		}
		ByteBuffer buf = ByteBuffer.allocate(18 + friendBufsSize).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(track);
		buf.putInt(result.ordinal());
		buf.putInt(friendBufs.size());
		for(byte[] friendBuf : friendBufs)
			buf.put(friendBuf);
		writeSize(buf);
		return buf;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		// TODO Auto-generated method stub
		
	}

	public List<ChatFriend> getFriendsList() {
		return friendsList;
	}

	public void setFriendsList(List<ChatFriend> friendsList) {
		this.friendsList = friendsList;
	}

}
