package chat.protocol;

import chat.protocol.response.ResponseResult;

public abstract class GenericResponse extends GenericMessage {
	
	protected int track;
	private long timeout;
	protected ResponseResult result = ResponseResult.CHATRESULT_TIMEOUT;

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

	public ResponseResult getResult() {
		return result;
	}

	public void setResult(ResponseResult result) {
		this.result = result;
	}

	public static final short RESPONSE_LOGINAVATAR = 0;
	public static final short RESPONSE_LOGOUTAVATAR = 1;
	public static final short RESPONSE_DESTROYAVATAR = 2;
	public static final short RESPONSE_GETAVATAR = 3;
	public static final short RESPONSE_CREATEROOM = 4;
	public static final short RESPONSE_DESTROYROOM = 5;
	public static final short RESPONSE_SENDINSTANTMESSAGE = 6;
	public static final short RESPONSE_SENDROOMMESSAGE = 7;
	public static final short RESPONSE_SENDBROADCASTMESSAGE = 8;
	public static final short RESPONSE_ADDFRIEND = 9;
	public static final short RESPONSE_REMOVEFRIEND = 10;
	public static final short RESPONSE_FRIENDSTATUS = 11;
	public static final short RESPONSE_ADDIGNORE = 12;
	public static final short RESPONSE_REMOVEIGNORE = 13;
	public static final short RESPONSE_ENTERROOM = 14;
	public static final short RESPONSE_LEAVEROOM = 15;
	public static final short RESPONSE_ADDMODERATOR = 16;
	public static final short RESPONSE_REMOVEMODERATOR = 17;
	public static final short RESPONSE_ADDBAN = 18;
	public static final short RESPONSE_REMOVEBAN = 19;
	public static final short RESPONSE_ADDINVITE = 20;
	public static final short RESPONSE_REMOVEINVITE = 21;
	public static final short RESPONSE_KICKAVATAR = 22;
	public static final short RESPONSE_SETROOMPARAMS = 23;
	public static final short RESPONSE_GETROOM = 24;
	public static final short RESPONSE_GETROOMSUMMARIES = 25;
	public static final short RESPONSE_SENDPERSISTENTMESSAGE = 26;
	public static final short RESPONSE_GETPERSISTENTHEADERS = 27;
	public static final short RESPONSE_GETPERSISTENTMESSAGE = 28;
	public static final short RESPONSE_UPDATEPERSISTENTMESSAGE = 29;
	public static final short RESPONSE_UNREGISTERROOM = 30;
	public static final short RESPONSE_IGNORESTATUS = 31;
	public static final short RESPONSE_FAILOVER_RELOGINAVATAR = 32;
	public static final short RESPONSE_FAILOVER_RECREATEROOM = 33;
	public static final short RESPONSE_CONFIRMFRIEND = 34;
	public static final short RESPONSE_GETAVATARKEYWORDS = 35;
	public static final short RESPONSE_SETAVATARKEYWORDS = 36;
	public static final short RESPONSE_SEARCHAVATARKEYWORDS = 37;
	public static final short RESPONSE_GETFANCLUBHANDLE = 38;
	public static final short RESPONSE_UPDATEPERSISTENTMESSAGES = 39;
	public static final short RESPONSE_FINDAVATARBYUID = 40;
	public static final short RESPONSE_CHANGEROOMOWNER = 41;
	public static final short RESPONSE_SETAPIVERSION = 42;
	public static final short RESPONSE_ADDTEMPORARYMODERATOR = 43;
	public static final short RESPONSE_REMOVETEMPORARYMODERATOR = 44;
	public static final short RESPONSE_GRANTVOICE = 45;
	public static final short RESPONSE_REVOKEVOICE = 46;
	public static final short RESPONSE_SETAVATARATTRIBUTES = 47;
	public static final short RESPONSE_ADDSNOOPAVATAR = 48;
	public static final short RESPONSE_REMOVESNOOPAVATAR = 49;
	public static final short RESPONSE_ADDSNOOPROOM = 50;
	public static final short RESPONSE_REMOVESNOOPROOM = 51;
	public static final short RESPONSE_GETSNOOPLIST = 52;
	public static final short RESPONSE_PARTIALPERSISTENTHEADERS = 53;
	public static final short RESPONSE_COUNTPERSISTENTMESSAGES = 54;
	public static final short RESPONSE_PURGEPERSISTENTMESSAGES = 55;
	public static final short RESPONSE_SETFRIENDCOMMENT = 56;
	public static final short RESPONSE_TRANSFERAVATAR = 57;
	public static final short RESPONSE_CHANGEPERSISTENTFOLDER = 58;
	public static final short RESPONSE_ALLOWROOMENTRY = 59;
	public static final short RESPONSE_SETAVATAREMAIL = 60;
	public static final short RESPONSE_SETAVATARINBOXLIMIT = 61;
	public static final short RESPONSE_SENDMULTIPLEPERSISTENTMESSAGES = 62;
	public static final short RESPONSE_GETMULTIPLEPERSISTENTMESSAGES = 63;
	public static final short RESPONSE_ALTERPERSISTENTMESSAGE = 64;
    public static final short RESPONSE_GETANYAVATAR = 65;
    public static final short RESPONSE_TEMPORARYAVATAR = 66;
    public static final short RESPONSE_AVATARLIST = 67;
    public static final short RESPONSE_SETSTATUSMESSAGE = 68;
	public static final short RESPONSE_CONFIRMFRIEND_RECIPROCATE = 69;
	public static final short RESPONSE_ADDFRIEND_RECIPROCATE = 70;
	public static final short RESPONSE_REMOVEFRIEND_RECIPROCATE = 71;
	public static final short RESPONSE_FILTERMESSAGE = 72;
	public static final short RESPONSE_FILTERMESSAGE_EX = 73;

	public static final short RESPONSE_REGISTRAR_GETCHATSERVER = 20001;

}
