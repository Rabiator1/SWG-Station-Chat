package chat.protocol;

import java.nio.ByteBuffer;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;

public abstract class GenericMessage {
	
	public static final ByteBufAllocator alloc = PooledByteBufAllocator.DEFAULT;
	
	protected short type;
	
	public final short getType() {
		return type;
	}
	
	protected void writeSize(ByteBuffer buf) {
		int size = buf.capacity();
		buf.putInt(0, ((size & 0xff) << 24 | (size & 0xff00) << 8 | (size & 0xff0000) >> 8 | (size >> 24) & 0xff));
	}
	
	public abstract ByteBuffer serialize();
	
	public abstract void deserialize(ByteBuffer buf);
	
	public static final short MESSAGE_INSTANTMESSAGE = 0;		
	public static final short MESSAGE_ROOMMESSAGE = 1;
	public static final short MESSAGE_BROADCASTMESSAGE = 2;
	public static final short MESSAGE_FRIENDLOGIN = 3;
	public static final short MESSAGE_FRIENDLOGOUT = 4;
	public static final short MESSAGE_KICKROOM = 5;			
	public static final short MESSAGE_ADDMODERATORROOM = 6;
	public static final short MESSAGE_REMOVEMODERATORROOM = 7;
	public static final short MESSAGE_REMOVEMODERATORAVATAR = 8;
	public static final short MESSAGE_ADDBANROOM = 9;
	public static final short MESSAGE_REMOVEBANROOM = 10;		
	public static final short MESSAGE_REMOVEBANAVATAR = 11;
	public static final short MESSAGE_ADDINVITEROOM = 12;
	public static final short MESSAGE_ADDINVITEAVATAR = 13;
	public static final short MESSAGE_REMOVEINVITEROOM = 14;
	public static final short MESSAGE_REMOVEINVITEAVATAR = 15;	
	public static final short MESSAGE_ENTERROOM = 16;
	public static final short MESSAGE_LEAVEROOM = 17;
	public static final short MESSAGE_DESTROYROOM = 18;
	public static final short MESSAGE_SETROOMPARAMS = 19;
	public static final short MESSAGE_PERSISTENTMESSAGE = 20;	
	public static final short MESSAGE_FORCEDLOGOUT = 21;
	public static final short MESSAGE_UNREGISTERROOMREADY = 22;
	public static final short MESSAGE_KICKAVATAR = 23;
	public static final short MESSAGE_ADDMODERATORAVATAR = 24;
	public static final short MESSAGE_ADDBANAVATAR = 25;		
	public static final short MESSAGE_ADDADMIN = 26;
	public static final short MESSAGE_REMOVEADMIN = 27;
	public static final short MESSAGE_FRIENDCONFIRMREQUEST = 28;
	public static final short MESSAGE_FRIENDCONFIRMRESPONSE = 29;
	public static final short MESSAGE_CHANGEROOMOWNER = 30;	
	public static final short MESSAGE_FORCEROOMFAILOVER = 31;
	public static final short MESSAGE_ADDTEMPORARYMODERATORROOM = 32;
	public static final short MESSAGE_ADDTEMPORARYMODERATORAVATAR = 33;
	public static final short MESSAGE_REMOVETEMPORARYMODERATORROOM = 34;
	public static final short MESSAGE_REMOVETEMPORARYMODERATORAVATAR = 35;	
	public static final short MESSAGE_GRANTVOICEROOM = 36;
	public static final short MESSAGE_GRANTVOICEAVATAR = 37;
	public static final short MESSAGE_REVOKEVOICEROOM = 38;
	public static final short MESSAGE_REVOKEVOICEAVATAR = 39;
	public static final short MESSAGE_SNOOP = 40;				
	public static final short MESSAGE_UIDLIST = 41;
	public static final short MESSAGE_REQUESTROOMENTRY = 42;
	public static final short MESSAGE_DELAYEDROOMENTRY = 43;
	public static final short MESSAGE_DENIEDROOMENTRY = 44;
    public static final short MESSAGE_FRIENDSTATUS = 45;		
	public static final short MESSAGE_FRIENDCONFIRMRECIPROCATE_REQUEST = 46;
	public static final short MESSAGE_FRIENDCONFIRMRECIPROCATE_RESPONSE = 47;
	public static final short MESSAGE_FILTERMESSAGE = 48;
	public static final short MESSAGE_FAILOVER_AVATAR_LIST = 49;
	public static final short MESSAGE_NOTIFY_FRIENDS_LIST_CHANGE = 50; 
	public static final short MESSAGE_NOTIFY_FRIEND_IS_REMOVED = 51;


}
