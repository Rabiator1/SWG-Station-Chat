package chat.protocol.request;

import java.nio.ByteBuffer;

import chat.protocol.GenericRequest;
import chat.util.ChatUnicodeString;

public class RSendPersistentMessage extends GenericRequest {
	
	private short avatarPresence;
	private int srcAvatarId;
	private ChatUnicodeString srcName = new ChatUnicodeString();
	private ChatUnicodeString destName = new ChatUnicodeString();
	private ChatUnicodeString destAddress = new ChatUnicodeString();
	private ChatUnicodeString subject = new ChatUnicodeString();
	private ChatUnicodeString message = new ChatUnicodeString();
	private ChatUnicodeString oob = new ChatUnicodeString();
	private ChatUnicodeString category = new ChatUnicodeString();
	private boolean enforceInboxLimit;
	private int categoryLimit;

	@Override
	public ByteBuffer serialize() {
		return null;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		type = buf.getShort();
		track = buf.getInt();
		avatarPresence = buf.getShort();
		if(avatarPresence != 0)
			srcAvatarId = buf.getInt();
		else
			srcName.deserialize(buf);
		destName.deserialize(buf);
		destAddress.deserialize(buf);
		subject.deserialize(buf);
		message.deserialize(buf);
		oob.deserialize(buf);
		category.deserialize(buf);
		enforceInboxLimit = buf.get() != 0;
		categoryLimit = buf.getInt();
	}
	
	public short isAvatarPresence() {
		return avatarPresence;
	}
	
	public void setAvatarPresence(short avatarPresence) {
		this.avatarPresence = avatarPresence;
	}

	public int getSrcAvatarId() {
		return srcAvatarId;
	}

	public void setSrcAvatarId(int srcAvatarId) {
		this.srcAvatarId = srcAvatarId;
	}

	public ChatUnicodeString getSrcName() {
		return srcName;
	}

	public void setSrcName(ChatUnicodeString srcName) {
		this.srcName = srcName;
	}

	public ChatUnicodeString getDestName() {
		return destName;
	}

	public void setDestName(ChatUnicodeString destName) {
		this.destName = destName;
	}

	public ChatUnicodeString getDestAddress() {
		return destAddress;
	}

	public void setDestAddress(ChatUnicodeString destAddress) {
		this.destAddress = destAddress;
	}

	public ChatUnicodeString getSubject() {
		return subject;
	}

	public void setSubject(ChatUnicodeString subject) {
		this.subject = subject;
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

	public ChatUnicodeString getCategory() {
		return category;
	}

	public void setCategory(ChatUnicodeString category) {
		this.category = category;
	}

	public boolean isEnforceInboxLimit() {
		return enforceInboxLimit;
	}

	public void setEnforceInboxLimit(boolean enforceInboxLimit) {
		this.enforceInboxLimit = enforceInboxLimit;
	}

	public int getCategoryLimit() {
		return categoryLimit;
	}

	public void setCategoryLimit(int categoryLimit) {
		this.categoryLimit = categoryLimit;
	}

}
