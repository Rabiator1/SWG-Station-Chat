package chat;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import io.netty.channel.Channel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import static org.fusesource.leveldbjni.JniDBFactory.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import chat.protocol.message.MDestroyRoom;
import chat.protocol.message.MFriendLogin;
import chat.protocol.message.MFriendLogout;
import chat.protocol.message.MForcedLogout;
import chat.protocol.message.MLeaveRoom;
import chat.protocol.message.MEnterRoom;
import chat.protocol.message.MPersistentMessage;
import chat.protocol.message.MRoomMessage;
import chat.protocol.message.MSendInstantMessage;
import chat.protocol.request.RAddBan;
import chat.protocol.request.RAddFriend;
import chat.protocol.request.RAddIgnore;
import chat.protocol.request.RAddInvite;
import chat.protocol.request.RAddModerator;
import chat.protocol.request.RCreateRoom;
import chat.protocol.request.RDestroyRoom;
import chat.protocol.request.RDestroyAvatar;
import chat.protocol.request.REnterRoom;
import chat.protocol.request.RFailoverReloginAvatar;
import chat.protocol.request.RFailoverRecreateRoom;
import chat.protocol.request.RGetAnyAvatar;
import chat.protocol.request.RGetRoom;
import chat.protocol.request.RGetRoomSummaries;
import chat.protocol.request.RKickAvatar;
import chat.protocol.request.RLeaveRoom;
import chat.protocol.request.RLoginAvatar;
import chat.protocol.request.RLogoutAvatar;
import chat.protocol.request.RRemoveBan;
import chat.protocol.request.RRemoveFriend;
import chat.protocol.request.RRemoveIgnore;
import chat.protocol.request.RRemoveInvite;
import chat.protocol.request.RRemoveModerator;
import chat.protocol.request.RSendInstantMessage;
import chat.protocol.request.RSendPersistentMessage;
import chat.protocol.request.RSendRoomMessage;
import chat.protocol.response.ResAddBan;
import chat.protocol.response.ResAddFriend;
import chat.protocol.response.ResAddIgnore;
import chat.protocol.response.ResAddInvite;
import chat.protocol.response.ResAddModerator;
import chat.protocol.response.ResCreateRoom;
import chat.protocol.response.ResDestroyRoom;
import chat.protocol.response.ResDestroyAvatar;
import chat.protocol.response.ResEnterRoom;
import chat.protocol.response.ResFailoverRecreateRoom;
import chat.protocol.response.ResFailLoginAvatar;
import chat.protocol.response.ResGetAnyAvatar;
import chat.protocol.response.ResGetRoom;
import chat.protocol.response.ResGetRoomSummaries;
import chat.protocol.response.ResKickAvatar;
import chat.protocol.response.ResLeaveRoom;
import chat.protocol.response.ResLoginAvatar;
import chat.protocol.response.ResLogoutAvatar;
import chat.protocol.response.ResRemoveBan;
import chat.protocol.response.ResRemoveFriend;
import chat.protocol.response.ResRemoveIgnore;
import chat.protocol.response.ResRemoveInvite;
import chat.protocol.response.ResRemoveModerator;
import chat.protocol.response.ResSendInstantMessage;
import chat.protocol.response.ResSendPersistentMessage;
import chat.protocol.response.ResSendRoomMessage;
import chat.protocol.response.ResUnregisterRoom;
import chat.protocol.response.ResponseResult;
import chat.util.ChatUnicodeString;
import chat.util.Config;
import chat.util.PersistentMessageStatus;


public class ChatApiServer {
	
