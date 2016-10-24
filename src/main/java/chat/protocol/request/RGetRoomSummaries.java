package chat.protocol.request;

import java.nio.ByteBuffer;

import chat.protocol.GenericRequest;
import chat.util.ChatUnicodeString;

public class RGetRoomSummaries extends GenericRequest {
	
	private ChatUnicodeString startNodeAddress = new ChatUnicodeString();
	private ChatUnicodeString roomFilter = new ChatUnicodeString();

	@Override
	public ByteBuffer serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		type = buf.getShort();
		track = buf.getInt();
		startNodeAddress.deserialize(buf);
		roomFilter.deserialize(buf);
	}

	public ChatUnicodeString getStartNodeAddress() {
		return startNodeAddress;
	}

	public void setStartNodeAddress(ChatUnicodeString startNodeAddress) {
		this.startNodeAddress = startNodeAddress;
	}

	public ChatUnicodeString getRoomFilter() {
		return roomFilter;
	}

	public void setRoomFilter(ChatUnicodeString roomFilter) {
		this.roomFilter = roomFilter;
	}

}
