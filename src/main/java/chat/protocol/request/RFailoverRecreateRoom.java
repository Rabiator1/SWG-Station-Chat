package chat.protocol.request;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import chat.ChatAvatar;
import chat.protocol.GenericRequest;
import chat.util.ChatUnicodeString;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

public class RFailoverRecreateRoom extends GenericRequest {

	private int srcAvatarId;
	private int inRoomAvatarId;
	private int destAvatarId;
	private int StationId;
	private int AvaTyp;
	private int Zahl2;
	
	private int roomFailID;
	//private int roomPrefix;
	private int createTime;
	private int avatarCount;	
	private int adminCount;	
	private int moderatorCount;	
	private int tempModeratorCount;	
	private int bannedCount;	
	private int inviteCount;	
	private int VoiceCount;	
	private TIntArrayList inRoomAvatarlist = new TIntArrayList();
	private TIntArrayList inRoomAdminlist = new TIntArrayList();
	private TIntArrayList inRoomModeratorlist = new TIntArrayList();
	private TIntArrayList inRoomTempModeratorlist = new TIntArrayList();
	private TIntArrayList inRoomBanlist = new TIntArrayList();
	private TIntArrayList inRoomInvitelist = new TIntArrayList();
	private TIntArrayList inRoomVoicelist = new TIntArrayList();
	
	private List<ChatAvatar> destAvatarIdList = new ArrayList<>();
	private ChatUnicodeString srcAddress = new ChatUnicodeString();
	private ChatUnicodeString roomAddress = new ChatUnicodeString();
	private ChatUnicodeString roomName = new ChatUnicodeString();
	private ChatUnicodeString roomTopic = new ChatUnicodeString();
	private ChatUnicodeString roomPassword = new ChatUnicodeString();
	private ChatUnicodeString name = new ChatUnicodeString();
	private ChatUnicodeString Address = new ChatUnicodeString();
	private ChatUnicodeString roomPrefix = new ChatUnicodeString();
	private ChatUnicodeString AvaName = new ChatUnicodeString();
	private ChatUnicodeString AvaAddress = new ChatUnicodeString();
	
	private ChatUnicodeString AdminAvaName = new ChatUnicodeString();
	private ChatUnicodeString AdminAvaAddress = new ChatUnicodeString();
	private int inRoomAdminAvatarId;
	private int AdminStationId;
	private int AdminAvaTyp;
	
	private ChatUnicodeString ModeratorAvaName = new ChatUnicodeString();
	private ChatUnicodeString ModeratorAvaAddress = new ChatUnicodeString();
	private int inRoomModeratorAvatarId;
	private int ModeratorStationId;
	private int ModeratorAvaTyp;
	
	private ChatUnicodeString TempModeratorAvaName = new ChatUnicodeString();
	private ChatUnicodeString TempModeratorAvaAddress = new ChatUnicodeString();
	private int inRoomTempModeratorAvatarId;
	private int TempModeratorStationId;
	private int TempModeratorAvaTyp;
	
	private ChatUnicodeString BannedAvaName = new ChatUnicodeString();
	private ChatUnicodeString BannedAvaAddress = new ChatUnicodeString();
	private int inRoomBannedAvatarId;
	private int BannedStationId;
	private int BannedAvaTyp;
	
	private ChatUnicodeString InviteAvaName = new ChatUnicodeString();
	private ChatUnicodeString InviteAvaAddress = new ChatUnicodeString();
	private int inRoomInviteAvatarId;
	private int InviteStationId;
	private int InviteAvaTyp;
	
	private ChatUnicodeString VoiceAvaName = new ChatUnicodeString();
	private ChatUnicodeString VoiceAvaAddress = new ChatUnicodeString();
	private int inRoomVoiceAvatarId;
	private int VoiceStationId;
	private int VoiceAvaTyp;
	
