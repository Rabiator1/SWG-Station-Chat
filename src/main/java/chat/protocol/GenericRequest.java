package chat.protocol;

public abstract class GenericRequest extends GenericMessage {
	
	protected int track;
	private long timeout;

	public int getTrack() {
		return track;
	}

	public void setTrack(int track) {
		this.track = track;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	public static final short REQUEST_LOGINAVATAR = 0;
	public static final short REQUEST_LOGOUTAVATAR = 1;
	public static final short REQUEST_DESTROYAVATAR = 2;
	public static final short REQUEST_GETAVATAR = 3;
	public static final short REQUEST_CREATEROOM = 4;
	public static final short REQUEST_DESTROYROOM = 5;			
	public static final short REQUEST_SENDINSTANTMESSAGE = 6;
	public static final short REQUEST_SENDROOMMESSAGE = 7;
	public static final short REQUEST_SENDBROADCASTMESSAGE = 8;
	public static final short REQUEST_ADDFRIEND = 9;
	public static final short REQUEST_REMOVEFRIEND = 10;			
	public static final short REQUEST_FRIENDSTATUS = 11;
	public static final short REQUEST_ADDIGNORE = 12;
	public static final short REQUEST_REMOVEIGNORE = 13;
	public static final short REQUEST_ENTERROOM = 14;
	public static final short REQUEST_LEAVEROOM = 15;				
	public static final short REQUEST_ADDMODERATOR = 16;
	public static final short REQUEST_REMOVEMODERATOR = 17;
	public static final short REQUEST_ADDBAN = 18;
	public static final short REQUEST_REMOVEBAN = 19;
	public static final short REQUEST_ADDINVITE = 20;				
	public static final short REQUEST_REMOVEINVITE = 21;
	public static final short REQUEST_KICKAVATAR = 22;
	public static final short REQUEST_SETROOMPARAMS = 23;
	public static final short REQUEST_GETROOM = 24;
	public static final short REQUEST_GETROOMSUMMARIES = 25;		
	public static final short REQUEST_SENDPERSISTENTMESSAGE = 26;
	public static final short REQUEST_GETPERSISTENTHEADERS = 27;
	public static final short REQUEST_GETPERSISTENTMESSAGE = 28;
	public static final short REQUEST_UPDATEPERSISTENTMESSAGE = 29;
	public static final short REQUEST_UNREGISTERROOM = 30;			
	public static final short REQUEST_IGNORESTATUS = 31;
	public static final short REQUEST_FAILOVER_RELOGINAVATAR = 32;
	public static final short REQUEST_FAILOVER_RECREATEROOM = 33;
	public static final short REQUEST_CONFIRMFRIEND = 34;
	public static final short REQUEST_GETAVATARKEYWORDS = 35;		
	public static final short REQUEST_SETAVATARKEYWORDS = 36;
	public static final short REQUEST_SEARCHAVATARKEYWORDS = 37;
	public static final short REQUEST_GETFANCLUBHANDLE = 38;
	public static final short REQUEST_UPDATEPERSISTENTMESSAGES = 39;
	public static final short REQUEST_FINDAVATARBYUID = 40;		
	public static final short REQUEST_CHANGEROOMOWNER = 41;
	public static final short REQUEST_SETAPIVERSION = 42;
	public static final short REQUEST_ADDTEMPORARYMODERATOR = 43;
	public static final short REQUEST_REMOVETEMPORARYMODERATOR = 44;
	public static final short REQUEST_GRANTVOICE = 45;				
	public static final short REQUEST_REVOKEVOICE = 46;
	public static final short REQUEST_SETAVATARATTRIBUTES = 47;
	public static final short REQUEST_ADDSNOOPAVATAR = 48;
	public static final short REQUEST_REMOVESNOOPAVATAR = 49;
	public static final short REQUEST_ADDSNOOPROOM = 50;			
	public static final short REQUEST_REMOVESNOOPROOM = 51;
	public static final short REQUEST_GETSNOOPLIST = 52;
	public static final short REQUEST_PARTIALPERSISTENTHEADERS = 53;
	public static final short REQUEST_COUNTPERSISTENTMESSAGES = 54;
	public static final short REQUEST_PURGEPERSISTENTMESSAGES = 55;	
	public static final short REQUEST_SETFRIENDCOMMENT = 56;
	public static final short REQUEST_TRANSFERAVATAR = 57;
	public static final short REQUEST_CHANGEPERSISTENTFOLDER = 58;
	public static final short REQUEST_ALLOWROOMENTRY = 59;
	public static final short REQUEST_SETAVATAREMAIL = 60;				
	public static final short REQUEST_SETAVATARINBOXLIMIT = 61;
	public static final short REQUEST_SENDMULTIPLEPERSISTENTMESSAGES = 62;
	public static final short REQUEST_GETMULTIPLEPERSISTENTMESSAGES = 63;
	public static final short REQUEST_ALTERPERISTENTMESSAGE = 64;    
    public static final short REQUEST_GETANYAVATAR = 65;				
    public static final short REQUEST_TEMPORARYAVATAR = 66;
    public static final short REQUEST_AVATARLIST = 67;					
    public static final short REQUEST_SETAVATARSTATUSMESSAGE = 68;	
	public static final short REQUEST_CONFIRMFRIEND_RECIPROCATE = 69;
	public static final short REQUEST_ADDFRIEND_RECIPROCATE = 70;		
	public static final short REQUEST_REMOVEFRIEND_RECIPROCATE = 71;
	public static final short REQUEST_FILTERMESSAGE = 72;
	public static final short REQUEST_FILTERMESSAGE_EX = 73;

	public static final short REQUEST_REGISTRAR_GETCHATSERVER = 20001;


}
