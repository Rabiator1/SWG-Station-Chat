package chat.protocol.response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import chat.ChatIgnore;
import chat.protocol.GenericResponse;

public class ResIgnoreStatus extends GenericResponse {
	
	private List<ChatIgnore> ignoreList;
	
	public ResIgnoreStatus() {
		type = RESPONSE_IGNORESTATUS;
	}

	@Override
	public ByteBuffer serialize() {
		List<byte[]> ignoreBufs = new ArrayList<>();
		int ignoreBufsSize = 0;
		for(ChatIgnore ignore : ignoreList) {
			byte[] ignoreBuf = ignore.serialize();
			ignoreBufsSize += ignoreBuf.length;
			ignoreBufs.add(ignoreBuf);
		}
		ByteBuffer buf = ByteBuffer.allocate(18 + ignoreBufsSize).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(track);
		buf.putInt(result.ordinal());
		buf.putInt(ignoreBufs.size());
		for(byte[] ignoreBuf : ignoreBufs)
			buf.put(ignoreBuf);
		writeSize(buf);
		return buf;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		// TODO Auto-generated method stub
		
	}

	public List<ChatIgnore> getIgnoreList() {
		return ignoreList;
	}

	public void setIgnoreList(List<ChatIgnore> ignoreList) {
		this.ignoreList = ignoreList;
	}
	
}
