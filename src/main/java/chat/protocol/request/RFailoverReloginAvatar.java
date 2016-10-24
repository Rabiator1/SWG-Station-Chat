package chat.protocol.request;

import java.nio.ByteBuffer;
import chat.protocol.GenericRequest;
import chat.util.ChatUnicodeString;

public class RFailoverReloginAvatar extends GenericRequest {
	
	private int userId;
	private int AvatarId;
	private ChatUnicodeString name = new ChatUnicodeString();
	private ChatUnicodeString address = new ChatUnicodeString();
	private ChatUnicodeString loginLocation = new ChatUnicodeString();
	private int loginPriority;
	private int loginAttributes;
	

	@Override
	public ByteBuffer serialize() {
		return null;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		type = buf.getShort();
		track = buf.getInt();
		setAvatarId(buf.getInt());
		setUserId(buf.getInt());
		name.deserialize(buf);
		address.deserialize(buf);
		loginLocation.deserialize(buf);
		setLoginPriority(buf.getInt());
		setLoginAttributes(buf.getInt());
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getAvatarId() {
		return AvatarId;
	}

	public void setAvatarId(int userId) {
		this.AvatarId = AvatarId;
	}

	public int getLoginPriority() {
		return loginPriority;
	}

	public void setLoginPriority(int loginPriority) {
		this.loginPriority = loginPriority;
	}

	public int getLoginAttributes() {
		return loginAttributes;
	}

	public void setLoginAttributes(int loginAttributes) {
		this.loginAttributes = loginAttributes;
	}

	public ChatUnicodeString getName() {
		return name;
	}

	public void setName(ChatUnicodeString name) {
		this.name = name;
	}

	public ChatUnicodeString getAddress() {
		return address;
	}

	public void setAddress(ChatUnicodeString address) {
		this.address = address;
	}

	public ChatUnicodeString getLoginLocation() {
		return loginLocation;
	}

	public void setLoginLocation(ChatUnicodeString loginLocation) {
		this.loginLocation = loginLocation;
	}

}
