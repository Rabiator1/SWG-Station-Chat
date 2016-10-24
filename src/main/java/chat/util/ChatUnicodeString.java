package chat.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class ChatUnicodeString {
	
	private String string;
	
	public ChatUnicodeString(String string) {
		setString(string);
	}

	public ChatUnicodeString() {
		string = new String();
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}
	
	public byte[] serialize() {
		int length = getStringLength();
		ByteBuffer buf = ByteBuffer.allocate(4 + length * 2).order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(length);
		buf.put(string.getBytes(StandardCharsets.UTF_16LE));
		return buf.array();
	}
	
	public void deserialize(ByteBuffer buf) {
		int length = buf.getInt();
		byte [] data = new byte[length * 2];
		buf.get(data);
		string = new String(data, StandardCharsets.UTF_16LE);
	}

	public int getStringLength() {
		return string.length();
	}

	public boolean equals(ChatUnicodeString string) {
		return getString().equals(string.getString());
	}
	
}
