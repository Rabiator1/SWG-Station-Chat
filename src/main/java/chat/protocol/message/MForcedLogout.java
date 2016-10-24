package chat.protocol.message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import chat.ChatAvatar;
import chat.ChatRoom;
import chat.protocol.GenericMessage;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

public class MForcedLogout extends GenericMessage {

	private int destRoomId;
	private List<ChatRoom> extraRooms = new ArrayList<ChatRoom>();
	private int roomId;
	private boolean gotRoom = true;
	private TIntList destAvatarIdList = new TIntArrayList();
	private ChatAvatar avatar;
	int SrcAvatarId;

	public MForcedLogout() {
		type = MESSAGE_FORCEDLOGOUT;
	}

	@Override
	public ByteBuffer serialize() {
		ByteBuffer buf = ByteBuffer.allocate(14).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(0);
		//buf.putInt(destAvatarIdList.size());
		//for(int avatarId : destAvatarIdList.toArray()) {
		//	buf.putInt(avatarId);
		//}
		buf.putInt(SrcAvatarId);
		writeSize(buf);freeNative(buf);
		return buf;
	}

	private void freeNative(ByteBuffer buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deserialize(ByteBuffer buf) {

	}

	public int getDestRoomId() {
		return destRoomId;
	}

	public void setDestRoomId(int destRoomId) {
		this.destRoomId = destRoomId;
	}
	
	public void setSrcAvatarId(int SrcAvatarId) {
		this.SrcAvatarId = SrcAvatarId;
	}

	public void setGotRoom(boolean b) {
		this.gotRoom = gotRoom;
		
	}
	public boolean isGotRoom() {
		return gotRoom;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	
	public void setDestAvatarIdList(TIntList destAvatarIdList) {
		this.destAvatarIdList = destAvatarIdList;
	}
	
	public ChatAvatar getAvatar() {
		return avatar;
	}

	public void setAvatar(ChatAvatar avatar) {
		this.avatar = avatar;
	}
	
}