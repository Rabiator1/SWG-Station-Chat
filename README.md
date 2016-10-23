# SWG-Station-Chat
external Station-Chat for SWG, Everquest and other SOE-Games

This i a heavy modified Version of:
https://github.com/Light2/StationChat/commits/master

Credits to Light2

This Station-Chat works for up to 10 SWG-Game-Servers !!
Failover works fully, that means you can shutdown/switch-on the station-chat and SWG reconnect without
problem and dont need to restart SWG for it.
You can also shutdown SWG-Game-Server and start again without restart the station-chat.
You can send cross-Server instant-messages, mails and addFriend.
For a BAN you have to use a fullName, that means "SWG-BASTEL.testuser" or "SWG.SWG-BASTEL.testuser" for security-messures.

Use Eclipse-Software to compile it !
Export it as runnable JAR
run it "java -jar station-chat.jar"

####################### How to run the Chat ############################

######## at the moment it works only on SWG-Linux Versions ####
Have to find out how to change it on Windows based Star Wars Galaxies.

Station-Chat from Light has one problem,
It run on TCP, but SWG run on UDP.

That problem is easy to solve, it is easier to change swg to talk TCP to the external chat as changing the chat to talk udp, because the udp is encrypted. We do not need that encryption as long as we run the swg-server and the station-chat in the same enviroment. That means as we do not run the chat for different games or on different swg-server as SOE did.

Now how to change SWG to talk TCP on chat.

copy the folder "TcpLibrary" from "src/external/3rd/library/soePlatform/CSAssist/utils/" to
"src/external/3rd/library/soePlatform/ChatAPI/utils"
_________________________________________
Than change in "src/external/3rd/library/soePlatform/ChatAPI/utils/GenericAPI/GenericConnection.cpp"
from: using namespace UdpLibrary;
---
to: using namespace TcpLibrary;
_________________________________________
Change "src/external/3rd/library/soePlatform/ChatAPI/utils/GenericAPI/GenericConnection.h"
from: class GenericConnection : public TcpConnectionHandler
---
to: class GenericConnection : public TcpLibrary::TcpConnectionHandler
_____
from: virtual void OnRoutePacket(TcpConnection *con, const unsigned char *data, int dataLen);
from: virtual void OnTerminated(TcpConnection *con);
---
to: virtual void OnRoutePacket(TcpLibrary::TcpConnection *con, const unsigned char *data, int dataLen);
to: virtual void OnTerminated(TcpLibrary::TcpConnection *con);
_____
from: TcpManager *m_manager;
from: TcpConnection *m_con;
---
to: TcpLibrary::TcpManager *m_manager;
to: TcpLibrary::TcpConnection *m_con;
___________________________________________

the most important change:
"src/external/3rd/library/soePlatform/ChatAPI/projects/ChatMono/CMakeLists.txt"

add: ../../utils/TcpLibrary/Clock.cpp
add: ../../utils/TcpLibrary/Clock.h
add: ../../utils/TcpLibrary/IPAddress.cpp
add: ../../utils/TcpLibrary/IPAddress.h
add: ../../utils/TcpLibrary/TcpBlockAllocator.cpp
add: ../../utils/TcpLibrary/TcpBlockAllocator.h
add: ../../utils/TcpLibrary/TcpConnection.cpp
add: ../../utils/TcpLibrary/TcpConnection.h
add: ../../utils/TcpLibrary/TcpHandlers.h
add: ../../utils/TcpLibrary/TcpManager.cpp
add: ../../utils/TcpLibrary/TcpManager.h

from: add_definitions(-DEXTERNAL_DISTRO -DNAMESPACE=ChatSystem -D_REENTRANT)
---
to: add_definitions(-DEXTERNAL_DISTRO -DNAMESPACE=ChatSystem -D_REENTRANT -DUSE_TCP_LIBRARY)

#######################################################################################

Now compile SWG
#################################
than set in your config in SWG:
[ChatServer]
registrarPort=5000
gatewayServerPort=5001
registrarHost=localhost
gatewayServerIP=localhost
###########################
No problem to change these ports or the "localhost" to any IP if you run the Station-chat on another box.
Be sure that you use that IP/port in the Station-chat-config, the IP and Port in station-config is used (registrar)
to tell swg where to find the chat.


######## run Chat, run SWG , have fun !!! ########### 
