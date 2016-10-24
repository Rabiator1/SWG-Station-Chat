package chat.protocol.request;

import java.nio.ByteBuffer;

import chat.protocol.GenericRequest;
import chat.util.ChatUnicodeString;

public class RRegistrarGetChatServer extends GenericRequest	{
	
	private ChatUnicodeString hostname = new ChatUnicodeString();
	private short port;
	
	@Override
	public ByteBuffer serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		type = buf.getShort();
		track = buf.getInt();
		hostname.deserialize(buf);
		setPort(buf.getShort());
	}

	public short getPort() {
		return port;
	}

	public void setPort(short port) {
		this.port = port;
	}
	
	

}