	private final Config config;
	private static ChatApiServer instance;
	private ChatApiTcpListener registrar;
	private ChatApiTcpListener gateway;
	private static Logger logger = LogManager.getLogger();
	private static final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
	    protected Kryo initialValue() {
	        Kryo kryo = new Kryo();
	        //TODO: configure kryo instance, customize settings
	        return kryo;
	    };
	};

	private ExecutorService packetProcessor = Executors.newSingleThreadExecutor();
	private List<ChatApiClient> connectedClusters = new CopyOnWriteArrayList<>();
	private DB avatarDb;
	private DB chatRoomDb;
	private DB mailDb;
	private final Map<String, ChatAvatar> onlineAvatars = new HashMap<>();
	private ExecutorService persister;
	private int highestAvatarId;
	private TIntIntMap roomMessageIdMap = new TIntIntHashMap();
	private Map<String, ChatRoom> roomMap = new HashMap<>();
	private Map<String, ChatRoom> roomMap1 = new HashMap<>();
	private Map<String, ChatRoom> roomMap2 = new HashMap<>();
	private Map<String, ChatRoom> roomMap3 = new HashMap<>();
	private Map<String, ChatRoom> roomMap4 = new HashMap<>();
	private Map<String, ChatRoom> roomMap5 = new HashMap<>();
	private Map<String, ChatRoom> roomMap6 = new HashMap<>();
	private Map<String, ChatRoom> roomMap7 = new HashMap<>();
	private Map<String, ChatRoom> roomMap8 = new HashMap<>();
	private Map<String, ChatRoom> roomMap9 = new HashMap<>();
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public ChatApiServer() {
		config = new Config("config.cfg");
	}
	
	public static void main(String[] args) throws InterruptedException, IOException {
		instance = new ChatApiServer();
		System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
		instance.start();
		while(true) {
			Thread.sleep(10000);
		}
	}
	
	private void start() throws IOException {
		persister = Executors.newFixedThreadPool(config.getInt("persisterThreads"));
		Options levelDbOptions = new Options();
		levelDbOptions.createIfMissing(true);
		levelDbOptions.cacheSize(config.getInt("levelDbCache"));
		avatarDb = factory.open(new File("./db/chatAvatars"), levelDbOptions);
		chatRoomDb = factory.open(new File("./db/chatRooms"), levelDbOptions);
		if(config.getBoolean("compressMails"))
			levelDbOptions.compressionType(CompressionType.SNAPPY);
		mailDb = factory.open(new File("./db/mails"), levelDbOptions);
		getHighestAvatarIdFromDatabase();
		registrar = new ChatApiTcpListener(config.getInt("registrarPort"));
		gateway = new ChatApiTcpListener(config.getInt("gatewayPort"));
		registrar.start();
		gateway.start();
	}
	
	private void getHighestAvatarIdFromDatabase() {
		byte[] buf = avatarDb.get("highestId".getBytes());
		if(buf == null) {
			highestAvatarId = 0;
			Integer idObj = new Integer(highestAvatarId);
			Output output = new Output(new ByteArrayOutputStream());
			kryos.get().writeClassAndObject(output, idObj);
			avatarDb.put("highestId".getBytes(), output.toBytes());
			output.close();
			return;
		}
		Input input = new Input(new ByteArrayInputStream(buf));
		Integer idObj = (Integer) kryos.get().readClassAndObject(input);
		highestAvatarId = idObj;
		input.close();
	}
	
	private int getNewAvatarId() {
		int nextAvatarId = ++highestAvatarId;
		Integer idObj = new Integer(highestAvatarId);
		Output output = new Output(new ByteArrayOutputStream());
		kryos.get().writeClassAndObject(output, idObj);
		avatarDb.put("highestId".getBytes(), output.toBytes());
		output.close();
		return nextAvatarId;
	}
	
	public void handleLoginAvatar(ChatApiClient cluster, RLoginAvatar request) {
		String fullAddress = request.getAddress().getString() + "+" + request.getName().getString();
		ResLoginAvatar response = new ResLoginAvatar();
		response.setTrack(request.getTrack());
		if(onlineAvatars.get(fullAddress) != null) {
			response.setAvatar(onlineAvatars.get(fullAddress));
			response.setResult(ResponseResult.CHATRESULT_DUPLICATELOGIN);
			cluster.send(response.serialize());
			return;
		}
		ChatAvatar avatar = getAvatarFromDatabase(fullAddress);
		if(avatar != null) {
			System.out.println("Got avatar from DB "  + avatar.getAddressAndName());
			loginAvatar(cluster, avatar);
		} else {
			avatar = createAvatar(cluster, request, fullAddress);
			System.out.println("Creating new avatar "   + fullAddress);
		}
		response.setAvatar(avatar);
		response.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(response.serialize());
		
		//send all avatars in all rooms that avatar has entered

		ChatAvatar systemavatar = onlineAvatars.get(cluster.getAddress().getString().toUpperCase() + "+" + "SYSTEM");
		avatar.setLoggedIn(true);
		
	//Avatar and UserCount
		TIntList UserCount = new TIntArrayList();
   	 int avatarCount = getHighestAvatarId();
	  for (int a = 0;a < avatarCount + 1  ; a++){ 
		ChatAvatar nextuser = getAvatarById(a);
	 	if (nextuser != null){ 
			int UserIdcount = nextuser.getUserId();
			//System.out.println("User Count : "   + nextuser.getUserId());
			if(!UserCount.contains(UserIdcount)) {
				UserCount.add(UserIdcount);
				}
	 		}
		}
		System.out.println("Avatar Count total: "   + onlineAvatars.size());
		System.out.println("Account Count total: "   + UserCount.size());
	}
	
	public void handleFailoverReloginAvatar(ChatApiClient cluster, RFailoverReloginAvatar request) {
		String fullAddress = request.getAddress().getString().toUpperCase() + "+" + request.getName().getString();
		ResFailLoginAvatar response = new ResFailLoginAvatar();
		response.setTrack(request.getTrack());
		if(onlineAvatars.get(fullAddress) != null) {
			response.setAvatar(onlineAvatars.get(fullAddress));
			response.setResult(ResponseResult.CHATRESULT_DUPLICATELOGIN);
			cluster.send(response.serialize());
			return;
		}
		ChatAvatar avatar = getAvatarFromDatabase(fullAddress);
		if(avatar != null) {
			System.out.println("Got avatar from DB "  + avatar.getAddressAndName());
			loginAvatar(cluster, avatar);
		}
		 else {
				avatar = createAvatar(cluster, request, fullAddress);
				System.out.println("Creating new avatar "  + fullAddress);
			}
		response.setAvatar(avatar);
		System.out.println("____________________________________________");
		System.out.println("Avatar failover logged in " + avatar.getAddressAndName());
		/*System.out.println("AvatarID " + avatar.getAvatarId());
		System.out.println("UserID " + avatar.getUserId());
		System.out.println("AvatarName " + avatar.getName().getString());
		System.out.println("AvatarAddress " + avatar.getAddress().getString());
		System.out.println("AvatarGateway " + avatar.getGateway().getString());
		System.out.println("AvatarServer " + avatar.getServer().getString());
		System.out.println("AvatarGatewayID " + avatar.getGatewayId());
		System.out.println("AvatarServerID " + avatar.getServerId());
		System.out.println("AvatarLoginLocation " + avatar.getLoginLocation().getString());
		System.out.println("AvatarAttribute " + avatar.getAttributes());*/
		System.out.println("____________________________________________");
		
		response.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(response.serialize());
		
		//Avatar and UserCount
		TIntList UserCount = new TIntArrayList();
   	 int avatarCount = getHighestAvatarId();
	  for (int a = 0;a < avatarCount + 1  ; a++){ 
		ChatAvatar nextuser = getAvatarById(a);
	 	if (nextuser != null){ 
			int UserIdcount = nextuser.getUserId();
			//System.out.println("User Count : "   + nextuser.getUserId());
			if(!UserCount.contains(UserIdcount)) {
				UserCount.add(UserIdcount);
				}
	 		}
		}
		System.out.println("Avatar Count total: "   + onlineAvatars.size());
		System.out.println("Account Count total: "   + UserCount.size());
	}
	
	public void handleInstantMessage(ChatApiClient cluster, RSendInstantMessage req) {
		// TODO: add ignore list check and gm stuff if needed
		int srcAvatarId = req.getSrcAvatarId();
		ResSendInstantMessage res = new ResSendInstantMessage();
		res.setTrack(req.getTrack());
		ChatAvatar srcAvatar = getAvatarById(srcAvatarId);
		if(srcAvatar == null) {
			res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		ChatAvatar destAvatar = onlineAvatars.get(req.getDestAddress().getString().toUpperCase() + "+" + req.getDestName().getString());
		if(destAvatar == null || destAvatar.isInvisible()) {
			res.setResult(ResponseResult.CHATRESULT_DESTAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			//System.out.println("Dest Avatar not online " + req.getDestAddress().getString().toUpperCase() + "+" + req.getDestName().getString());
			return;
		}
		if(!srcAvatar.isGm() && !srcAvatar.isSuperGm() && destAvatar.hasIgnore(srcAvatar.getAvatarId())) {
			res.setResult(ResponseResult.CHATRESULT_IGNORING);
			cluster.send(res.serialize());
			return;					
		}
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		MSendInstantMessage msg = new MSendInstantMessage();
		msg.setDestAvatarId(destAvatar.getAvatarId());
		msg.setMessage(req.getMessage());
		msg.setSrcAvatar(srcAvatar);
		msg.setOob(req.getOob());
		destAvatar.getCluster().send(msg.serialize());
		cluster.send(res.serialize());
	}
	
	private ChatAvatar createAvatar(ChatApiClient cluster, RLoginAvatar request, String fullAddress) {
		ChatAvatar avatar = new ChatAvatar();
		avatar.setAvatarId(getNewAvatarId());
		avatar.setAddress(request.getAddress());
		avatar.setName(request.getName());
		avatar.setAttributes(request.getLoginAttributes());
		avatar.setLoginLocation(request.getLoginLocation());
		avatar.setUserId(request.getUserId());
		persistAvatar(avatar, false);
		loginAvatar(cluster, avatar);
		return avatar;
	}
	
	private ChatAvatar createAvatar(ChatApiClient cluster, RFailoverReloginAvatar request, String fullAddress) {
		ChatAvatar avatar = new ChatAvatar();
		avatar.setAvatarId(getNewAvatarId());
		avatar.setAddress(request.getAddress());
		avatar.setName(request.getName());
		avatar.setAttributes(request.getLoginAttributes());
		avatar.setLoginLocation(request.getLoginLocation());
		avatar.setUserId(request.getUserId());
		persistAvatar(avatar, false);
		loginAvatar(cluster, avatar);
		return avatar;
	}
	
	public void persistAvatar(ChatAvatar avatar, boolean async) {
		if(async)
			persister.execute(() -> persistAvatar(avatar));
		else
			persistAvatar(avatar);
	}
	
	private void persistAvatar(ChatAvatar avatar) {
		Output output = new Output(new ByteArrayOutputStream());
		kryos.get().writeClassAndObject(output, avatar);
		avatarDb.put(avatar.getAddressAndName().getBytes(), output.toBytes());
		output.close();
	}

	private void loginAvatar(ChatApiClient cluster, ChatAvatar avatar) {
		onlineAvatars.put(avatar.getAddressAndName(), avatar);
		avatar.setCluster(cluster);
		avatar.setLoggedIn(true);
		//TODO: add chat room + friends status updates etc.
		for(int mailId : avatar.getMailIds().toArray()) {
			PersistentMessage pm = getPersistentMessageFromDb(mailId);
			if(pm == null)
				continue;
		    int daysUntilDelete = config.getInt("deleteMailTimerInDays");
		    int elapsedDays = (int) TimeUnit.MILLISECONDS.toDays((System.currentTimeMillis() / 1000) - pm.getTimestamp());
		    if(elapsedDays >= daysUntilDelete) {
		    	destroyPersistentMessage(avatar, pm);
		    	continue;
		    }
			avatar.getPmList().put(mailId, pm);
		}
		if(!avatar.isInvisible()) {
			for(ChatAvatar avatar2 : onlineAvatars.values()) {
				if(avatar2.hasFriend(avatar.getAvatarId())) {
					MFriendLogin msg = new MFriendLogin();
					msg.setDestAvatarId(avatar2.getAvatarId());
					msg.setFriendAvatar(avatar);
					msg.setFriendAddress(avatar.getAddress());
					avatar2.getFriend(avatar.getAvatarId()).setStatus((short) 1);
					avatar2.getCluster().send(msg.serialize());
				}
			}
			
			System.out.println("____________________________________________");
			System.out.println("Avatar logged in " + avatar.getAddressAndName());
			/*System.out.println("AvatarID " + avatar.getAvatarId());
			System.out.println("UserID " + avatar.getUserId());
			System.out.println("AvatarName " + avatar.getName().getString());
			System.out.println("AvatarAddress " + avatar.getAddress().getString());
			System.out.println("AvatarGateway " + avatar.getGateway().getString());
			System.out.println("AvatarServer " + avatar.getServer().getString());
			System.out.println("AvatarGatewayID " + avatar.getGatewayId());
			System.out.println("AvatarServerID " + avatar.getServerId());
			System.out.println("AvatarLoginLocation " + avatar.getLoginLocation().getString());
			System.out.println("AvatarAttribute " + avatar.getAttributes());*/
			System.out.println("____________________________________________");
			
			
			//hier weitermachennnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
			ChatAvatar systemavatar = onlineAvatars.get(cluster.getAddress().getString().toUpperCase() + "+" + "SYSTEM");
			for(ChatRoom room : roomMap.values()) {
				TIntArrayList destAvatarIdList = new TIntArrayList();
				room.getAvatarList().stream().map(ChatAvatar::getAvatarId).forEach(destAvatarIdList::add);
				if(destAvatarIdList.contains(avatar.getAvatarId())) {
	    			
				
					MEnterRoom msg = new MEnterRoom() ;
					msg.setSrcAvatar(avatar);
					msg.setCreator(systemavatar);
					msg.setAvatarName(avatar.getName());
					msg.setAvatarAddress(avatar.getAddress());
					msg.setGateway(avatar.getGateway());
					msg.setServer(avatar.getServer());
					//msg.setRoomAddress(room.getRoomAddress());
					msg.setRoomId(room.getRoomId());
					cluster.send(msg.serialize());
					
				}
			}
		}
		for(ChatFriend friend : avatar.getFriendsList()) {
			if(onlineAvatars.containsKey(friend.getFullAddress()))
				friend.setStatus((short) 1);
			else
				friend.setStatus((short) 0);
		}
		//System.out.println("Login roomMessageIdMap before remove " + roomMessageIdMap.values());
		roomMessageIdMap.remove(avatar.getAvatarId());
		if(avatar.getName().getString().equalsIgnoreCase("system")) {
			createRootGameRoom(cluster, avatar);
			//createRootClusterRoom(cluster, avatar);
		}
	}

	public ChatAvatar getAvatarFromDatabase(String fullAddress) {
		byte[] buf = avatarDb.get(fullAddress.getBytes());
		if(buf == null)
			return null;
		Input input = new Input(new ByteArrayInputStream(buf));
		ChatAvatar avatar = (ChatAvatar) kryos.get().readClassAndObject(input);
		input.close();
		if(avatar == null)
			return null;
		return avatar;
	}
	
	public ChatAvatar getAvatarById(int avatarId) {
		return onlineAvatars.values().stream().filter(avatar -> avatar.getAvatarId() == avatarId).findFirst().orElse(null);
	}

	public final Config getConfig() {
		return config;
	}

	public static ChatApiServer getInstance() {
		return instance;
	}

	public ChatApiTcpListener getRegistrar() {
		return registrar;
	}

	public void setRegistrar(ChatApiTcpListener registrar) {
		this.registrar = registrar;
	}

	public ChatApiTcpListener getGateway() {
		return gateway;
	}

	public void setGateway(ChatApiTcpListener gateway) {
		this.gateway = gateway;
	}

	public ExecutorService getPacketProcessor() {
		return packetProcessor;
	}

	public void setPacketProcessor(ExecutorService packetProcessor) {
		this.packetProcessor = packetProcessor;
	}

	public List<ChatApiClient> getConnectedClusters() {
		return connectedClusters;
	}

	public void setConnectedClusters(List<ChatApiClient> connectedClusters) {
		this.connectedClusters = connectedClusters;
	}

	public DB getAvatarDb() {
		return avatarDb;
	}

	public void setAvatarDb(DB avatarDb) {
		this.avatarDb = avatarDb;
	}

	public DB getChatRoomDb() {
		return chatRoomDb;
	}

	public void setChatRoomDb(DB chatRoomDb) {
		this.chatRoomDb = chatRoomDb;
	}

	public DB getMailDb() {
		return mailDb;
	}

	public void setMailDb(DB mailDb) {
		this.mailDb = mailDb;
	}
	
	public ChatApiClient getClusterByChannel(Channel channel) {
		for(ChatApiClient cluster : connectedClusters) {
			if(cluster.getChannel() == channel)
				return cluster;
		}
		return null;
	}
	
	public void addCluster(Channel channel) {
		ChatApiClient cluster = new ChatApiClient(channel);
		connectedClusters.add(cluster);
	}
	
	public void removeCluster(Channel channel) {
		if (getClusterByChannel(channel).getAddress() != null){
			System.out.println("SWG Server is offline " + getClusterByChannel(channel).getAddress().getString());	
        	 int avatarCount = getHighestAvatarId();
        	  for (int a = 0;a < avatarCount + 1  ; a++){ 
        		ChatAvatar nextuser = getAvatarById(a);
        	 	if (nextuser != null){  
        	 		//System.out.println("auto read UserID " + nextuser.getAddress().getString().toUpperCase());
        	 		if (nextuser.getAddress().getString().toUpperCase().equals( getClusterByChannel(channel).getAddress().getString().toUpperCase())){ 
        		logoutAvatar(nextuser,true );
        		//logoutAvatar(systemuser,true );
        		//System.out.println("auto logout UserID" + a);
        	 		}
            	}
        	  }
      		for(ChatRoom room : roomMap.values()) {
    			if((room.getRoomAddress().getString().contains(getClusterByChannel(channel).getAddress().getString()))) {
    				roomMap.clear();
    				//System.out.println("roomMap cleared");
    				break;
    			}
    		}
      		for(ChatRoom room : roomMap1.values()) {
    			if((room.getRoomAddress().getString().contains(getClusterByChannel(channel).getAddress().getString()))) {
    				roomMap1.clear();
    				//System.out.println("roomMap1 cleared");
    				break;
    			}
    		}
      		for(ChatRoom room : roomMap2.values()) {
    			if((room.getRoomAddress().getString().contains(getClusterByChannel(channel).getAddress().getString()))) {
    				roomMap2.clear();
    				//System.out.println("roomMap2 cleared");
    				break;
    			}
    		}
      		for(ChatRoom room : roomMap3.values()) {
    			if((room.getRoomAddress().getString().contains(getClusterByChannel(channel).getAddress().getString()))) {
    				roomMap3.clear();
    				//System.out.println("roomMap3 cleared");
    				break;
    			}
    		}
      		for(ChatRoom room : roomMap4.values()) {
    			if((room.getRoomAddress().getString().contains(getClusterByChannel(channel).getAddress().getString()))) {
    				roomMap4.clear();
    				//System.out.println("roomMap4 cleared");
    				break;
    			}
    		}
      		for(ChatRoom room : roomMap5.values()) {
    			if((room.getRoomAddress().getString().contains(getClusterByChannel(channel).getAddress().getString()))) {
    				roomMap5.clear();
    				//System.out.println("roomMap5 cleared");
    				break;
    			}
    		}
      		for(ChatRoom room : roomMap6.values()) {
    			if((room.getRoomAddress().getString().contains(getClusterByChannel(channel).getAddress().getString()))) {
    				roomMap6.clear();
    				//System.out.println("roomMap6 cleared");
    				break;
    			}
    		}
      		for(ChatRoom room : roomMap7.values()) {
    			if((room.getRoomAddress().getString().contains(getClusterByChannel(channel).getAddress().getString()))) {
    				roomMap7.clear();
    				//System.out.println("roomMap7 cleared");
    				break;
    			}
    		}
      		for(ChatRoom room : roomMap8.values()) {
    			if((room.getRoomAddress().getString().contains(getClusterByChannel(channel).getAddress().getString()))) {
    				roomMap8.clear();
    				//System.out.println("roomMap8 cleared");
    				break;
    			}
    		}
      		for(ChatRoom room : roomMap9.values()) {
    			if((room.getRoomAddress().getString().contains(getClusterByChannel(channel).getAddress().getString()))) {
    				roomMap9.clear();
    				//System.out.println("roomMap9 cleared");
    				break;
    			}
    		}
		}
		
		connectedClusters.remove(getClusterByChannel(channel));
		//System.out.println("Cluster removed");
		// TODO: add disconnect handling
	}

	public Map<String, ChatAvatar> getOnlineAvatars() {
		return onlineAvatars;
	}

	public ExecutorService getPersister() {
		return persister;
	}

	public void setPersister(ExecutorService persister) {
		this.persister = persister;
	}

	public int getHighestAvatarId() {
		return highestAvatarId;
	}

	public void setHighestAvatarId(int highestAvatarId) {
		this.highestAvatarId = highestAvatarId;
	}

	public void handleLogoutAvatar(ChatApiClient cluster, RLogoutAvatar req) {
		ResLogoutAvatar res = new ResLogoutAvatar();
		res.setTrack(req.getTrack());
		ChatAvatar avatar = getAvatarById(req.getAvatarId());
		ChatAvatar systemavatar = onlineAvatars.get(cluster.getAddress().getString().toUpperCase() + "+" + "SYSTEM");
		if(avatar == null) {
			res.setResult(ResponseResult.CHATRESULT_DESTAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		logoutAvatar(avatar, true);
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(res.serialize());	
		
		//send all avatars in all rooms that avatar has leaved
		for(ChatRoom room : roomMap.values()) {
			if(room.isInRoom(avatar)) {
			
			    MLeaveRoom msg = new MLeaveRoom();
    			msg.setSrcAvatarId(req.getAvatarId());
    			msg.setRoomId(room.getRoomId());
    			cluster.send(msg.serialize());

				room.leaveRoom(avatar);
				room.leaveRoom(systemavatar);
			}
		}
		
		for(ChatRoom room : roomMap1.values()) {
			if(room.isInRoom(avatar)) {			
			    MLeaveRoom msg = new MLeaveRoom();
    			msg.setSrcAvatarId(req.getAvatarId());
    			msg.setRoomId(room.getRoomId());
    			cluster.send(msg.serialize());
				room.leaveRoom(avatar);
				room.leaveRoom(systemavatar);
			}
		}
		
		for(ChatRoom room : roomMap2.values()) {
			if(room.isInRoom(avatar)) {			
			    MLeaveRoom msg = new MLeaveRoom();
    			msg.setSrcAvatarId(req.getAvatarId());
    			msg.setRoomId(room.getRoomId());
    			cluster.send(msg.serialize());
				room.leaveRoom(avatar);
				room.leaveRoom(systemavatar);
			}
		}
		
		for(ChatRoom room : roomMap3.values()) {
			if(room.isInRoom(avatar)) {			
			    MLeaveRoom msg = new MLeaveRoom();
    			msg.setSrcAvatarId(req.getAvatarId());
    			msg.setRoomId(room.getRoomId());
    			cluster.send(msg.serialize());
				room.leaveRoom(avatar);
				room.leaveRoom(systemavatar);
			}
		}
		
		for(ChatRoom room : roomMap4.values()) {
			if(room.isInRoom(avatar)) {			
			    MLeaveRoom msg = new MLeaveRoom();
    			msg.setSrcAvatarId(req.getAvatarId());
    			msg.setRoomId(room.getRoomId());
    			cluster.send(msg.serialize());
				room.leaveRoom(avatar);
				room.leaveRoom(systemavatar);
			}
		}
		
		for(ChatRoom room : roomMap5.values()) {
			if(room.isInRoom(avatar)) {			
			    MLeaveRoom msg = new MLeaveRoom();
    			msg.setSrcAvatarId(req.getAvatarId());
    			msg.setRoomId(room.getRoomId());
    			cluster.send(msg.serialize());
				room.leaveRoom(avatar);
				room.leaveRoom(systemavatar);
			}
		}
		
		for(ChatRoom room : roomMap6.values()) {
			if(room.isInRoom(avatar)) {			
			    MLeaveRoom msg = new MLeaveRoom();
    			msg.setSrcAvatarId(req.getAvatarId());
    			msg.setRoomId(room.getRoomId());
    			cluster.send(msg.serialize());
				room.leaveRoom(avatar);
				room.leaveRoom(systemavatar);
			}
		}
		
		for(ChatRoom room : roomMap7.values()) {
			if(room.isInRoom(avatar)) {			
			    MLeaveRoom msg = new MLeaveRoom();
    			msg.setSrcAvatarId(req.getAvatarId());
    			msg.setRoomId(room.getRoomId());
    			cluster.send(msg.serialize());
				room.leaveRoom(avatar);
				room.leaveRoom(systemavatar);
			}
		}
		
		for(ChatRoom room : roomMap8.values()) {
			if(room.isInRoom(avatar)) {			
			    MLeaveRoom msg = new MLeaveRoom();
    			msg.setSrcAvatarId(req.getAvatarId());
    			msg.setRoomId(room.getRoomId());
    			cluster.send(msg.serialize());
				room.leaveRoom(avatar);
				room.leaveRoom(systemavatar);
			}
		}
		
		for(ChatRoom room : roomMap9.values()) {
			if(room.isInRoom(avatar)) {			
			    MLeaveRoom msg = new MLeaveRoom();
    			msg.setSrcAvatarId(req.getAvatarId());
    			msg.setRoomId(room.getRoomId());
    			cluster.send(msg.serialize());
				room.leaveRoom(avatar);
				room.leaveRoom(systemavatar);
			}
		}
	//Avatar and UserCount
		TIntList UserCount = new TIntArrayList();
   	 int avatarCount = getHighestAvatarId();
	  for (int a = 0;a < avatarCount + 1  ; a++){ 
		ChatAvatar nextuser = getAvatarById(a);
	 	if (nextuser != null){ 
			int UserIdcount = nextuser.getUserId();
			//System.out.println("User Count : "   + nextuser.getUserId());
			if(!UserCount.contains(UserIdcount)) {
				UserCount.add(UserIdcount);
				}
	 		}
		}
		System.out.println("Avatar Count total: "   + onlineAvatars.size());
		System.out.println("Account Count total: "   + UserCount.size());
	}

	void logoutAvatar(ChatAvatar avatar, boolean persist) {
		onlineAvatars.remove(avatar.getAddressAndName());
		avatar.setLoggedIn(false);
		ChatAvatar systemavatar = onlineAvatars.get(avatar.getAddress().getString().toUpperCase() + "+" + "SYSTEM");
		if(persist){			
		persistAvatar(avatar, true);
		}
		
		//TODO: remove chat room + friends status updates etc.
		for(PersistentMessage pm : avatar.getPmList().valueCollection()) {
			persistPersistentMessage(pm, true);
		}
		for(ChatAvatar avatar2 : onlineAvatars.values()) {
			if(avatar2.hasFriend(avatar.getAvatarId())) {
				MFriendLogout msg = new MFriendLogout();
				msg.setDestAvatarId(avatar2.getAvatarId());
				msg.setFriendAvatar(avatar);
				msg.setFriendAddress(avatar.getAddress());
				avatar2.getFriend(avatar.getAvatarId()).setStatus((short) 0);
				avatar2.getCluster().send(msg.serialize());
			}
		}
		roomMessageIdMap.remove(avatar.getAvatarId());
	}
 
	protected void persistPersistentMessage(PersistentMessage pm, boolean async) {
		if(async)
			persister.execute(() -> persistPersistentMessage(pm));
		else
			persistPersistentMessage(pm);
	}

	public void handleDestroyAvatar(ChatApiClient cluster, RDestroyAvatar req) {
		ResDestroyAvatar res = new ResDestroyAvatar();
		res.setTrack(req.getTrack());
		ChatAvatar avatar = getAvatarById(req.getAvatarId());
		if(avatar == null) {
			res.setResult(ResponseResult.CHATRESULT_DESTAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		destroyAvatar(avatar);
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(res.serialize());		
	}
	
	public void destroyPersistentMessage(ChatAvatar avatar, PersistentMessage pm) {
		avatar.removeMail(pm);
		mailDb.delete(ByteBuffer.allocate(4).putInt(pm.getMessageId()).array());
	}

	private void destroyAvatar(ChatAvatar avatar) {
		//TODO: destroy mails, update friends/ignore lists, update chat rooms etc.
		logoutAvatar(avatar, false);
		avatarDb.delete(avatar.getAddressAndName().getBytes());
	}

	public void handleGetAnyAvatar(ChatApiClient cluster, RGetAnyAvatar req) {
		ResGetAnyAvatar res = new ResGetAnyAvatar();
		res.setTrack(req.getTrack());
		String fullAddress = req.getAddress() + "+" + req.getName();
		ChatAvatar avatar = onlineAvatars.get(fullAddress);
		if(avatar != null) {
			res.setResult(ResponseResult.CHATRESULT_SUCCESS);
			res.setAvatar(avatar);
			res.setLoggedIn(true);
			cluster.send(res.serialize());
			return;		
		}
		avatar = getAvatarFromDatabase(fullAddress);
		if(avatar != null) {
			res.setResult(ResponseResult.CHATRESULT_SUCCESS);
			res.setAvatar(avatar);
			res.setLoggedIn(false);
			cluster.send(res.serialize());
			return;					
		}
		res.setResult(ResponseResult.CHATRESULT_DESTAVATARDOESNTEXIST);
		res.setLoggedIn(false);
		cluster.send(res.serialize());
	}
	
	private int getNewPmId() {
		boolean found = false;
		int mailId = 0;
		while(!found) {
			mailId = ThreadLocalRandom.current().nextInt();
			if(mailId != 0 && getPersistentMessageFromDb(mailId) == null)
				found = true;
		}
		return mailId;
	}
	
	protected PersistentMessage getPersistentMessageFromDb(int messageId) {
		byte[] key = ByteBuffer.allocate(4).putInt(messageId).array();
		byte[] value = mailDb.get(key);
		if(value == null)
			return null;
		Input input = new Input(new ByteArrayInputStream(value));
		PersistentMessage pm = (PersistentMessage) kryos.get().readClassAndObject(input);
		return pm;
	}
	
	private void persistPersistentMessage(PersistentMessage pm) {
		byte[] key = ByteBuffer.allocate(4).putInt(pm.getMessageId()).array();
		Output output = new Output(new ByteArrayOutputStream());
		kryos.get().writeClassAndObject(output, pm);
		mailDb.put(key, output.toBytes());
	}
	
//################################################################################
	public void handleSendPersistentMessage(ChatApiClient cluster, RSendPersistentMessage req) {
		ResSendPersistentMessage res = new ResSendPersistentMessage();
		res.setTrack(req.getTrack());
		ChatAvatar srcAvatar = null;

		if(req.isAvatarPresence() != 0) {
			srcAvatar = getAvatarById(req.getSrcAvatarId());
		} else {
			String srcName = req.getSrcName().getString();
			if(srcName.contains(" "))
				srcName = srcName.split(" ")[0];
			srcAvatar = onlineAvatars.get(cluster.getAddress().getString() + "+" + srcName);
		}
		if(srcAvatar == null) {
			if(req.isAvatarPresence() == 0) {
				// for auctioneer, planetary civil authority, and .stf sender names
				srcAvatar = onlineAvatars.get(cluster.getAddress().getString().toUpperCase() + "+" + "SYSTEM");				
			} else {
				res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
				cluster.send(res.serialize());
				return;					
			}
		}
		String destFullAdress = req.getDestAddress().getString().toUpperCase() + "+" + req.getDestName().getString();
		ChatAvatar destAvatar = onlineAvatars.get(destFullAdress);
		if(destAvatar == null) {
			destAvatar = getAvatarFromDatabase(destFullAdress);
			if(destAvatar == null) {
				res.setResult(ResponseResult.CHATRESULT_DESTAVATARDOESNTEXIST);
				cluster.send(res.serialize());
				return;					
			}
		}
		if(!srcAvatar.isGm() && !srcAvatar.isSuperGm() && destAvatar.hasIgnore(srcAvatar.getAvatarId())) {
			res.setResult(ResponseResult.CHATRESULT_IGNORING);
			cluster.send(res.serialize());
			return;					
		}
		PersistentMessage pm = new PersistentMessage();
		pm.setAvatarId(destAvatar.getAvatarId());
		pm.setMessage(req.getMessage());
		pm.setOob(req.getOob());
		pm.setMessageId(getNewPmId());
		pm.setCategory(req.getCategory());
		pm.setSenderAddress(srcAvatar.getAddress());
		if(req.isAvatarPresence() == 0){
			pm.setSenderName(req.getSrcName());
			}
		else
			pm.setSenderName(srcAvatar.getName());
		pm.setStatus(PersistentMessageStatus.NEW);
		pm.setSubject(req.getSubject());
		pm.setTimestamp((int) (System.currentTimeMillis() / 1000));
		persistPersistentMessage(pm);
		persistAvatar(destAvatar, true);
		destAvatar.addMail(pm);
		//System.out.println(req.getSubject().getString());
		//System.out.println(req.getMessage().getString());
		//System.out.println(req.getOob().getString());
		
		if(destAvatar.getCluster() != null && destAvatar.isLoggedIn()) {
			MPersistentMessage msg = new MPersistentMessage();
			msg.setPm(pm);
			destAvatar.getCluster().send(msg.serialize());
		}
		persistPersistentMessage(pm);
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		res.setMessageId(pm.getMessageId());
		cluster.send(res.serialize());
	}
	
	public ChatApiClient getClusterByAddress(ChatUnicodeString address) {
		for(ChatApiClient cluster : connectedClusters) {
			if(cluster.getAddress().getString().equals(address.getString()))
				return cluster;
		}
		return null;
	}
	
	public void handleAddFriend(ChatApiClient cluster, RAddFriend req) {
		ResAddFriend res = new ResAddFriend();
		res.setTrack(req.getTrack());
		ChatAvatar avatar = getAvatarById(req.getSrcAvatarId());
		//System.out.println(req.getDestName().getString());
		if(avatar == null) {
			res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		if(avatar.hasFriend(req.getDestAddress(), req.getDestName())) {
			res.setResult(ResponseResult.CHATRESULT_DUPLICATEFRIEND);
			cluster.send(res.serialize());
			return;
		}
		String fullAddress = req.getDestAddress().getString().toString().toUpperCase() + "+" + req.getDestName().getString();
		ChatAvatar destAvatar = onlineAvatars.get(fullAddress);
		if(destAvatar == null) {
			destAvatar = getAvatarFromDatabase(fullAddress);
			if(destAvatar == null) {
				res.setResult(ResponseResult.CHATRESULT_DESTAVATARDOESNTEXIST);
				cluster.send(res.serialize());
				return;					
			}
		}
		if(destAvatar == avatar) {
			res.setResult(ResponseResult.CHATRESULT_INVALID_INPUT);
			cluster.send(res.serialize());
			return;
		}
		ChatFriend friend = new ChatFriend();
		friend.setAvatarId(destAvatar.getAvatarId());
		friend.setAddress(destAvatar.getAddress());
		friend.setName(destAvatar.getName());
		friend.setComment(req.getComment());
		friend.setStatus((short) (destAvatar.isLoggedIn() ? 1 : 0));
		avatar.addFriend(friend);
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(res.serialize());
		if(friend.getStatus() != 0) {
			MFriendLogin msg = new MFriendLogin();
			msg.setDestAvatarId(avatar.getAvatarId());
			msg.setFriendAvatar(destAvatar);
			msg.setFriendAddress(destAvatar.getAddress());
			cluster.send(msg.serialize());
		}
	}

	public void handleRemoveFriend(ChatApiClient cluster, RRemoveFriend req) {
		ResRemoveFriend res = new ResRemoveFriend();
		res.setTrack(req.getTrack());
		ChatAvatar avatar = getAvatarById(req.getSrcAvatarId());
		if(avatar == null) {
			res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		if(!avatar.hasFriend(req.getDestAddress(), req.getDestName())) {
			res.setResult(ResponseResult.CHATRESULT_FRIENDNOTFOUND);
			cluster.send(res.serialize());
			return;
		}
		avatar.removeFriend(req.getDestAddress(), req.getDestName());
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(res.serialize());
	}

	public void handleAddIgnore(ChatApiClient cluster, RAddIgnore req) {
		ResAddIgnore res = new ResAddIgnore();
		res.setTrack(req.getTrack());
		ChatAvatar avatar = getAvatarById(req.getSrcAvatarId());
		if(avatar == null) {
			res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		if(avatar.hasIgnore(req.getDestAddress(), req.getDestName())) {
			res.setResult(ResponseResult.CHATRESULT_DUPLICATEIGNORE);
			cluster.send(res.serialize());
			return;
		}
		String fullAddress = req.getDestAddress().getString().toUpperCase() + "+" + req.getDestName().getString();
		//System.out.println(fullAddress);
		ChatAvatar destAvatar = onlineAvatars.get(fullAddress);
		if(destAvatar == null) {
			destAvatar = getAvatarFromDatabase(fullAddress);
			if(destAvatar == null) {
				res.setResult(ResponseResult.CHATRESULT_DESTAVATARDOESNTEXIST);
				cluster.send(res.serialize());
				return;					
			}
		}
		if(destAvatar == avatar) {
			res.setResult(ResponseResult.CHATRESULT_INVALID_INPUT);
			cluster.send(res.serialize());
			return;
		}
		ChatIgnore ignore = new ChatIgnore();
		ignore.setAvatarId(destAvatar.getAvatarId());
		ignore.setAddress(destAvatar.getAddress());
		ignore.setName(destAvatar.getName());
		avatar.addIgnore(ignore);
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(res.serialize());
	}

	public void handleRemoveIgnore(ChatApiClient cluster, RRemoveIgnore req) {
		ResRemoveIgnore res = new ResRemoveIgnore();
		res.setTrack(req.getTrack());
		ChatAvatar avatar = getAvatarById(req.getSrcAvatarId());
		if(avatar == null) {
			res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		if(!avatar.hasIgnore(req.getDestAddress(), req.getDestName())) {
			res.setResult(ResponseResult.CHATRESULT_IGNORENOTFOUND);
			cluster.send(res.serialize());
			return;
		}
		avatar.removeIgnore(req.getDestAddress(), req.getDestName());
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(res.serialize());
	}

	public TIntIntMap getRoomMessageIdMap() {
		return roomMessageIdMap;
	}

	public void setRoomMessageIdMap(TIntIntMap roomMessageIdMap) {
		this.roomMessageIdMap = roomMessageIdMap;
	}

	public void handleGetRoomSummaries(ChatApiClient cluster, RGetRoomSummaries req){
		ResGetRoomSummaries res = new ResGetRoomSummaries();
		res.setTrack(req.getTrack());
		List<ChatRoom> rooms = new ArrayList<>();
		rooms.clear();
		
		//up to 10 swg servers
		if(cluster.getAddress() != null ) {
		for(ChatRoom room : roomMap.values()) {
			if((room.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("handle get roomSummarie room Map "+ roomMap.size() + " "  + cluster.getAddress().getString());
				rooms.add(room);
			}
		}
		for(ChatRoom room : roomMap1.values()) {
			if((room.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("handle get roomSummarie room Map1 "+ roomMap1.size() + " "  + cluster.getAddress().getString());
				rooms.add(room);
			}
		}
		for(ChatRoom room : roomMap2.values()) {
			if((room.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("handle get roomSummarie room Map2 "+ roomMap2.size() + " "  + cluster.getAddress().getString());
				rooms.add(room);
			}
		}
		for(ChatRoom room : roomMap3.values()) {
			if((room.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("handle get roomSummarie room Map3 "+ roomMap3.size() + " "  + cluster.getAddress().getString());
				rooms.add(room);
			}
		}
		for(ChatRoom room : roomMap4.values()) {
			if((room.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("handle get roomSummarie room Map4 "+ roomMap4.size() + " "  + cluster.getAddress().getString());
				rooms.add(room);
			}
		}
		for(ChatRoom room : roomMap5.values()) {
			if((room.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("handle get roomSummarie room Map5 "+ roomMap5.size() + " "  + cluster.getAddress().getString());
				rooms.add(room);
			}
		}
		for(ChatRoom room : roomMap6.values()) {
			if((room.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("handle get roomSummarie room Map6 "+ roomMap6.size() + " "  + cluster.getAddress().getString());
				rooms.add(room);
			}
		}
		for(ChatRoom room : roomMap7.values()) {
			if((room.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("handle get roomSummarie room Map7 "+ roomMap7.size() + " "  + cluster.getAddress().getString());
				rooms.add(room);
			}
		}
		for(ChatRoom room : roomMap8.values()) {
			if((room.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("handle get roomSummarie room Map8 "+ roomMap8.size() + " "  + cluster.getAddress().getString());
				rooms.add(room);
			}
		}
		for(ChatRoom room : roomMap9.values()) {
			if((room.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("handle get roomSummarie room Map9 "+ roomMap9.size() + " "  + cluster.getAddress().getString());
				rooms.add(room);
			}
		}
	}
		
		//System.out.println("_________________________________________________");
		res.setRooms(rooms);
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(res.serialize());
	}
	

//################################################################################
	public void handleCreateRoom(ChatApiClient cluster, RCreateRoom req) {
		ResCreateRoom res = new ResCreateRoom();
		res.setTrack(req.getTrack());
		ChatAvatar avatar = getAvatarById(req.getSrcAvatarId());
		//System.out.println("handle create room Cluster " + cluster.getAddress().getString());
		if(avatar == null) {
			res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		if(roomMap.get(req.getRoomAddress().getString() + "+" + req.getRoomName().getString()) != null) {
			//System.out.println("handle create room allready exist Map " + req.getRoomAddress().getString() + "+" + req.getRoomName().getString());
			res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
			cluster.send(res.serialize());
			return;
		}
		if(roomMap1.get(req.getRoomAddress().getString() + "+" + req.getRoomName().getString()) != null) {
			//System.out.println("handle create room allready exist Map1 " + req.getRoomAddress().getString() + "+" + req.getRoomName().getString());
			res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
			cluster.send(res.serialize());
			return;
		}
		if(roomMap2.get(req.getRoomAddress().getString() + "+" + req.getRoomName().getString()) != null) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
			cluster.send(res.serialize());
			return;
		}
		if(roomMap3.get(req.getRoomAddress().getString().toUpperCase() + "+" + req.getRoomName().getString()) != null) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
			cluster.send(res.serialize());
			return;
		}
		if(roomMap4.get(req.getRoomAddress().getString().toUpperCase() + "+" + req.getRoomName().getString()) != null) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
			cluster.send(res.serialize());
			return;
		}
		if(roomMap5.get(req.getRoomAddress().getString().toUpperCase() + "+" + req.getRoomName().getString()) != null) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
			cluster.send(res.serialize());
			return;
		}
		if(roomMap6.get(req.getRoomAddress().getString().toUpperCase() + "+" + req.getRoomName().getString()) != null) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
			cluster.send(res.serialize());
			return;
		}
		if(roomMap7.get(req.getRoomAddress().getString().toUpperCase() + "+" + req.getRoomName().getString()) != null) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
			cluster.send(res.serialize());
			return;
		}
		if(roomMap8.get(req.getRoomAddress().getString().toUpperCase() + "+" + req.getRoomName().getString()) != null) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
			cluster.send(res.serialize());
			return;
		}
		if(roomMap9.get(req.getRoomAddress().getString().toUpperCase() + "+" + req.getRoomName().getString()) != null) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
			cluster.send(res.serialize());
			return;
		}
		
		ChatRoom room = new ChatRoom();
		room.setCreateTime((int) (System.currentTimeMillis() / 1000));
		room.setRoomId(getNewRoomId());
		//System.out.println("getNewRoomId " + room.getRoomId());
		room.setCreatorId(avatar.getAvatarId());
		//System.out.println("getAvatarId " + avatar.getAvatarId());
		room.setCreatorAddress(avatar.getAddress());
		//System.out.println("Avatar getAddress " + avatar.getAddress().getString());
		room.setCreatorName(avatar.getName());
		//System.out.println("getAvatarName " + avatar.getName().getString());
		room.setRoomAddress(new ChatUnicodeString(req.getRoomAddress().getString().toUpperCase() + "+" + req.getRoomName().getString()));
		//System.out.println("setRoomAddress " + (req.getRoomAddress().getString().toUpperCase() + "+" + req.getRoomName().getString()));
		room.setRoomAttributes(req.getRoomAttributes());
		//System.out.println("setRoomAttributes " + (req.getRoomAttributes()));
		room.setRoomName(req.getRoomName());
		//System.out.println("setRoomName " + (req.getRoomName().getString()));
		room.setRoomPassword(req.getRoomPassword());
		room.setRoomTopic(req.getRoomTopic());
		//System.out.println("setRoomTopic " + (req.getRoomTopic().getString()));
		room.setNodeLevel(room.getRoomAddress().getString().split("\\+").length);
		//System.out.println("setNodeLevel " + (room.getRoomAddress().getString().split("\\+").length));
		room.setMaxRoomSize(req.getMaxRoomSize());
		//System.out.println("setMaxRoomSize " + (req.getMaxRoomSize()));
		room.addAvatar(avatar);
		room.addAdmin(avatar);
		

		//uo to 10 swg servers
		for(ChatRoom rooma : roomMap.values()) {
			if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("cR Map put " + (req.getRoomName().getString()));
				roomMap.put(room.getRoomAddress().getString(), room);
				res.setResult(ResponseResult.CHATRESULT_SUCCESS);
				res.setRoom(room);
				List<ChatRoom> parentRooms = getParentRooms(room);
				res.setExtraRooms(parentRooms);
				cluster.send(res.serialize());
				return;
			}
		}
		for(ChatRoom rooma : roomMap1.values()) {
			if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("cR Map1 put " + (req.getRoomName().getString()));
				roomMap1.put(room.getRoomAddress().getString(), room);
				res.setResult(ResponseResult.CHATRESULT_SUCCESS);
				res.setRoom(room);
				List<ChatRoom> parentRooms = getParentRooms(room);
				res.setExtraRooms(parentRooms);
				cluster.send(res.serialize());
				return;
			}
		}
		for(ChatRoom rooma : roomMap2.values()) {
			if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("cR Map2 put " + (req.getRoomName().getString()));
				roomMap2.put(room.getRoomAddress().getString(), room);
				res.setResult(ResponseResult.CHATRESULT_SUCCESS);
				res.setRoom(room);
				List<ChatRoom> parentRooms = getParentRooms(room);
				res.setExtraRooms(parentRooms);
				cluster.send(res.serialize());
				return;
			}
		}
		for(ChatRoom rooma : roomMap3.values()) {
			if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("cR Map3 put " + (req.getRoomName().getString()));
				roomMap3.put(room.getRoomAddress().getString(), room);
				res.setResult(ResponseResult.CHATRESULT_SUCCESS);
				res.setRoom(room);
				List<ChatRoom> parentRooms = getParentRooms(room);
				res.setExtraRooms(parentRooms);
				cluster.send(res.serialize());
				return;
			}
		}
		for(ChatRoom rooma : roomMap4.values()) {
			if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("cR Map4 put " + (req.getRoomName().getString()));
				roomMap4.put(room.getRoomAddress().getString(), room);
				res.setResult(ResponseResult.CHATRESULT_SUCCESS);
				res.setRoom(room);
				List<ChatRoom> parentRooms = getParentRooms(room);
				res.setExtraRooms(parentRooms);
				cluster.send(res.serialize());
				return;
			}
		}
		for(ChatRoom rooma : roomMap5.values()) {
			if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("cR Map5 put " + (req.getRoomName().getString()));
				roomMap5.put(room.getRoomAddress().getString(), room);
				res.setResult(ResponseResult.CHATRESULT_SUCCESS);
				res.setRoom(room);
				List<ChatRoom> parentRooms = getParentRooms(room);
				res.setExtraRooms(parentRooms);
				cluster.send(res.serialize());
				return;
			}
		}
		for(ChatRoom rooma : roomMap6.values()) {
			if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("cR Map6 put " + (req.getRoomName().getString()));
				roomMap6.put(room.getRoomAddress().getString(), room);
				res.setResult(ResponseResult.CHATRESULT_SUCCESS);
				res.setRoom(room);
				List<ChatRoom> parentRooms = getParentRooms(room);
				res.setExtraRooms(parentRooms);
				cluster.send(res.serialize());
				return;
			}
		}
		for(ChatRoom rooma : roomMap7.values()) {
			if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("cR Map7 put " + (req.getRoomName().getString()));
				roomMap7.put(room.getRoomAddress().getString(), room);
				res.setResult(ResponseResult.CHATRESULT_SUCCESS);
				res.setRoom(room);
				List<ChatRoom> parentRooms = getParentRooms(room);
				res.setExtraRooms(parentRooms);
				cluster.send(res.serialize());
				return;
			}
		}
		for(ChatRoom rooma : roomMap8.values()) {
			if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("cR Map8 put " + (req.getRoomName().getString()));
				roomMap8.put(room.getRoomAddress().getString(), room);
				res.setResult(ResponseResult.CHATRESULT_SUCCESS);
				res.setRoom(room);
				List<ChatRoom> parentRooms = getParentRooms(room);
				res.setExtraRooms(parentRooms);
				cluster.send(res.serialize());
				return;
			}
		}
		for(ChatRoom rooma : roomMap9.values()) {
			if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
				//System.out.println("cR Map9 put " + (req.getRoomName().getString()));
				roomMap9.put(room.getRoomAddress().getString(), room);
				res.setResult(ResponseResult.CHATRESULT_SUCCESS);
				res.setRoom(room);
				List<ChatRoom> parentRooms = getParentRooms(room);
				res.setExtraRooms(parentRooms);
				cluster.send(res.serialize());
				return;
			}
		}
	}
	
	//################################################################################	
		public void handleFailoverRecreateRoom(ChatApiClient cluster, RFailoverRecreateRoom req) {
			ResFailoverRecreateRoom res = new ResFailoverRecreateRoom();
			res.setTrack(req.getTrack());
			ChatAvatar avatar = getAvatarById(req.getSrcAvatarId());
			int notonline = 0;
			if(avatar == null) {
				String fullAddress = req.getAddress().getString().toUpperCase() + "+" + req.getcreatorName().getString();
				avatar = getAvatarFromDatabase(fullAddress);
				//System.out.println("CreatorName not online anymore " + req.getAddress().getString().toUpperCase() + "+" + req.getcreatorName().getString());
				notonline = 1;
			}


			if(roomMap.get(req.getRoomAddress().getString() + "+" + req.getRoomName().getString()) != null) {
				//System.out.println("handle create room allready exist Map " + req.getRoomAddress().getString() + "+" + req.getRoomName().getString());
				res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
				cluster.send(res.serialize());
				return;
			}
			if(roomMap1.get(req.getRoomAddress().getString() + "+" + req.getRoomName().getString()) != null) {
				//System.out.println("handle create room allready exist Map1 " + req.getRoomAddress().getString() + "+" + req.getRoomName().getString());
				res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
				cluster.send(res.serialize());
				return;
			}
			if(roomMap2.get(req.getRoomAddress().getString() + "+" + req.getRoomName().getString()) != null) {
				res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
				cluster.send(res.serialize());
				return;
			}
			if(roomMap3.get(req.getRoomAddress().getString().toUpperCase() + "+" + req.getRoomName().getString()) != null) {
				res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
				cluster.send(res.serialize());
				return;
			}
			if(roomMap4.get(req.getRoomAddress().getString().toUpperCase() + "+" + req.getRoomName().getString()) != null) {
				res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
				cluster.send(res.serialize());
				return;
			}
			if(roomMap5.get(req.getRoomAddress().getString().toUpperCase() + "+" + req.getRoomName().getString()) != null) {
				res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
				cluster.send(res.serialize());
				return;
			}
			if(roomMap6.get(req.getRoomAddress().getString().toUpperCase() + "+" + req.getRoomName().getString()) != null) {
				res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
				cluster.send(res.serialize());
				return;
			}
			if(roomMap7.get(req.getRoomAddress().getString().toUpperCase() + "+" + req.getRoomName().getString()) != null) {
				res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
				cluster.send(res.serialize());
				return;
			}
			if(roomMap8.get(req.getRoomAddress().getString().toUpperCase() + "+" + req.getRoomName().getString()) != null) {
				res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
				cluster.send(res.serialize());
				return;
			}
			if(roomMap9.get(req.getRoomAddress().getString().toUpperCase() + "+" + req.getRoomName().getString()) != null) {
				res.setResult(ResponseResult.CHATRESULT_ROOM_ALREADYEXISTS);
				cluster.send(res.serialize());
				return;
			}
			
			ChatRoom room = new ChatRoom();
	         room.setCreatorName(avatar.getName());
	         room.setCreatorAddress(avatar.getAddress());
	         room.setCreatorId(avatar.getAvatarId());
	         room.setRoomName(req.getRoomName());
	         room.setRoomTopic(req.getRoomTopic());
	         room.setRoomPassword(req.getRoomPassword());
	         room.setNodeLevel(req.getRoomAddress().getString().split("\\+").length);
	         room.setRoomAddress(new ChatUnicodeString(req.getRoomAddress().getString()));
	         System.out.println("RoomAddress recreated " + (req.getRoomAddress().getString()));
	         room.setRoomAttributes(req.getRoomAttributes());
	         room.setMaxRoomSize(req.getMaxRoomSize());
	         room.setRoomId(req.getroomFailID());
	         room.setCreateTime((int) (System.currentTimeMillis() / 1000));	         
	        
	         if(notonline == 0) {
	        	 room.addAvatar(avatar); 
	         }			
			room.addAdmin(avatar);
			
			
			//System.out.println("getAvatarList before" + room.getAvatarList().toString());
			//System.out.println("getavatarCount in ChatApi " + req.getavatarCount());
			
			//inRoomAvatars
			if (req.getavatarCount() > 1 ){
				for (int i = 0; i < req.getavatarCount();i++){
					int  destavatar2 = req.getinRoomAvatarlist().get(i);	
					
					//System.out.println("destavatar2  " + destavatar2);
					ChatAvatar destavatar = getAvatarById(destavatar2);
					if (destavatar != null){
					//ChatAvatar destavatar = getAvatarById(req.getinRoomAvatarId());
					//ChatAvatar destavatar = onlineAvatars.get(req.getAvaAddress().getString().toUpperCase() + "+" + req.getAvaName().getString());
					if(!room.isInRoom(destavatar)){
						room.addAvatar(destavatar);
						res.setGotRoom(true);
					}		
						}
				}				
			}
			//inRoomAdmin
			if (req.getAdminCount() > 1 ){
				for (int i = 0; i < req.getAdminCount();i++){
					int  destavatar3 = req.getinRoomAdminlist().get(i);	
					//System.out.println("destAdmin2  " + destavatar3);
					ChatAvatar destavatar = getAvatarById(destavatar3);
					//ChatAvatar destavatar = getAvatarById(req.getinRoomAvatarId());
					//ChatAvatar destavatar = onlineAvatars.get(req.getAvaAddress().getString().toUpperCase() + "+" + req.getAvaName().getString());
					if(!room.isAdmin(destavatar)){
						room.addAdmin(destavatar);
					}					
				}				
			}
			
			//inRoomModerator
			if (req.getModeratorCount() > 0 ){
				for (int i = 0; i < req.getModeratorCount();i++){
					int  destavatar4 = req.getinRoomModeratorlist().get(i);	
					//System.out.println("destModerator2  " + destavatar4);
					ChatAvatar destavatar = getAvatarById(destavatar4);
					//ChatAvatar destavatar = getAvatarById(req.getinRoomAvatarId());
					//ChatAvatar destavatar = onlineAvatars.get(req.getAvaAddress().getString().toUpperCase() + "+" + req.getAvaName().getString());
					if(!room.isModerator(destavatar)){
						room.addModerator(destavatar);
					}					
				}				
			}
			
			//inRoomBan
			if (req.getBanCount() > 0 ){
				for (int i = 0; i < req.getBanCount();i++){
					int  destavatar5 = req.getinRoomBanlist().get(i);	
					//System.out.println("destBann2  " + destavatar5);
					ChatAvatar destavatar = getAvatarById(destavatar5);
					//ChatAvatar destavatar = getAvatarById(req.getinRoomAvatarId());
					//ChatAvatar destavatar = onlineAvatars.get(req.getAvaAddress().getString().toUpperCase() + "+" + req.getAvaName().getString());
					if(!room.isBanned(destavatar)){
						room.addBan(destavatar);
					}					
				}				
			}
			
			//inRoomInvite
			if (req.getInviteCount() > 0 ){
				for (int i = 0; i < req.getInviteCount();i++){
					int  destavatar6 = req.getinRoomInvitelist().get(i);	
					//System.out.println("destInvite2  " + destavatar6);
					ChatAvatar destavatar = getAvatarById(destavatar6);
					if (destavatar != null){
					//ChatAvatar destavatar = getAvatarById(req.getinRoomAvatarId());
					//ChatAvatar destavatar = onlineAvatars.get(req.getAvaAddress().getString().toUpperCase() + "+" + req.getAvaName().getString());
					if(!room.isInvited(destavatar)){
						room.AddInvite(destavatar);
					}					
											}
				}				
			}

			//uo to 10 swg servers
			for(ChatRoom rooma : roomMap.values()) {
				if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
					//System.out.println("cR Map put " + (req.getRoomName().getString()));
					roomMap.put(room.getRoomAddress().getString(), room);
					res.setResult(ResponseResult.CHATRESULT_SUCCESS);
					res.setRoom(room);
					List<ChatRoom> parentRooms = getParentRooms(room);
					res.setExtraRooms(parentRooms);
					cluster.send(res.serialize());
					return;
				}
			}
			for(ChatRoom rooma : roomMap1.values()) {
				if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
					//System.out.println("cR Map1 put " + (req.getRoomName().getString()));
					roomMap1.put(room.getRoomAddress().getString(), room);
					res.setResult(ResponseResult.CHATRESULT_SUCCESS);
					res.setRoom(room);
					List<ChatRoom> parentRooms = getParentRooms(room);
					res.setExtraRooms(parentRooms);
					cluster.send(res.serialize());
					return;
				}
			}
			for(ChatRoom rooma : roomMap2.values()) {
				if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
					//System.out.println("cR Map2 put " + (req.getRoomName().getString()));
					roomMap2.put(room.getRoomAddress().getString(), room);
					res.setResult(ResponseResult.CHATRESULT_SUCCESS);
					res.setRoom(room);
					List<ChatRoom> parentRooms = getParentRooms(room);
					res.setExtraRooms(parentRooms);
					cluster.send(res.serialize());
					return;
				}
			}
			for(ChatRoom rooma : roomMap3.values()) {
				if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
					//System.out.println("cR Map3 put " + (req.getRoomName().getString()));
					roomMap3.put(room.getRoomAddress().getString(), room);
					res.setResult(ResponseResult.CHATRESULT_SUCCESS);
					res.setRoom(room);
					List<ChatRoom> parentRooms = getParentRooms(room);
					res.setExtraRooms(parentRooms);
					cluster.send(res.serialize());
					return;
				}
			}
			for(ChatRoom rooma : roomMap4.values()) {
				if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
					//System.out.println("cR Map4 put " + (req.getRoomName().getString()));
					roomMap4.put(room.getRoomAddress().getString(), room);
					res.setResult(ResponseResult.CHATRESULT_SUCCESS);
					res.setRoom(room);
					List<ChatRoom> parentRooms = getParentRooms(room);
					res.setExtraRooms(parentRooms);
					cluster.send(res.serialize());
					return;
				}
			}
			for(ChatRoom rooma : roomMap5.values()) {
				if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
					//System.out.println("cR Map5 put " + (req.getRoomName().getString()));
					roomMap5.put(room.getRoomAddress().getString(), room);
					res.setResult(ResponseResult.CHATRESULT_SUCCESS);
					res.setRoom(room);
					List<ChatRoom> parentRooms = getParentRooms(room);
					res.setExtraRooms(parentRooms);
					cluster.send(res.serialize());
					return;
				}
			}
			for(ChatRoom rooma : roomMap6.values()) {
				if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
					//System.out.println("cR Map6 put " + (req.getRoomName().getString()));
					roomMap6.put(room.getRoomAddress().getString(), room);
					res.setResult(ResponseResult.CHATRESULT_SUCCESS);
					res.setRoom(room);
					List<ChatRoom> parentRooms = getParentRooms(room);
					res.setExtraRooms(parentRooms);
					cluster.send(res.serialize());
					return;
				}
			}
			for(ChatRoom rooma : roomMap7.values()) {
				if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
					//System.out.println("cR Map7 put " + (req.getRoomName().getString()));
					roomMap7.put(room.getRoomAddress().getString(), room);
					res.setResult(ResponseResult.CHATRESULT_SUCCESS);
					res.setRoom(room);
					List<ChatRoom> parentRooms = getParentRooms(room);
					res.setExtraRooms(parentRooms);
					cluster.send(res.serialize());
					return;
				}
			}
			for(ChatRoom rooma : roomMap8.values()) {
				if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
					//System.out.println("cR Map8 put " + (req.getRoomName().getString()));
					roomMap8.put(room.getRoomAddress().getString(), room);
					res.setResult(ResponseResult.CHATRESULT_SUCCESS);
					res.setRoom(room);
					List<ChatRoom> parentRooms = getParentRooms(room);
					res.setExtraRooms(parentRooms);
					cluster.send(res.serialize());
					return;
				}
			}
			for(ChatRoom rooma : roomMap9.values()) {
				if((rooma.getRoomAddress().getString().contains(cluster.getAddress().getString()))) {
					//System.out.println("cR Map9 put " + (req.getRoomName().getString()));
					roomMap9.put(room.getRoomAddress().getString(), room);
					res.setResult(ResponseResult.CHATRESULT_SUCCESS);
					res.setRoom(room);
					List<ChatRoom> parentRooms = getParentRooms(room);
					res.setExtraRooms(parentRooms);
					cluster.send(res.serialize());
					return;
				}
			}
		
			
		}
		
