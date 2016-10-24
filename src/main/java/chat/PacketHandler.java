package chat;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface PacketHandler {
	
	public void handle(ChatApiClient cluster, ByteBuffer packet);

}