	private int roomAttributes; 
	private int maxRoomSize; 
	private ChatUnicodeString serializeWithLocalAvatarsOnly = new ChatUnicodeString();
	
	

	
	@Override
	public ByteBuffer serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deserialize(ByteBuffer buf) {
		type = buf.getShort();
		track = buf.getInt();
		serializeWithLocalAvatarsOnly();
		name.deserialize(buf);
		Address.deserialize(buf);
		srcAvatarId = buf.getInt();
		roomName.deserialize(buf);
		roomTopic.deserialize(buf);
		roomPassword.deserialize(buf);
		roomPrefix.deserialize(buf);
		roomAddress.deserialize(buf);
		roomAttributes = buf.getInt();
		maxRoomSize = buf.getInt();
		roomFailID = buf.getInt();//System.out.println("roomFailID  " + roomFailID);
		createTime = buf.getInt();

		
		//adminCount = buf.getInt(); System.out.println("adminCount  " + adminCount);
		//AvatarId = buf.getInt(); System.out.println("AvatarId1  " + AvatarId);
		//moderatorCount = buf.getInt(); System.out.println("moderatorCount  " + moderatorCount);
		//tempModeratorCount = buf.getInt(); System.out.println("tempModeratorCount  " + tempModeratorCount);
		//bannedCount = buf.getInt(); System.out.println("bannedCount  " + bannedCount);
		//inviteCount = buf.getInt(); System.out.println("inviteCount  " + inviteCount);
		//VoiceCount = buf.getInt(); System.out.println("VoiceCount  " + VoiceCount);
		//System.out.println("hasArraylenght  " + buf.array().length);
		//System.out.println("hasArraygetClass  " + buf.array().getClass());
		//System.out.println("hasArraygetPosition  " + buf.position());
		//System.out.println("hasArraygetCapacity  " + buf.capacity());

		
		ArrayList<Byte> AvatarIds = new ArrayList<>();
		int [] einArray;
		einArray = new int[buf.remaining()];
		
		//inRoomAvatars
		avatarCount = buf.getInt();// System.out.println("avatarCount  " + avatarCount);
		for (int i=0;i < avatarCount  ; i++){
			inRoomAvatarId = buf.getInt();
			//System.out.println("inRoomAvatarId  " + inRoomAvatarId);
			inRoomAvatarlist.add(inRoomAvatarId);
			//System.out.println("inRoomAvatarlist  " + inRoomAvatarlist);
			StationId = buf.getInt();
			//System.out.println("StationId  " + StationId);
			AvaName.deserialize(buf);
			//System.out.println("AvaName  " + AvaName.getString());
			AvaAddress.deserialize(buf);
			//System.out.println("AvaAddress  " + AvaAddress.getString());
			AvaTyp = buf.getInt();
			//System.out.println("AvaTyp  " + AvaTyp);
			Zahl2 = buf.getInt();
			//System.out.println("Zahl2  " + Zahl2);
			
			//AvatarIds.add((byte) buf.getChar());
			//int digits = (int)Math.floor(Math.log10(Math.abs(AvatarId)));
			//if (digits == 10 || digits == 9 ){
			//}
		}
		//Zahl2 = buf.getInt();
		//System.out.println("Zahl2  " + Zahl2);
		
		//inRoomAdmin
		adminCount = buf.getInt();
		//System.out.println("adminCount  " + adminCount);
		for (int i=0;i < adminCount  ; i++){
			inRoomAdminAvatarId = buf.getInt();
			//System.out.println("inRoomAdminAvatarId  " + inRoomAdminAvatarId);
			inRoomAdminlist.add(inRoomAdminAvatarId);
			//System.out.println("inRoomAdminlist  " + inRoomAdminlist);
			AdminStationId = buf.getInt();
			//System.out.println("AdminStationId  " + AdminStationId);
			AdminAvaName.deserialize(buf);
			//System.out.println("AdminAvaName  " + AdminAvaName.getString());
			AdminAvaAddress.deserialize(buf);
			//System.out.println("AdminAvaAddress  " + AdminAvaAddress.getString());
			AdminAvaTyp = buf.getInt();
			//System.out.println("AdminAvaTyp  " + AdminAvaTyp);
			Zahl2 = buf.getInt();
			//System.out.println("Zahl2  " + Zahl2);
			
		}
		
		//Zahl2 = buf.getInt();
		//System.out.println("Zahl2  " + Zahl2);
		
		//inRoomModerator
		moderatorCount = buf.getInt();
		//System.out.println("moderatorCount  " + moderatorCount);
		for (int i=0;i < moderatorCount  ; i++){
			inRoomModeratorAvatarId = buf.getInt();
			//System.out.println("inRoomModeratorAvatarId  " + inRoomModeratorAvatarId);
			inRoomModeratorlist.add(inRoomModeratorAvatarId);
			//System.out.println("inRoomModeratorlist  " + inRoomModeratorlist);
			ModeratorStationId = buf.getInt();
			//System.out.println("ModeratorStationId  " + ModeratorStationId);
			ModeratorAvaName.deserialize(buf);
			//System.out.println("ModeratorAvaName  " + ModeratorAvaName.getString());
			ModeratorAvaAddress.deserialize(buf);
			//System.out.println("ModeratorAvaAddress  " + ModeratorAvaAddress.getString());
			ModeratorAvaTyp = buf.getInt();
			//System.out.println("ModeratorAvaTyp  " + ModeratorAvaTyp);
			Zahl2 = buf.getInt();
			//System.out.println("Zahl2  " + Zahl2);			
		}
		
		//inRoomTempModerator
		tempModeratorCount = buf.getInt();
		//System.out.println("tempModeratorCount  " + tempModeratorCount);
		for (int i=0;i < tempModeratorCount  ; i++){
			inRoomTempModeratorAvatarId = buf.getInt();
			//System.out.println("inRoomTempModeratorAvatarId  " + inRoomTempModeratorAvatarId);
			inRoomTempModeratorlist.add(inRoomTempModeratorAvatarId);
			//System.out.println("inRoomTempModeratorlist  " + inRoomTempModeratorlist);
			TempModeratorStationId = buf.getInt();
			//System.out.println("TempModeratorStationId  " + TempModeratorStationId);
			TempModeratorAvaName.deserialize(buf);
			//System.out.println("TempModeratorAvaName  " + TempModeratorAvaName.getString());
			TempModeratorAvaAddress.deserialize(buf);
			//System.out.println("TempModeratorAvaAddress  " + TempModeratorAvaAddress.getString());
			TempModeratorAvaTyp = buf.getInt();
			//System.out.println("TempModeratorAvaTyp  " + TempModeratorAvaTyp);
			Zahl2 = buf.getInt();
			//System.out.println("Zahl2  " + Zahl2);			
		}
		
		//inRoomBanned
		bannedCount = buf.getInt();
		//System.out.println("bannedCount  " + bannedCount);
		for (int i=0;i < bannedCount  ; i++){
			inRoomBannedAvatarId = buf.getInt();
			//System.out.println("inRoomBannedAvatarId  " + inRoomBannedAvatarId);
			inRoomBanlist.add(inRoomBannedAvatarId);
			//System.out.println("inRoomBanlist  " + inRoomBanlist);
			BannedStationId = buf.getInt();
			//System.out.println("BannedStationId  " + BannedStationId);
			BannedAvaName.deserialize(buf);
			//System.out.println("BannedAvaName  " + BannedAvaName.getString());
			BannedAvaAddress.deserialize(buf);
			//System.out.println("BannedAvaAddress  " + BannedAvaAddress.getString());
			BannedAvaTyp = buf.getInt();
			//System.out.println("BannedAvaTyp  " + BannedAvaTyp);
			Zahl2 = buf.getInt();
			//System.out.println("Zahl2  " + Zahl2);			
		}
		
		//inRoomInvited
		inviteCount = buf.getInt();
		//System.out.println("inviteCount  " + inviteCount);
		for (int i=0;i < inviteCount  ; i++){
			inRoomInviteAvatarId = buf.getInt();
			//System.out.println("inRoomInviteAvatarId  " + inRoomInviteAvatarId);
			inRoomInvitelist.add(inRoomInviteAvatarId);
			//System.out.println("inRoomInvitelist  " + inRoomInvitelist);
			InviteStationId = buf.getInt();
			//System.out.println("InviteStationId  " + InviteStationId);
			InviteAvaName.deserialize(buf);
			//System.out.println("InviteAvaName  " + InviteAvaName.getString());
			InviteAvaAddress.deserialize(buf);
			//System.out.println("InviteAvaAddress  " + InviteAvaAddress.getString());
			InviteAvaTyp = buf.getInt();
			//System.out.println("InviteAvaTyp  " + InviteAvaTyp);
			Zahl2 = buf.getInt();
			//System.out.println("Zahl2  " + Zahl2);			
		}
		//inRoomVoice
		VoiceCount = buf.getInt();
		//System.out.println("VoiceCount  " + VoiceCount);
		for (int i=0;i < VoiceCount  ; i++){
			inRoomVoiceAvatarId = buf.getInt();
			//System.out.println("inRoomVoiceAvatarId  " + inRoomVoiceAvatarId);
			inRoomVoicelist.add(inRoomVoiceAvatarId);
			//System.out.println("inRoomVoicelist  " + inRoomVoicelist);
			VoiceStationId = buf.getInt();
			//System.out.println("VoiceStationId  " + VoiceStationId);
			VoiceAvaName.deserialize(buf);
			//System.out.println("VoiceAvaName  " + VoiceAvaName.getString());
			VoiceAvaAddress.deserialize(buf);
			//System.out.println("VoiceAvaAddress  " + VoiceAvaAddress.getString());
			VoiceAvaTyp = buf.getInt();
			//System.out.println("VoiceAvaTyp  " + VoiceAvaTyp);
			Zahl2 = buf.getInt();
			//System.out.println("Zahl2  " + Zahl2);			
		}
	}

