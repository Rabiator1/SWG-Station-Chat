package chat.protocol.response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import chat.protocol.GenericResponse;
import chat.util.ChatUnicodeString;

public class ResRegistrarGetChatServer extends GenericResponse {
	
	private ChatUnicodeString hostname;
	private short port;
	
	public ResRegistrarGetChatServer() {
		type = GenericResponse.RESPONSE_REGISTRAR_GETCHATSERVER;
	}

	@Override
	public ByteBuffer serialize() {
		ByteBuffer buf = ByteBuffer.allocate(20 + hostname.getStringLength() * 2).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(track);
		buf.putInt(result.ordinal());
		buf.put(hostname.serialize());
		buf.putShort(port);
		writeSize(buf);
		return buf;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		// TODO Auto-generated method stub
	}

	public ChatUnicodeString getHostname() {
		return hostname;
	}

	public void setHostname(ChatUnicodeString hostname) {
		this.hostname = hostname;
	}

	public short getPort() {
		return port;
	}

	public void setPort(short port) {
		this.port = port;
	}

}
