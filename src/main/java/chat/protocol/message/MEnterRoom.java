package chat.protocol.message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import chat.ChatAvatar;
import chat.protocol.GenericMessage;
import chat.util.ChatUnicodeString;

public class MEnterRoom extends GenericMessage {
	
	private ChatAvatar srcAvatar;
	private ChatAvatar Creator;
	private int roomId;
	private int SrcAvatarId;
	private ChatUnicodeString AvatarName;
	private ChatUnicodeString AvatarAddress;
	private ChatUnicodeString RoomAddress;
	private ChatUnicodeString Gateway;
	private ChatUnicodeString Server;
	
	public MEnterRoom() {
		type = MESSAGE_ENTERROOM;
	}

	@Override
	public ByteBuffer serialize() {
		byte[] avatarBuf = srcAvatar.serialize(); 
		//byte[] CreatorBuf = Creator.serialize();
		//ByteBuffer buf = ByteBuffer.allocate(30 + avatarBuf.length + AvatarName.getStringLength() * 2 + AvatarAddress.getStringLength() * 2 + Gateway.getStringLength() * 2 + Server.getStringLength() * 2).order(ByteOrder.LITTLE_ENDIAN);
		ByteBuffer buf = ByteBuffer.allocate(14 + avatarBuf.length  ).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(0);
		
		//buf.put(CreatorBuf);
		//buf.putInt(roomId);
		//buf.putInt(SrcAvatarId);
		//buf.putInt(0);
		buf.put(avatarBuf);
		//buf.put(AvatarAddress.serialize());
		//buf.put(AvatarName.serialize());
		//buf.put(Gateway.serialize());
		//buf.put(Server.serialize());
		//buf.putInt(0);
		
		//buf.put(RoomAddress.serialize());
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
	
	public void setCreator(ChatAvatar Creator) {
		this.Creator = Creator;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	
	public void setSrcAvatarId(int SrcAvatarId) {
		this.SrcAvatarId = SrcAvatarId;
	}
	
	public void setAvatarName(ChatUnicodeString AvatarName) {
		this.AvatarName = AvatarName;
	}
	
	public void setAvatarAddress(ChatUnicodeString AvatarAddress) {
		this.AvatarAddress = AvatarAddress;
	}
	
	public void setGateway(ChatUnicodeString Gateway) {
		this.Gateway = Gateway;
	}
	
	public void setServer(ChatUnicodeString Server) {
		this.Server = Server;
	}

}