	private void serializeWithLocalAvatarsOnly() {
		// TODO Auto-generated method stub		
	}

	public ChatUnicodeString getcreatorName() {
		return name;
	}
	
	public ChatUnicodeString getAddress() {
		return Address;
	}
	
	public ChatUnicodeString getroomPrefix() {
		return roomPrefix;
	}
	
	public int getroomFailID() {
		return roomFailID;
	}
	public void setRoomPrefix(ChatUnicodeString roomPrefix) {
		this.roomPrefix = roomPrefix;
	}
	
	public int getSrcAvatarId() {
		return srcAvatarId;
	}
	
	public int getdestAvatarId() {
		return destAvatarId;
	}
	
	public int getavatarCount() {
		return avatarCount;
	}
	
	public TIntArrayList getinRoomAvatarlist() {  
		return inRoomAvatarlist;
	}
	
	public ChatUnicodeString getAvaAddress() {
		return AvaAddress;
	}
	
	public ChatUnicodeString getAvaName() {
		return AvaName;
	}
	
	public int getinRoomAvatarId() {
		return inRoomAvatarId;
	}

	public void setSrcAvatarId(int srcAvatarId) {
		this.srcAvatarId = srcAvatarId;
	}

	public ChatUnicodeString getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(ChatUnicodeString srcAddress) {
		this.srcAddress = srcAddress;
	}

