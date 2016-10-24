package chat.protocol.request;

import java.nio.ByteBuffer;

import chat.protocol.GenericRequest;

public class RSendApiVersion extends GenericRequest {
	
	private int version;

	@Override
	public ByteBuffer serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		type = buf.getShort();
		track = buf.getInt();
		version = buf.getInt();
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