//################################################################################	
	public void handleDestroyRoom(ChatApiClient cluster, RDestroyRoom req) {
		ResDestroyRoom res = new ResDestroyRoom();
		res.setTrack(req.getTrack());
		ChatAvatar avatar = getAvatarById(req.getSrcAvatarId());
		ChatAvatar systemavatar = onlineAvatars.get(cluster.getAddress().getString().toUpperCase() + "+" + "SYSTEM");
		if(avatar == null) {
			res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		ChatRoom room = roomMap.get(req.getRoomAddress().getString());
		if(room != null) {
			//System.out.println("enterroom found in map " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap1.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map1 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap2.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map2 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap3.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map3 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap4.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map4 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap5.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map5 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap6.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map6 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap7.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map7 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap8.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map8 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap9.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map9 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			res.setResult(ResponseResult.CHATRESULT_ADDRESSDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		
		
		if (req.getRoomAddress().getString().contains("group+")){
			MDestroyRoom msg = new MDestroyRoom();
			msg.setSrcAvatar(systemavatar);
			ChatRoom grouproom = roomMap.get(req.getRoomAddress().getString() + "+GroupChat");
			msg.setRoomId(grouproom.getRoomId());
			//System.out.println("Group SUB room deleted " + grouproom.getRoomAddress().getString());
			//System.out.println("Group SUB room deleted RoomId " + grouproom.getRoomId());
			cluster.send(msg.serialize());
			roomMap.remove(grouproom.getRoomAddress().getString(), grouproom);			
			
			
			MDestroyRoom msg2 = new MDestroyRoom();
			msg2.setSrcAvatar(systemavatar);
			msg2.setRoomId(room.getRoomId());
			cluster.send(msg2.serialize());
			roomMap.remove(room.getRoomAddress().getString(), room);
		}
		else if
		(req.getRoomAddress().getString().contains("guild+")){
			MDestroyRoom msg = new MDestroyRoom();
			msg.setSrcAvatar(systemavatar);
			ChatRoom guildroom = roomMap.get(req.getRoomAddress().getString() + "+GuildChat");
			msg.setRoomId(guildroom.getRoomId());
			//System.out.println("Guild SUB room deleted " + guildroom.getRoomAddress().getString());
			//System.out.println("Guild SUB room deleted RoomId " + guildroom.getRoomId());
			cluster.send(msg.serialize());
			roomMap.remove(guildroom.getRoomAddress().getString(), guildroom);			
			
			
			MDestroyRoom msg2 = new MDestroyRoom();
			msg2.setSrcAvatar(systemavatar);
			msg2.setRoomId(room.getRoomId());
			cluster.send(msg2.serialize());
			roomMap.remove(room.getRoomAddress().getString(), room);
		}
		else
		{
			MDestroyRoom msg2 = new MDestroyRoom();
			msg2.setSrcAvatar(avatar);
			msg2.setRoomId(room.getRoomId());
			cluster.send(msg2.serialize());
			roomMap.remove(room.getRoomAddress().getString(), room);
		}
	    
		res.setDestRoomId(room.getRoomId());
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(res.serialize());	

		//room.removeAvatar(avatar);
		//room.clear();
		//roomMap.remove(room.getRoomAddress().getString(), room);
		//System.out.println("room deleted " + req.getRoomAddress().getString());
		//System.out.println("room deleted RoomId " + room.getRoomId());
		
		List<ChatRoom> rooms = new ArrayList<>();
		for(ChatRoom rooma : roomMap.values()) {			
			//System.out.println("room after deleted Name" + rooms.toArray().toString());
			//System.out.println("room after del list IDs " + rooma.getRoomId());
			//if(room.getRoomAddress().getString().startsWith(req.getStartNodeAddress().getString()) && room.getRoomAddress().getString().contains(req.getRoomFilter().getString()))
			//rooms.add(room);
		}
		

	}	
//################################################################################
	public void handleGetRoom(ChatApiClient cluster, RGetRoom req) {
		ResGetRoom res = new ResGetRoom();
		res.setTrack(req.getTrack());
		

			//System.out.println("handle get room Map " + cluster.getAddress().getString());
			ChatRoom room = roomMap.get(req.getRoomAddress().getString());

			if(room == null) {
				room = roomMap1.get(req.getRoomAddress().getString());
			}			
			if(room == null) {
				room = roomMap2.get(req.getRoomAddress().getString());
			}
			if(room == null) {
				room = roomMap3.get(req.getRoomAddress().getString());
			}
			if(room == null) {
				room = roomMap4.get(req.getRoomAddress().getString());
			}
			if(room == null) {
				room = roomMap5.get(req.getRoomAddress().getString());
			}
			if(room == null) {
				room = roomMap6.get(req.getRoomAddress().getString());
			}
			if(room == null) {
				room = roomMap7.get(req.getRoomAddress().getString());
			}
			if(room == null) {
				room = roomMap8.get(req.getRoomAddress().getString());
			}
			if(room == null) {
				room = roomMap9.get(req.getRoomAddress().getString());
			}
			
			if(room == null) {
				res.setResult(ResponseResult.CHATRESULT_ADDRESSDOESNTEXIST);
				cluster.send(res.serialize());
				return;
			}
			res.setRoom(room);
			List<ChatRoom> parentRooms = getParentRooms(room);
			//List<ChatRoom> subRooms = getSubRooms(room);
			res.setExtraRooms(parentRooms);
			res.setResult(ResponseResult.CHATRESULT_SUCCESS);
			cluster.send(res.serialize());

	}

	private List<ChatRoom> getParentRooms(ChatRoom room) {
		List<ChatRoom> parentRooms = new ArrayList<>();
		for(ChatRoom parent : roomMap.values()) {
			if(room.getRoomAddress().getString().startsWith(parent.getRoomAddress().getString()))
				parentRooms.add(parent);
		}
		for(ChatRoom parent : roomMap1.values()) {
			if(room.getRoomAddress().getString().startsWith(parent.getRoomAddress().getString()))
				parentRooms.add(parent);
		}
		for(ChatRoom parent : roomMap2.values()) {
			if(room.getRoomAddress().getString().startsWith(parent.getRoomAddress().getString()))
				parentRooms.add(parent);
		}
		for(ChatRoom parent : roomMap3.values()) {
			if(room.getRoomAddress().getString().startsWith(parent.getRoomAddress().getString()))
				parentRooms.add(parent);
		}
		for(ChatRoom parent : roomMap4.values()) {
			if(room.getRoomAddress().getString().startsWith(parent.getRoomAddress().getString()))
				parentRooms.add(parent);
		}
		for(ChatRoom parent : roomMap5.values()) {
			if(room.getRoomAddress().getString().startsWith(parent.getRoomAddress().getString()))
				parentRooms.add(parent);
		}
		for(ChatRoom parent : roomMap6.values()) {
			if(room.getRoomAddress().getString().startsWith(parent.getRoomAddress().getString()))
				parentRooms.add(parent);
		}
		for(ChatRoom parent : roomMap7.values()) {
			if(room.getRoomAddress().getString().startsWith(parent.getRoomAddress().getString()))
				parentRooms.add(parent);
		}
		for(ChatRoom parent : roomMap8.values()) {
			if(room.getRoomAddress().getString().startsWith(parent.getRoomAddress().getString()))
				parentRooms.add(parent);
		}
		for(ChatRoom parent : roomMap9.values()) {
			if(room.getRoomAddress().getString().startsWith(parent.getRoomAddress().getString()))
				parentRooms.add(parent);
		}
		return parentRooms;
	}
	
	private List<ChatRoom> getSubRooms(ChatRoom room) {
		List<ChatRoom> subRooms = new ArrayList<>();
		for(ChatRoom sub : roomMap.values()) {
			if(sub.getRoomAddress().getString().startsWith(room.getRoomAddress().getString()))
				subRooms.add(sub);
		}
		for(ChatRoom sub : roomMap1.values()) {
			if(sub.getRoomAddress().getString().startsWith(room.getRoomAddress().getString()))
				subRooms.add(sub);
		}
		for(ChatRoom sub : roomMap2.values()) {
			if(sub.getRoomAddress().getString().startsWith(room.getRoomAddress().getString()))
				subRooms.add(sub);
		}
		for(ChatRoom sub : roomMap3.values()) {
			if(sub.getRoomAddress().getString().startsWith(room.getRoomAddress().getString()))
				subRooms.add(sub);
		}
		for(ChatRoom sub : roomMap4.values()) {
			if(sub.getRoomAddress().getString().startsWith(room.getRoomAddress().getString()))
				subRooms.add(sub);
		}
		for(ChatRoom sub : roomMap5.values()) {
			if(sub.getRoomAddress().getString().startsWith(room.getRoomAddress().getString()))
				subRooms.add(sub);
		}
		for(ChatRoom sub : roomMap6.values()) {
			if(sub.getRoomAddress().getString().startsWith(room.getRoomAddress().getString()))
				subRooms.add(sub);
		}
		for(ChatRoom sub : roomMap7.values()) {
			if(sub.getRoomAddress().getString().startsWith(room.getRoomAddress().getString()))
				subRooms.add(sub);
		}
		for(ChatRoom sub : roomMap8.values()) {
			if(sub.getRoomAddress().getString().startsWith(room.getRoomAddress().getString()))
				subRooms.add(sub);
		}
		for(ChatRoom sub : roomMap9.values()) {
			if(sub.getRoomAddress().getString().startsWith(room.getRoomAddress().getString()))
				subRooms.add(sub);
		}
		return subRooms;
	}
//################################################################################
	public void handleEnterRoom(ChatApiClient cluster, REnterRoom req) {
		ResEnterRoom res = new ResEnterRoom() ;
		res.setTrack(req.getTrack());
		ChatAvatar avatar = getAvatarById(req.getSrcAvatarId());
		ChatAvatar systemavatar = onlineAvatars.get(cluster.getAddress().getString().toUpperCase() + "+" + "SYSTEM");
		if(avatar == null) {
			res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		ChatRoom room = roomMap.get(req.getRoomAddress().getString());
		if(room != null) {
			//System.out.println("enterroom found in map " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap1.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map1 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap2.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map2 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap3.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map3 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap4.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map4 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap5.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map5 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap6.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map6 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap7.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map7 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap8.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map8 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap9.get(req.getRoomAddress().getString());
			//System.out.println("enterroom found in map9 " + req.getRoomAddress().getString());
		}

		
		if(room == null) {
			res.setResult(ResponseResult.CHATRESULT_ADDRESSDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		res.setRoomId(room.getRoomId());
		if(room.isOnBanList(avatar)) {
			//swg crash if send it
			//res.setResult(ResponseResult.CHATRESULT_ROOM_BANNEDAVATAR);
			//cluster.send(res.serialize());
			return;
		}
		if(room.hasPassword() && !room.validatePassword(req.getRoomPassword())) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_NOPRIVILEGES);
			cluster.send(res.serialize());
			return;
		}
		if(room.isBanned(avatar)) {
			//swg crash if send it
			//res.setResult(ResponseResult.CHATRESULT_ROOM_BANNEDAVATAR);
			//cluster.send(res.serialize());
			return;			
		}
		if(!room.isPrivate()){
		} else if (avatar.getAvatarId() == 1){
			  //System.out.println("Systemava");
		} else if (room.isOwner(avatar)){
			  //System.out.println("src is Owner");
		} else if (avatar.isGm()){
			  //System.out.println("src is Gm");
		} else if (avatar.isSuperGm()){
			  //System.out.println("src is SuperGm");	
		} else if(!room.isInvited(avatar)) {
			  //System.out.println("Ava is not invited");
			  res.setGotRoom(false);
			  res.setRoom(room);
			  //swg crash if send it
			  //res.setResult(ResponseResult.CHATRESULT_ROOM_PRIVATEROOM);
			  //cluster.send(res.serialize());
			  return;			
		}
			room.addAvatar(avatar);
			res.setGotRoom(true);

		res.setRoom(room);
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(res.serialize());	
		
		MEnterRoom msg = new MEnterRoom() ;
		msg.setSrcAvatar(avatar);
		msg.setCreator(systemavatar);
		msg.setAvatarName(avatar.getName());
		msg.setAvatarAddress(avatar.getAddress());
		msg.setSrcAvatarId(avatar.getAvatarId());
		msg.setGateway(avatar.getGateway());
		msg.setServer(avatar.getServer());
		//msg.setRoomAddress(room.getRoomAddress());
		msg.setRoomId(room.getRoomId());
		cluster.send(msg.serialize());

	}

//################################################################################
	public void handleLeaveRoom(ChatApiClient cluster, RLeaveRoom req) {
		ResLeaveRoom res = new ResLeaveRoom() ;
		res.setTrack(req.getTrack());
		ChatAvatar avatar = getAvatarById(req.getSrcAvatarId());
		ChatAvatar systemavatar = onlineAvatars.get(cluster.getAddress().getString().toUpperCase() + "+" + "SYSTEM");
		if(avatar == null) {
			res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		
		ChatRoom room = roomMap.get(req.getRoomAddress().getString());
		if(room != null) {
			//System.out.println("leaveroom found in map " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap1.get(req.getRoomAddress().getString());
			//System.out.println("leaveroom found in map1 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap2.get(req.getRoomAddress().getString());
			//System.out.println("leaveroom found in map2 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap3.get(req.getRoomAddress().getString());
			//System.out.println("leaveroom found in map3 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap4.get(req.getRoomAddress().getString());
			//System.out.println("leaveroom found in map4 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap5.get(req.getRoomAddress().getString());
			//System.out.println("leaveroom found in map5 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap6.get(req.getRoomAddress().getString());
			//System.out.println("leaveroom found in map6 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap7.get(req.getRoomAddress().getString());
			//System.out.println("leaveroom found in map7 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap8.get(req.getRoomAddress().getString());
			//System.out.println("leaveroom found in map8 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap9.get(req.getRoomAddress().getString());
			//System.out.println("leaveroom found in map9 " + req.getRoomAddress().getString());
		}
		
		
		if(room == null) {
			res.setResult(ResponseResult.CHATRESULT_ADDRESSDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		//System.out.println("Room " + room.getRoomId());
		//System.out.println("getAvatarList before leave" + room.getAvatarList().toString());
		res.setRoomId(room.getRoomId());
		room.leaveRoom(avatar);
		room.leaveRoom(systemavatar);
		
		//System.out.println("getAvatarList after leave" + room.getAvatarList().toString());
		res.setGotRoom(false);
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(res.serialize());	
	
    			MLeaveRoom msg = new MLeaveRoom();
    			//TIntArrayList destAvatarIdList = new TIntArrayList();
    			//room.getAvatarList().stream().map(ChatAvatar::getAvatarId).forEach(destAvatarIdList::add);
    			//msg.setDestAvatarIdList(destAvatarIdList);
    			msg.setSrcAvatarId(req.getSrcAvatarId());
    			msg.setRoomId(room.getRoomId());
    			cluster.send(msg.serialize());   	 

	}
	
	public Map<String, ChatRoom> getRoomMap() {
		return roomMap;
	}

	public void setRoomMap(Map<String, ChatRoom> roomMap) {
		this.roomMap = roomMap;
	}
	
	public ChatRoom getRoomById(int roomId) {
		for(ChatRoom room : roomMap.values()) {
			if(room.getRoomId() == roomId) {
				return room;
			}
		}
		for(ChatRoom room : roomMap1.values()) {
			if(room.getRoomId() == roomId) {
				return room;
			}
		}
		for(ChatRoom room : roomMap2.values()) {
			if(room.getRoomId() == roomId) {
				return room;
			}
		}
		for(ChatRoom room : roomMap3.values()) {
			if(room.getRoomId() == roomId) {
				return room;
			}
		}
		for(ChatRoom room : roomMap4.values()) {
			if(room.getRoomId() == roomId) {
				return room;
			}
		}
		for(ChatRoom room : roomMap5.values()) {
			if(room.getRoomId() == roomId) {
				return room;
			}
		}
		for(ChatRoom room : roomMap6.values()) {
			if(room.getRoomId() == roomId) {
				return room;
			}
		}
		for(ChatRoom room : roomMap7.values()) {
			if(room.getRoomId() == roomId) {
				return room;
			}
		}
		for(ChatRoom room : roomMap8.values()) {
			if(room.getRoomId() == roomId) {
				return room;
			}
		}
		for(ChatRoom room : roomMap9.values()) {
			if(room.getRoomId() == roomId) {
				return room;
			}
		}
		return null;
	}
	
	private int getNewRoomId() {
		boolean found = false;
		int roomId = 0;
		while(!found) {
			roomId = ThreadLocalRandom.current().nextInt();
			if(roomId != 0 && getRoomById(roomId) == null)
				found = true;
		}
		return roomId;
	}
	
	private void persistChatRoom(ChatRoom room) {
		Output output = new Output(new ByteArrayOutputStream());
		kryos.get().writeClassAndObject(output, room);
		byte[] value = output.toBytes();
		byte[] key = room.getRoomAddress().getString().getBytes();
		chatRoomDb.put(key, value);
	}

	public ScheduledExecutorService getScheduler() {
		return scheduler;
	}

	public void setScheduler(ScheduledExecutorService scheduler) {
		this.scheduler = scheduler;
	}
//################################################################################
	public void handleSendRoomMessage(ChatApiClient cluster, RSendRoomMessage req) {
		ResSendRoomMessage res = new ResSendRoomMessage() ;
		res.setTrack(req.getTrack());
		//System.out.println(req.getRoomAddress().getString());
		ChatAvatar avatar = getAvatarById(req.getSrcAvatarId());
		if(avatar == null) {
			res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		ChatRoom room = roomMap.get(req.getRoomAddress().getString());
		if(room != null) {
			//System.out.println("sendRoomMessage found in map " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap1.get(req.getRoomAddress().getString());
			//System.out.println("sendRoomMessage found in map1 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap2.get(req.getRoomAddress().getString());
			//System.out.println("sendRoomMessage found in map2 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap3.get(req.getRoomAddress().getString());
			//System.out.println("sendRoomMessage found in map3 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap4.get(req.getRoomAddress().getString());
			//System.out.println("sendRoomMessage found in map4 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap5.get(req.getRoomAddress().getString());
			//System.out.println("sendRoomMessage found in map5 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap6.get(req.getRoomAddress().getString());
			//System.out.println("sendRoomMessage found in map6 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap7.get(req.getRoomAddress().getString());
			//System.out.println("sendRoomMessage found in map7 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap8.get(req.getRoomAddress().getString());
			//System.out.println("sendRoomMessage found in map8 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap9.get(req.getRoomAddress().getString());
			//System.out.println("sendRoomMessage found in map9 " + req.getRoomAddress().getString());
		}
		if(room == null) {
			res.setResult(ResponseResult.CHATRESULT_ADDRESSDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		//System.out.println("sending room msg");
		res.setDestRoomId(room.getRoomId());
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(res.serialize());		
		MRoomMessage msg = new MRoomMessage();
		TIntArrayList destAvatarIdList = new TIntArrayList();
		room.getAvatarList().stream().map(ChatAvatar::getAvatarId).forEach(destAvatarIdList::add);
		msg.setDestAvatarIdList(destAvatarIdList);
		msg.setMessage(req.getMsg());
		msg.setOob(req.getOob());
		msg.setRoomId(room.getRoomId());
		msg.setMessageId(getNewRoomMsgId(avatar));
		msg.setAvatar(avatar);
		cluster.send(msg.serialize());
	}

	private int getNewRoomMsgId(ChatAvatar avatar) {
		boolean found = false;
		int msgId = 0;
		while(!found) {
			msgId = ThreadLocalRandom.current().nextInt();
			if(msgId != 0 && roomMessageIdMap.get(avatar.getAvatarId()) != msgId)
				found = true;
		}
		roomMessageIdMap.put(avatar.getAvatarId(), msgId);
		return msgId;
	}
	
	private void createRootClusterRoom(ChatApiClient cluster, ChatAvatar systemAvatar) {
		String clusterFullAddress = cluster.getAddress().getString();
		String[] splitAddress = clusterFullAddress.split("\\+");
		ChatRoom room = new ChatRoom();
		room.setCreateTime((int) (System.currentTimeMillis() / 1000));
		room.setRoomId(getNewRoomId());
		room.setCreatorId(systemAvatar.getAvatarId());
		room.setCreatorAddress(systemAvatar.getAddress());
		room.setCreatorName(systemAvatar.getName());
		room.setRoomAddress(new ChatUnicodeString(splitAddress[0] + "+" + splitAddress[1] + "+" + splitAddress[2]));
		room.setRoomAttributes(0);
		room.setRoomName(new ChatUnicodeString(splitAddress[2]));
		room.setRoomPassword(new ChatUnicodeString());
		room.setRoomTopic(new ChatUnicodeString());
		room.setMaxRoomSize(0);
		room.addAvatar(systemAvatar);
		room.addAdmin(systemAvatar);
		//System.out.println("got handle create root room2 for Cluster " + cluster.getAddress().getString());
		//System.out.println("got handle create root room2  " + room.getRoomAddress().getString()); 
		
		
			if(roomMap.values().size() == 1) {
				//System.out.println("handle create root room2 Map put " + cluster.getAddress().getString());
				roomMap.put(room.getRoomAddress().getString(), room);
				return;
			}
			if(roomMap1.values().size() == 1) {
				//System.out.println("handle create root room2 Map1 put " + cluster.getAddress().getString());
				roomMap1.put(room.getRoomAddress().getString(), room);
				return;
			}		
			if(roomMap2.values().size() == 1) {
				//System.out.println("handle create root room2 Map2 put " + cluster.getAddress().getString());
				roomMap2.put(room.getRoomAddress().getString(), room);
				return;
			}
			if(roomMap3.values().size() == 1) {
				//System.out.println("handle create root room2 Map3 put " + cluster.getAddress().getString());
				roomMap3.put(room.getRoomAddress().getString(), room);
				return;
			}
			if(roomMap4.values().size() == 1) {
				//System.out.println("handle create root room2 Map4 put " + cluster.getAddress().getString());
				roomMap4.put(room.getRoomAddress().getString(), room);
				return;
			}
			if(roomMap5.values().size() == 1) {
				//System.out.println("handle create root room2 Map5 put " + cluster.getAddress().getString());
				roomMap5.put(room.getRoomAddress().getString(), room);
				return;
			}
			if(roomMap6.values().size() == 1) {
				//System.out.println("handle create root room2 Map6 put " + cluster.getAddress().getString());
				roomMap6.put(room.getRoomAddress().getString(), room);
				return;
			}
			if(roomMap7.values().size() == 1) {
				//System.out.println("handle create root room2 Map7 put " + cluster.getAddress().getString());
				roomMap7.put(room.getRoomAddress().getString(), room);
				return;
			}
			if(roomMap8.values().size() == 1) {
				//System.out.println("handle create root room2 Map8 put " + cluster.getAddress().getString());
				roomMap8.put(room.getRoomAddress().getString(), room);
				return;
			}
			if(roomMap9.values().size() == 1) {
				//System.out.println("handle create root room2 Map9 put " + cluster.getAddress().getString());
				roomMap9.put(room.getRoomAddress().getString(), room);
				return;
			}
		
	}
//################################################################################
	private void createRootGameRoom(ChatApiClient cluster, ChatAvatar systemAvatar) {
		String clusterFullAddress = cluster.getAddress().getString();
		String[] splitAddress = clusterFullAddress.split("\\+");
		ChatRoom room = new ChatRoom();
		room.setCreateTime((int) (System.currentTimeMillis() / 1000));
		room.setRoomId(getNewRoomId());
		room.setCreatorId(systemAvatar.getAvatarId());
		room.setCreatorAddress(systemAvatar.getAddress());
		room.setCreatorName(systemAvatar.getName());
		room.setRoomAddress(new ChatUnicodeString(splitAddress[0] + "+" + splitAddress[1]));
		room.setRoomAttributes(0);
		room.setRoomName(new ChatUnicodeString(splitAddress[1]));
		room.setRoomPassword(new ChatUnicodeString());
		room.setRoomTopic(new ChatUnicodeString());
		room.setMaxRoomSize(0);
		room.addAvatar(systemAvatar);
		room.addAdmin(systemAvatar);
		System.out.println("New cluster " + cluster.getAddress().getString());
		//System.out.println("got handle create root room  " + room.getRoomAddress().getString()); 

		if(roomMap.isEmpty()) {
			//System.out.println("handle create root room Map put room " + room.getRoomAddress().getString());
			roomMap.put(room.getRoomAddress().getString(), room);
			createRootClusterRoom(cluster, systemAvatar);
			return;
		}
		if(roomMap1.isEmpty()) {
			//System.out.println("handle create root room Map1 put room " + room.getRoomAddress().getString());
			roomMap1.put(room.getRoomAddress().getString(), room);
			createRootClusterRoom(cluster, systemAvatar);
			return;
		}
		if(roomMap2.isEmpty()) {
			//System.out.println("handle create root room Map2 put room " + room.getRoomAddress().getString());
			roomMap2.put(room.getRoomAddress().getString(), room);
			createRootClusterRoom(cluster, systemAvatar);
			return;
		}
		if(roomMap3.isEmpty()) {
			//System.out.println("handle create root room Map3 put room " + room.getRoomAddress().getString());
			roomMap3.put(room.getRoomAddress().getString(), room);
			createRootClusterRoom(cluster, systemAvatar);
			return;
		}
		if(roomMap4.isEmpty()) {
			//System.out.println("handle create root room Map4 put room " + room.getRoomAddress().getString());
			roomMap4.put(room.getRoomAddress().getString(), room);
			createRootClusterRoom(cluster, systemAvatar);
			return;
		}
		if(roomMap5.isEmpty()) {
			//System.out.println("handle create root room Map5 put room " + room.getRoomAddress().getString());
			roomMap5.put(room.getRoomAddress().getString(), room);
			createRootClusterRoom(cluster, systemAvatar);
			return;
		}
		if(roomMap6.isEmpty()) {
			//System.out.println("handle create root room Map6 put room " + room.getRoomAddress().getString());
			roomMap6.put(room.getRoomAddress().getString(), room);
			createRootClusterRoom(cluster, systemAvatar);
			return;
		}
		if(roomMap7.isEmpty()) {
			//System.out.println("handle create root room Map7 put room " + room.getRoomAddress().getString());
			roomMap7.put(room.getRoomAddress().getString(), room);
			createRootClusterRoom(cluster, systemAvatar);
			return;
		}
		if(roomMap8.isEmpty()) {
			//System.out.println("handle create root room Map8 put room " + room.getRoomAddress().getString());
			roomMap8.put(room.getRoomAddress().getString(), room);
			createRootClusterRoom(cluster, systemAvatar);
			return;
		}
		if(roomMap9.isEmpty()) {
			//System.out.println("handle create root room Map9 put room " + room.getRoomAddress().getString());
			roomMap9.put(room.getRoomAddress().getString(), room);
			createRootClusterRoom(cluster, systemAvatar);
			return;
		}
		
	}
//################################################################################
	public void handleAddModerator(ChatApiClient cluster, RAddModerator req) {
		ResAddModerator res = new ResAddModerator();
		res.setTrack(req.getTrack());
		ChatAvatar avatar = getAvatarById(req.getSrcAvatarId());
		if(avatar == null) {
			res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		ChatAvatar destAvatar = onlineAvatars.get(req.getDestAvatarAddress().getString().toUpperCase() + "+" + req.getDestAvatarName().getString());
		if(destAvatar == null) {
			res.setResult(ResponseResult.CHATRESULT_DESTAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		ChatRoom room = roomMap.get(req.getDestRoomAddress().getString());
		if(room != null) {
			//System.out.println("AddMod found in map " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap1.get(req.getDestRoomAddress().getString());
			//System.out.println("AddMod found in map1 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap2.get(req.getDestRoomAddress().getString());
			//System.out.println("AddMod found in map2 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap3.get(req.getDestRoomAddress().getString());
			//System.out.println("AddMod found in map3 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap4.get(req.getDestRoomAddress().getString());
			//System.out.println("AddMod found in map4 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap5.get(req.getDestRoomAddress().getString());
			//System.out.println("AddMod found in map5 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap6.get(req.getDestRoomAddress().getString());
			//System.out.println("AddMod found in map6 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap7.get(req.getDestRoomAddress().getString());
			//System.out.println("AddMod found in map7 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap8.get(req.getDestRoomAddress().getString());
			//System.out.println("AddMod found in map8 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap9.get(req.getDestRoomAddress().getString());
			//System.out.println("AddMod found in map9 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			res.setResult(ResponseResult.CHATRESULT_ADDRESSDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		res.setDestRoomId(room.getRoomId());
		if(!room.isModerated()) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_MODERATEDROOM);
			cluster.send(res.serialize());
			return;
		}
		if(!room.isInRoom(avatar)) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_NOTINROOM);
			cluster.send(res.serialize());
			return;
		}
		if(!room.isInRoom(destAvatar)) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_DESTNOTINROOM);
			cluster.send(res.serialize());
			return;
		}
		//if((!room.isAdmin(avatar) || !room.isOwner(avatar)) && (!avatar.isGm() || !avatar.isSuperGm())) {
		//	res.setResult(ResponseResult.CHATRESULT_ROOM_NOPRIVILEGES);
		//	cluster.send(res.serialize());
		//	return;
		//}
		if(room.isModerator(destAvatar)) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_DUPLICATEMODERATOR);
			cluster.send(res.serialize());
			return;
		}
		room.addModerator(destAvatar);
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(res.serialize());		
	}
//################################################################################
	public void handleRemoveModerator(ChatApiClient cluster, RRemoveModerator req) {
		ResRemoveModerator res = new ResRemoveModerator();
		res.setTrack(req.getTrack());
		ChatAvatar avatar = getAvatarById(req.getSrcAvatarId());
		if(avatar == null) {
			res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		ChatAvatar destAvatar = onlineAvatars.get(req.getDestAvatarAddress().getString().toUpperCase() + "+" + req.getDestAvatarName().getString());
		if(destAvatar == null) {
			res.setResult(ResponseResult.CHATRESULT_DESTAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		ChatRoom room = roomMap.get(req.getDestRoomAddress().getString());
		if(room != null) {
			//System.out.println("removeMod found in map " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap1.get(req.getDestRoomAddress().getString());
			//System.out.println("removeMod found in map1 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap2.get(req.getDestRoomAddress().getString());
			//System.out.println("removeMod found in map2 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap3.get(req.getDestRoomAddress().getString());
			//System.out.println("removeMod found in map3 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap4.get(req.getDestRoomAddress().getString());
			//System.out.println("removeMod found in map4 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap5.get(req.getDestRoomAddress().getString());
			//System.out.println("removeMod found in map5 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap6.get(req.getDestRoomAddress().getString());
			//System.out.println("removeMod found in map6 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap7.get(req.getDestRoomAddress().getString());
			//System.out.println("removeMod found in map7 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap8.get(req.getDestRoomAddress().getString());
			//System.out.println("removeMod found in map8 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap9.get(req.getDestRoomAddress().getString());
			//System.out.println("removeMod found in map9 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			res.setResult(ResponseResult.CHATRESULT_ADDRESSDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		res.setDestRoomId(room.getRoomId());
		if(!room.isModerated()) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_MODERATEDROOM);
			cluster.send(res.serialize());
			return;
		}
		if(!room.isInRoom(avatar)) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_NOTINROOM);
			cluster.send(res.serialize());
			return;
		}
		if(!room.isInRoom(destAvatar)) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_DESTNOTINROOM);
			cluster.send(res.serialize());
			return;
		}
		//if((!room.isAdmin(avatar) || !room.isOwner(avatar) || !room.isModerator(avatar)) && (!avatar.isGm() || !avatar.isSuperGm())) {
		//	res.setResult(ResponseResult.CHATRESULT_ROOM_NOPRIVILEGES);
		//	cluster.send(res.serialize());
		//	return;
		//}
		
		if (room.isAdmin(avatar)){
		} else if (room.isOwner(avatar)){
		} else if (room.isModerator(avatar)){
		} else if (avatar.isGm()){
		} else if (avatar.isSuperGm()){
		} else {
			res.setResult(ResponseResult.CHATRESULT_ROOM_NOPRIVILEGES);
			cluster.send(res.serialize());
			return;
		}
		
		if(!room.isModerator(destAvatar)) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_DESTAVATARISMODERATOR);
			cluster.send(res.serialize());
			return;
		}
		room.removeModerator(destAvatar);
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(res.serialize());		
	}
//################################################################################
	public void handleAddBan(ChatApiClient cluster, RAddBan req) {
		ResAddBan res = new ResAddBan();
		res.setTrack(req.getTrack());
		ChatAvatar avatar = getAvatarById(req.getSrcAvatarId());
		
		//String fullAddress = request.getAddress().getString().toUpperCase() + "+" + request.getName().getString();
		if(avatar == null) {
		    res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		String destAvataraa =  (req.getDestAvatarAddress().getString().toUpperCase() + "+" + req.getDestAvatarName().getString());
		//ChatAvatar destAvatar = onlineAvatars.get(req.getDestAvatarName().getString());
		ChatAvatar destAvatar = onlineAvatars.get(req.getDestAvatarAddress().getString().toUpperCase() + "+" + req.getDestAvatarName().getString());
		String fullAddress = req.getDestAvatarAddress().getString().toUpperCase() + "+" + req.getDestAvatarName().getString();
		//ChatAvatar destAvatar = onlineAvatars.get(fullAddress);
		//System.out.println("show onlinelist " + onlineAvatars.toString());
		//System.out.println("got ban from " + avatar + " to " + destAvataraa);
		if(destAvatar == null) {
			destAvatar = getAvatarFromDatabase(fullAddress);
			if(destAvatar == null) {
				res.setResult(ResponseResult.CHATRESULT_DESTAVATARDOESNTEXIST);
				//System.out.println("destAva does not exist");
				cluster.send(res.serialize());
				return;					
			}
		}
		ChatRoom room = roomMap.get(req.getDestRoomAddress().getString());
		if(room != null) {
			//System.out.println("AddBan found in map " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap1.get(req.getDestRoomAddress().getString());
			//System.out.println("AddBan found in map1 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap2.get(req.getDestRoomAddress().getString());
			//System.out.println("AddBan found in map2 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap3.get(req.getDestRoomAddress().getString());
			//System.out.println("AddBan found in map3 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap4.get(req.getDestRoomAddress().getString());
			//System.out.println("AddBan found in map4 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap5.get(req.getDestRoomAddress().getString());
			//System.out.println("AddBan found in map5 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap6.get(req.getDestRoomAddress().getString());
			//System.out.println("AddBan found in map6 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap7.get(req.getDestRoomAddress().getString());
			//System.out.println("AddBan found in map7 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap8.get(req.getDestRoomAddress().getString());
			//System.out.println("AddBan found in map8 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap9.get(req.getDestRoomAddress().getString());
			//System.out.println("AddBan found in map9 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			res.setResult(ResponseResult.CHATRESULT_ADDRESSDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		res.setDestRoomId(room.getRoomId());
		if(!room.isInRoom(avatar)) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_NOTINROOM);
			//System.out.println("srcAva not in room");
			cluster.send(res.serialize());
			return;
		}
		//if((!room.isAdmin(avatar)) || (!room.isOwner(avatar)) || (!room.isModerator(avatar)) || (!avatar.isGm()) || (!avatar.isSuperGm())) {
		//	res.setResult(ResponseResult.CHATRESULT_ROOM_NOPRIVILEGES);
		//	System.out.println("no src right");
		//	cluster.send(res.serialize());
		//	return;
		//}

		if (room.isAdmin(avatar)){
		} else if (room.isOwner(avatar)){
		} else if (room.isModerator(avatar)){
		} else if (avatar.isGm()){
		} else if (avatar.isSuperGm()){
		} else {
			res.setResult(ResponseResult.CHATRESULT_ROOM_NOPRIVILEGES);
			cluster.send(res.serialize());
			return;
		}
		
		if(room.isBanned(destAvatar)) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_DUPLICATEBAN);
			cluster.send(res.serialize());
			return;
		}
		if(destAvatar.isGm() || destAvatar.isSuperGm() || room.isOwner(destAvatar) && (!avatar.isGm() || !avatar.isSuperGm())) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_NOPRIVILEGES);
			cluster.send(res.serialize());
			return;			
		}
		room.addBan(destAvatar);
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		room.removeAvatar(destAvatar);
		room.RemoveInvite(destAvatar);
		cluster.send(res.serialize());		
	}