	public ChatUnicodeString getRoomAddress() {
		return roomAddress;
	}

	public void setRoomAddress(ChatUnicodeString roomAddress) {
		this.roomAddress = roomAddress;
	}

	public int getRoomAttributes() {
		return roomAttributes;
	}

	public void setRoomAttributes(int roomAttributes) {
		this.roomAttributes = roomAttributes;
	}

	public ChatUnicodeString getRoomName() {
		return roomName;
	}

	public void setRoomName(ChatUnicodeString roomName) {
		this.roomName = roomName;
	}

	public ChatUnicodeString getRoomTopic() {
		return roomTopic;
	}

	public void setRoomTopic(ChatUnicodeString roomTopic) {
		this.roomTopic = roomTopic;
	}

	public ChatUnicodeString getRoomPassword() {
		return roomPassword;
	}

	public void setRoomPassword(ChatUnicodeString roomPassword) {
		this.roomPassword = roomPassword;
	}

	public int getMaxRoomSize() {
		return maxRoomSize;
	}

	public void setMaxRoomSize(int maxRoomSize) {
		this.maxRoomSize = maxRoomSize;
	}
	
	public TIntList getDestAvatarIdList() {
		return (TIntList) destAvatarIdList;
	}
	
	//inRoomAdmin
	public int getAdminCount() {
		return adminCount;
	}
	
	public TIntArrayList getinRoomAdminlist() {  
		return inRoomAdminlist;
	}
	
	//inRoomModerator
	public int getModeratorCount() {
		return moderatorCount;
	}
	
	public TIntArrayList getinRoomModeratorlist() {  
		return inRoomModeratorlist;
	}
	
	//inRoomTempModerator
	public int getTempModeratorCount() {
		return tempModeratorCount;
	}
	
	public TIntArrayList getinRoomTempModeratorlist() {  
		return inRoomTempModeratorlist;
	}
	
	//inRoomBan
	public int getBanCount() {
		return bannedCount;
	}
	
	public TIntArrayList getinRoomBanlist() {  
		return inRoomBanlist;
	}
	
	//inRoomInvite
	public int getInviteCount() {
		return inviteCount;
	}
	
	public TIntArrayList getinRoomInvitelist() {  
		return inRoomInvitelist;
	}
	
	//inRoomVoice
	public int getVoiceCount() {
		return VoiceCount;
	}
	
	public TIntArrayList getinRoomVoicelist() {  
		return inRoomVoicelist;
	}


	//public void setDestAvatarIdList(TIntList destAvatarIdList) {
	//	this.destAvatarIdList = destAvatarIdList;
	//}
	
}
