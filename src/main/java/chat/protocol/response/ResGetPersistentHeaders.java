package chat.protocol.response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import chat.PersistentMessage;
import chat.protocol.GenericResponse;

public class ResGetPersistentHeaders extends GenericResponse {

	private List<PersistentMessage> pmList = new ArrayList<>();
	
	public ResGetPersistentHeaders() {
		type = RESPONSE_GETPERSISTENTHEADERS;
	}

	@Override
	public ByteBuffer serialize() {
		List<byte[]> pmBuffers = new ArrayList<>();
		int pmBufSize = 0;
		for(PersistentMessage pm : pmList) {
			byte[] pmBuf = pm.serializeHeader();
			pmBufSize += pmBuf.length;
			pmBuffers.add(pmBuf);
		}
		ByteBuffer buf = ByteBuffer.allocate(18 + pmBufSize).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(0);
		buf.putShort(type);
		buf.putInt(track);
		buf.putInt(result.ordinal());
		buf.putInt(pmList.size());
		for(byte[] pmBuf : pmBuffers)
			buf.put(pmBuf);
		writeSize(buf);
		return buf;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		
	}

	public List<PersistentMessage> getPmList() {
		return pmList;
	}

	public void setPmList(List<PersistentMessage> pmList) {
		this.pmList = pmList;
	}
	
	public void addPersistentMessage(PersistentMessage pm) {
		pmList.add(pm);
	}

}
