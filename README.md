# SWG-Station-Chat<br>
external Station-Chat for SWG, Everquest and other SOE-Games<br>
<br>
This i a heavy modified Version of:<br>
https://github.com/Light2/StationChat/commits/master<br>
<br>
Credits to Light2<br>
<br>
This Station-Chat works for up to 10 SWG-Game-Servers !!<br>
Failover works fully, that means you can shutdown/switch-on the station-chat and SWG reconnect without<br>
problem and dont need to restart SWG for it.<br>
You can also shutdown SWG-Game-Server and start again without restart the station-chat.<br>
You can send cross-Server instant-messages, mails and addFriend.<br>
For a BAN you have to use a fullName, that means "SWG-BASTEL.testuser" or "SWG.SWG-BASTEL.testuser" for security-messures.<br>
<br>
Use Eclipse-Software to compile it !<br>
Export it as runnable JAR<br>
run it "java -jar station-chat.jar"<br>
<br>
####################### How to run the Chat ############################<br>
<br>
######## at the moment it works only on SWG-Linux Versions ####<br>
Have to find out how to change it on Windows based Star Wars Galaxies.<br>

Station-Chat from Light has one problem,<br>
It run on TCP, but SWG run on UDP.<br>

That problem is easy to solve, it is easier to change swg to talk TCP to the external chat as changing the chat to talk udp,<br> because the udp is encrypted. We do not need that encryption as long as we run the swg-server and the station-chat in<br> the same enviroment. That means as we do not run the chat for different games or on different swg-server as SOE did.<br>
<br>
Now how to change SWG to talk TCP on chat.<br>
<br>
copy the folder "TcpLibrary" from "src/external/3rd/library/soePlatform/CSAssist/utils/" to<br>
"src/external/3rd/library/soePlatform/ChatAPI/utils"<br>
_________________________________________<br>
Than change in "src/external/3rd/library/soePlatform/ChatAPI/utils/GenericAPI/GenericConnection.cpp"<br>
 from: using namespace UdpLibrary;<br>
 <br>
 to: using namespace TcpLibrary;<br>
_________________________________________<br>
Change "src/external/3rd/library/soePlatform/ChatAPI/utils/GenericAPI/GenericConnection.h"<br>
from: class GenericConnection : public TcpConnectionHandler<br>
---<br>
to: class GenericConnection : public TcpLibrary::TcpConnectionHandler<br>
_____<br>
from: virtual void OnRoutePacket(TcpConnection *con, const unsigned char *data, int dataLen);<br>
from: virtual void OnTerminated(TcpConnection *con);<br>
---<br>
to: virtual void OnRoutePacket(TcpLibrary::TcpConnection *con, const unsigned char *data, int dataLen);<br>
to: virtual void OnTerminated(TcpLibrary::TcpConnection *con);<br>
_____<br>
from: TcpManager *m_manager;<br>
from: TcpConnection *m_con;<br>
---<br>
to: TcpLibrary::TcpManager *m_manager;<br>
to: TcpLibrary::TcpConnection *m_con;<br>
___________________________________________<br>
<br>
the most important change:<br>
"src/external/3rd/library/soePlatform/ChatAPI/projects/ChatMono/CMakeLists.txt"<br>
<br>
add: ../../utils/TcpLibrary/Clock.cpp<br>
add: ../../utils/TcpLibrary/Clock.h<br>
add: ../../utils/TcpLibrary/IPAddress.cpp<br>
add: ../../utils/TcpLibrary/IPAddress.h<br>
add: ../../utils/TcpLibrary/TcpBlockAllocator.cpp<br>
add: ../../utils/TcpLibrary/TcpBlockAllocator.h<br>
add: ../../utils/TcpLibrary/TcpConnection.cpp<br>
add: ../../utils/TcpLibrary/TcpConnection.h<br>
add: ../../utils/TcpLibrary/TcpHandlers.h<br>
add: ../../utils/TcpLibrary/TcpManager.cpp<br>
add: ../../utils/TcpLibrary/TcpManager.h<br>
<br>
from: add_definitions(-DEXTERNAL_DISTRO -DNAMESPACE=ChatSystem -D_REENTRANT)<br>
---<br>
to: add_definitions(-DEXTERNAL_DISTRO -DNAMESPACE=ChatSystem -D_REENTRANT -DUSE_TCP_LIBRARY)<br>
<br>
#######################################################################################<br>
<br>
Now compile SWG<br>
#################################<br>
than set in your config in SWG:<br>
[ChatServer]<br>
registrarPort=5000<br>
gatewayServerPort=5001<br>
registrarHost=localhost<br>
gatewayServerIP=localhost<br>
###########################<br>
No problem to change these ports or the "localhost" to any IP if you run the Station-chat on another box.<br>
Be sure that you use that IP/port in the Station-chat-config, the IP and Port in station-config is used (registrar)<br>
to tell swg where to find the chat.<br>
<br>
<br>
######## run Chat, run SWG , have fun !!! ########### <br>