//################################################################################
	public void handleRemoveBan(ChatApiClient cluster, RRemoveBan req) {
		ResRemoveBan res = new ResRemoveBan();
		res.setTrack(req.getTrack());
		ChatAvatar avatar = getAvatarById(req.getSrcAvatarId());
		if(avatar == null) {
			res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		ChatAvatar destAvatar = onlineAvatars.get(req.getDestAvatarAddress().getString().toUpperCase() + "+" + req.getDestAvatarName().getString());
		if(destAvatar == null) {
			res.setResult(ResponseResult.CHATRESULT_DESTAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		ChatRoom room = roomMap.get(req.getDestRoomAddress().getString());
		if(room != null) {
			//System.out.println("RemoveBan found in map " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap1.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveBan found in map1 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap2.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveBan found in map2 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap3.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveBan found in map3 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap4.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveBan found in map4 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap5.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveBan found in map5 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap6.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveBan found in map6 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap7.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveBan found in map7 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap8.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveBan found in map8 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap9.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveBan found in map9 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			res.setResult(ResponseResult.CHATRESULT_ADDRESSDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		res.setDestRoomId(room.getRoomId());
		if(!room.isInRoom(avatar)) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_NOTINROOM);
			//System.out.println("srcAva not in room");
			cluster.send(res.serialize());
			return;
		}
		//if((!room.isAdmin(avatar)) || (!room.isOwner(avatar)) || (!room.isModerator(avatar)) || (!avatar.isGm()) || (!avatar.isSuperGm())) {
		//	res.setResult(ResponseResult.CHATRESULT_ROOM_NOPRIVILEGES);
		//	System.out.println("src no right");
		//	cluster.send(res.serialize());
		//	return;
		//}
		
		if (room.isAdmin(avatar)){
		} else if (room.isOwner(avatar)){
		} else if (room.isModerator(avatar)){
		} else if (avatar.isGm()){
		} else if (avatar.isSuperGm()){
		} else {
			res.setResult(ResponseResult.CHATRESULT_ROOM_NOPRIVILEGES);
			cluster.send(res.serialize());
			return;
		}
		
		if(!room.isBanned(destAvatar)) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_DESTAVATARNOTBANNED);
			cluster.send(res.serialize());
			return;
		}
		res.setDestRoomId(room.getRoomId());
		room.removeBan(destAvatar);
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(res.serialize());		
	}
