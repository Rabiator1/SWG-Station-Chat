package chat.protocol.request;

import java.nio.ByteBuffer;
import chat.protocol.GenericRequest;
import chat.util.ChatUnicodeString;

public class RAddFriend extends GenericRequest {

	private int srcAvatarId;
	private ChatUnicodeString destName = new ChatUnicodeString();
	private ChatUnicodeString destAddress = new ChatUnicodeString();
	private ChatUnicodeString comment = new ChatUnicodeString();
	private boolean confirm;
	private ChatUnicodeString srcAddress = new ChatUnicodeString();

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
		destName.deserialize(buf);
		destAddress.deserialize(buf);
		comment.deserialize(buf);
		confirm = buf.get() != 0;
		srcAddress.deserialize(buf);
	}

	public int getSrcAvatarId() {
		return srcAvatarId;
	}

	public void setSrcAvatarId(int srcAvatarId) {
		this.srcAvatarId = srcAvatarId;
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

	public ChatUnicodeString getComment() {
		return comment;
	}

	public void setComment(ChatUnicodeString comment) {
		this.comment = comment;
	}

	public boolean isConfirm() {
		return confirm;
	}

	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}

	public ChatUnicodeString getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(ChatUnicodeString srcAddress) {
		this.srcAddress = srcAddress;
	}
	
}
