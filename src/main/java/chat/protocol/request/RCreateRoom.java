package chat.protocol.request;

import java.nio.ByteBuffer;

import chat.protocol.GenericRequest;
import chat.util.ChatUnicodeString;

public class RCreateRoom extends GenericRequest {

	private int srcAvatarId;
	private ChatUnicodeString srcAddress = new ChatUnicodeString();
	private ChatUnicodeString roomAddress = new ChatUnicodeString();
	private ChatUnicodeString roomName = new ChatUnicodeString();
	private ChatUnicodeString roomTopic = new ChatUnicodeString();
	private ChatUnicodeString roomPassword = new ChatUnicodeString();
	private int roomAttributes; 
	private int maxRoomSize; 
	private int roomID;
	
	@Override
	public ByteBuffer serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		type = buf.getShort();
		track = buf.getInt();
		srcAvatarId = buf.getInt();
		roomName.deserialize(buf);
		roomTopic.deserialize(buf);
		roomPassword.deserialize(buf);
		roomAttributes = buf.getInt();
		maxRoomSize = buf.getInt();
		roomAddress.deserialize(buf);
		srcAddress.deserialize(buf);

	}

	public int getSrcAvatarId() {
		return srcAvatarId;
	}

	public void setSrcAvatarId(int srcAvatarId) {
		this.srcAvatarId = srcAvatarId;
	}

	public ChatUnicodeString getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(ChatUnicodeString srcAddress) {
		this.srcAddress = srcAddress;
	}

	public ChatUnicodeString getRoomAddress() {
		return roomAddress;
	}

	public void setRoomAddress(ChatUnicodeString roomAddress) {
		this.roomAddress = roomAddress;
	}

	public int getRoomAttributes() {
		return roomAttributes;
	}

	public void setRoomAttributes(int roomAttributes) {
		this.roomAttributes = roomAttributes;
	}

	public ChatUnicodeString getRoomName() {
		return roomName;
	}

	public void setRoomName(ChatUnicodeString roomName) {
		this.roomName = roomName;
	}

	public ChatUnicodeString getRoomTopic() {
		return roomTopic;
	}

	public void setRoomTopic(ChatUnicodeString roomTopic) {
		this.roomTopic = roomTopic;
	}

	public ChatUnicodeString getRoomPassword() {
		return roomPassword;
	}

	public void setRoomPassword(ChatUnicodeString roomPassword) {
		this.roomPassword = roomPassword;
	}

	public int getMaxRoomSize() {
		return maxRoomSize;
	}

	public void setMaxRoomSize(int maxRoomSize) {
		this.maxRoomSize = maxRoomSize;
	}

}