//################################################################################
	public void handleRemoveInvite(ChatApiClient cluster, RRemoveInvite req) {
		ResRemoveInvite res = new ResRemoveInvite();
		res.setTrack(req.getTrack());
		ChatAvatar avatar = getAvatarById(req.getSrcAvatarId());
		if(avatar == null) {
			res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		ChatAvatar destAvatar = onlineAvatars.get(req.getDestAvatarAddress().getString().toUpperCase() + "+" + req.getDestAvatarName().getString());
		if(destAvatar == null) {
			res.setResult(ResponseResult.CHATRESULT_DESTAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		ChatRoom room = roomMap.get(req.getDestRoomAddress().getString());
		if(room != null) {
			//System.out.println("RemoveInvite found in map " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap1.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveInvite found in map1 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap2.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveInvite found in map2 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap3.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveInvite found in map3 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap4.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveInvite found in map4 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap5.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveInvite found in map5 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap6.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveInvite found in map6 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap7.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveInvite found in map7 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap8.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveInvite found in map8 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap9.get(req.getDestRoomAddress().getString());
			//System.out.println("RemoveInvite found in map9 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			res.setResult(ResponseResult.CHATRESULT_ADDRESSDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		res.setDestRoomId(room.getRoomId());
		if(!room.isInRoom(avatar)) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_NOTINROOM);
			cluster.send(res.serialize());
			return;
		}
		//if((!room.isAdmin(avatar)) || (!room.isOwner(avatar)) || (!room.isModerator(avatar)) || (!avatar.isGm()) || (!avatar.isSuperGm())) {
		//	res.setResult(ResponseResult.CHATRESULT_ROOM_NOPRIVILEGES);
		//	cluster.send(res.serialize());
		//	return;
		//}
		
		if (room.isAdmin(avatar)){
		} else if (room.isOwner(avatar)){
		} else if (room.isModerator(avatar)){
		} else if (avatar.isGm()){
		} else if (avatar.isSuperGm()){
		} else {
			res.setResult(ResponseResult.CHATRESULT_ROOM_NOPRIVILEGES);
			cluster.send(res.serialize());
			return;
		}
		
		//if(!room.isBanned(destAvatar)) {
		//	res.setResult(ResponseResult.CHATRESULT_ROOM_DESTAVATARNOTBANNED);
		//	cluster.send(res.serialize());
		//	return;
		//}
		//room.addBan(destAvatar);
		room.RemoveInvite(destAvatar);
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(res.serialize());		
	}
//################################################################################
	public void handleKickAvatar(ChatApiClient cluster, RKickAvatar req) {
		ResKickAvatar res = new ResKickAvatar();
		res.setTrack(req.getTrack());
		ChatAvatar avatar = getAvatarById(req.getSrcAvatarId());
		if(avatar == null) {
			res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		ChatAvatar destAvatar = onlineAvatars.get(req.getDestAvatarAddress().getString().toUpperCase() + "+" + req.getDestAvatarName().getString());
		if(destAvatar == null) {
			res.setResult(ResponseResult.CHATRESULT_DESTAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		ChatRoom room = roomMap.get(req.getDestRoomAddress().getString());
		if(room != null) {
			//System.out.println("KickAva found in map " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap1.get(req.getDestRoomAddress().getString());
			//System.out.println("KickAva found in map1 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap2.get(req.getDestRoomAddress().getString());
			//System.out.println("KickAva found in map2 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap3.get(req.getDestRoomAddress().getString());
			//System.out.println("KickAva found in map3 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap4.get(req.getDestRoomAddress().getString());
			//System.out.println("KickAva found in map4 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap5.get(req.getDestRoomAddress().getString());
			//System.out.println("KickAva found in map5 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap6.get(req.getDestRoomAddress().getString());
			//System.out.println("KickAva found in map6 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap7.get(req.getDestRoomAddress().getString());
			//System.out.println("KickAva found in map7 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap8.get(req.getDestRoomAddress().getString());
			//System.out.println("KickAva found in map8 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap9.get(req.getDestRoomAddress().getString());
			//System.out.println("KickAva found in map9 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			res.setResult(ResponseResult.CHATRESULT_ADDRESSDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		res.setDestRoomId(room.getRoomId());
		if(!room.isModerated()) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_MODERATEDROOM);
			cluster.send(res.serialize());
			return;
		}
		if(!room.isInRoom(avatar)) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_NOTINROOM);
			cluster.send(res.serialize());
			return;
		}
		if(!room.isInRoom(destAvatar)) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_DESTNOTINROOM);
			cluster.send(res.serialize());
			return;
		}
		//if((!room.isAdmin(avatar)) || (!room.isOwner(avatar)) || (!room.isModerator(avatar)) || (!avatar.isGm()) || (!avatar.isSuperGm())) {
		//	res.setResult(ResponseResult.CHATRESULT_ROOM_NOPRIVILEGES);
		//	System.out.println("kick src no rights");
		//	cluster.send(res.serialize());
		//	return;
		//}
		
		if (room.isAdmin(avatar)){
		} else if (room.isOwner(avatar)){
		} else if (room.isModerator(avatar)){
		} else if (avatar.isGm()){
		} else if (avatar.isSuperGm()){
		} else {
			res.setResult(ResponseResult.CHATRESULT_ROOM_NOPRIVILEGES);
			cluster.send(res.serialize());
			return;
		}
		
		if(destAvatar.isGm() || destAvatar.isSuperGm() || room.isOwner(destAvatar) && (!avatar.isGm() || !avatar.isSuperGm())) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_NOPRIVILEGES);
			cluster.send(res.serialize());
			return;
		}
		if(room.isModerator(destAvatar)) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_DESTAVATARISMODERATOR);
			cluster.send(res.serialize());
			return;
		}
		//room.removeAvatar(destAvatar);
		room.kickAvatar(destAvatar);
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(res.serialize());		
	}
