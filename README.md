# Distributed-Systems-Design
# Description
Distributed Player Status System (DPSS) is aims to connect a group of servers located in three different geolocations. This distributed system will be used to manage player status, create account, transfer account between servers, suspend account across multiple game servers. The users of the system are players and administrator, and both are identified by a unique Username. 

The administrator could perform:
1. Update Player Status
2. Suspend Existing Player Account

Player only limited to following kinds of work:
1. Create New Account
2. Transfer Account from one server to another.

There are three servers are used and they are in three different geolocations.
1. North America (NA): IP-addresses starting with 132 such as 132.xxx.xxx.xxx
2. Europe (EU): IP-addresses starting with 93 such as 93.xxx.xxx.xxx
3. Asia (AS): IP-addresses starting with 182 such as 182.xxx.xxx.xxx

There will be three separate groups of the same servers and each group replica contains a Replica managers (RM) and corresponding Replica servers.  Apart from that there will be a Front End (FE) server and the implementation of CORBA Front End has been considered as a Leader replica. The entire server system would be Replicas, FE, and RM.  In this project, Active Replication will be used to handle a single software (non-malicious Byzantine) failure.

# Protocols Used
1. IDL Compiler: To register remote objects using.
2. ORB: Using CORBA naming service register the objects remotely. Then Access the
objects by clients. Clients and servers are running on different threads
3. Java Programming Language
4. UDP/IP Socket: Inter Server Communication is done using UDP Protocol. As we try to
access any distributed information. The servers need to communicate with each other.

# Active Replication
There are five phases to perform a client request (copied from class lecture slide)
1. Request: Front End attaches a unique id and uses totally ordered reliable multicast to send request to RMs. 
2. Coordination: The multicast delivers requests to all the RMs in the same (total) order. 
3. Execution: Every RM executes the request. They are state machines and receive requests in the same order, so the effects are identical. The id is put in the response. 
4. Agreement: No agreement is required because all RMs execute the same operations in the same order, due to the properties of the totally ordered multicast. 
5. Response: FEs collect responses from RMs. FE may just use one or more responses. 
3. Architecture

# Model View
Clients only can communication channel with Front-End (FE) server.  We have implemented FE that accepts the Clients request through CORBA. Once FE receive a request, it sends the request to Sequencer then Sequencer send the messages with a sequence id to all Replica Managers (RM). This is multicasting of each request from Sequencer to all RMs and which conform that all RMs perform the same operations in the same order. Each RMs process each request according to sequence id and send it to the corresponding servers (replica server). Specific servers execute and send output to the FE.  FE receives three outputs from all replicas. And finally, send a single corrected result to the client. The total process is completed by without client’s concern. 

The communication Sequencer to RMs are done by multicast UDP/IP connection. Communication between individual RM and corresponding servers have been established by the UDP connection. Each Replica server and FE communicate with each other by reliable UDP/IP connection. Within group, if the operation requires data from other geolocation servers, then the server makes a UDP request to the target server and gets the required data using a socket communication. 