//################################################################################ 
	public void handleAddInvite(ChatApiClient cluster, RAddInvite req) {
		ResAddInvite res = new ResAddInvite();
		res.setTrack(req.getTrack());
		ChatAvatar avatar = getAvatarById(req.getSrcAvatarId());
		if(avatar == null) {
			res.setResult(ResponseResult.CHATRESULT_SRCAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		ChatAvatar destAvatar = onlineAvatars.get(req.getDestAvatarAddress().getString().toUpperCase() + "+" + req.getDestAvatarName().getString().toLowerCase());
		if(destAvatar == null) {
			res.setResult(ResponseResult.CHATRESULT_DESTAVATARDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		ChatRoom room = roomMap.get(req.getDestRoomAddress().getString());
		if(room != null) {
			//System.out.println("AddInvite found in map " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap1.get(req.getDestRoomAddress().getString());
			//System.out.println("AddInvite found in map1 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap2.get(req.getDestRoomAddress().getString());
			//System.out.println("AddInvite found in map2 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap3.get(req.getDestRoomAddress().getString());
			//System.out.println("AddInvite found in map3 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap4.get(req.getDestRoomAddress().getString());
			//System.out.println("AddInvite found in map4 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap5.get(req.getDestRoomAddress().getString());
			//System.out.println("AddInvite found in map5 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap6.get(req.getDestRoomAddress().getString());
			//System.out.println("AddInvite found in map6 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap7.get(req.getDestRoomAddress().getString());
			//System.out.println("AddInvite found in map7 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap8.get(req.getDestRoomAddress().getString());
			//System.out.println("AddInvite found in map8 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			room = roomMap9.get(req.getDestRoomAddress().getString());
			//System.out.println("AddInvite found in map9 " + req.getDestRoomAddress().getString());
		}
		if(room == null) {
			res.setResult(ResponseResult.CHATRESULT_ADDRESSDOESNTEXIST);
			cluster.send(res.serialize());
			return;
		}
		res.setDestRoomId(room.getRoomId());
		if(!room.isInRoom(avatar)) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_NOTINROOM);
			cluster.send(res.serialize());
			return;
		}
		if (room.isAdmin(avatar)){
		} else if (room.isOwner(avatar)){
		} else if (room.isModerator(avatar)){
		} else if (avatar.isGm()){
		} else if (avatar.isSuperGm()){
		} else {
			res.setResult(ResponseResult.CHATRESULT_ROOM_NOPRIVILEGES);
			cluster.send(res.serialize());
			return;
		}
		if(room.isInvited(destAvatar)) {
			res.setResult(ResponseResult.CHATRESULT_ROOM_DUPLICATEINVITE);
			cluster.send(res.serialize());
			return;
		}
		res.setDestRoomId(room.getRoomId());
		room.AddInvite(destAvatar);
		//room.removeBan(destAvatar);
		res.setResult(ResponseResult.CHATRESULT_SUCCESS);
		cluster.send(res.serialize());		
	}

}
