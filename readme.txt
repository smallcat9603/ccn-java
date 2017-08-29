[What is CCN-Java Emulator]
What I am doing now is to implement an open source emulation platform for CCN experiments. It realizes a kind of application-level overlay on top of TCP or UDP communications based on the CCN paradigm. It is designed for real node-to-node experiments and their relative analysis. It can be seen a kind of CCN emulator, which is different from ndnSIM that is a popular simulator for CCN or NDN. Compared to the current CCNx project, the CCN-Java emulator has a cleaner and more extensible data structure and can be directly executed as an application program. We can simply pass variables for configuration and implement our own ideas or algorithms based on the basic CCN scheme. So.. it should be much easier to carry out CCN experiments!

[Features]
Each CCN node (instance) is able to
(1) listen on a socket
(2) process incoming packets and outgoing packets (including Interest and Data packets)
(3) configure routing information in FIB
(4) keep tracks of impending Interests in PIT
(5) cache the Data packets passing through it in CS (currently implemented by LRU replacement policy, you can modify it or extend it with new caching algorithms)

Now, the program is extended to the RMI (Remote Method Invocation) framwork. As a CCN manager, it can monitor all the behaviors of nodes in the experiment and get the statistical information from each node. I have added several interfaces currently which can be used to fetch the statistical information. Currently, it supports to
(1) get remote machine ID
(2) get cache of remote machine
(3) get FIB of remote machine
(4) get PIT of remote machine
(5) get No. of cache hits
(6) get No. of pit entries

[Requested Packages and Tools]
For running, just JRE 1.7 (or above) is required.
For developing, JDK 1.7 (or above) and Eclipse (recommended) are needed.

[How to Modify the Code]
If you need to edit the source code, you can use Eclipse. To import the project, follow 
File ¡ú Import ¡ú General ¡ú Existing project into Workspace ¡ú select the project folder!

############################################################################################################################

(2013.11.11)
The update from CCN-Java_0.3 to CCN-Java_0.4gamma includes

[1] The partial implementation of the new proposal.
It has not been finished. For the time being, it is just for testing myself. So you can ignore it currently. 

[2] role selection of CCN node
When running one CCN node, now you can select the role of the node as a CCN user, router, repository or others. Still it is being implemented, but you can use and test the existing ones. 

[3] PIT entries operation 
Several logic errors about PIT entries operation have been fixed.

[4] FIB entries longest matching
It includes the algorithm improvement about the FIB entries longest matching.

[5] Other several code improvements and minor error fixes

############################################################################################################################

(2013.11.26)  ICN-Java (ver0.4)

The update from CCN-Java_0.4gamma to ICN-Java_0.4 includes the new proposal (which has been ALMOST finished, but not tested yet) and other code bug fixes and improvements

[1] The implementation of the new proposal.
It has been almost finished. Next I am ready to test it. But you can ignore it currently. 

[2] bugs fix
Several bugs has been fixed including Map initiation.  

[3] more comments 
More comments are added to help understanding the code. 

[4] Other several code improvements and minor error fixes

############################################################################################################################

(2013.11.30)  ICN-Java (ver0.5beta)

The update from CCN-Java_0.4 to ICN-Java_0.5beta includes the code bug fixes for the new proposal and several improvements for the basic CCN scheme

[1] bugs fix for the new proposal
Several bugs have been detected while debugging. Now they have been fixed. 

[2] improvements for the basic CCN scheme code
The execution timestamps are attached on each print-out statement. Now you can read the output more clearly.

############################################################################################################################

(2013.12.12)  ICN-Java (ver0.5)

The major updates from CCN-Java_0.5beta to ICN-Java_0.5 are

[1] campatibility with linux
Now the MANIFEST.MF file is included in the code package. If you export the program as a jar file, the jar file can be directly run in the linux environment. 

[2] topology configuration£¨trial£©
Now you can generate a simple topology conveniently for the experiments as you wish.

############################################################################################################################

(2014.01.21)  ICN-Java (ver0.7beta)

The major updates from CCN-Java_0.5 to ICN-Java_0.7beta are

[1] role configuration
The role of the node can be specified at once beforehand if you like. 

[2] node running
Now you can run multiple nodes simutaneously in the ccn_rmi_client.

[3] routing calculation re-written
The routing calculation algorithm has been re-written because of the dead loop in the previous version of the calculation.

[4] ¡¸no such object in table¡¹(test)
The error message ¡¸no such object in table¡¹has vanished.

[5] Other algorithms improved

############################################################################################################################

(2014.01.21)  ICN-Java (ver0.8beta)

The major updates from ICN-Java_0.7beta to ICN-Java_0.8beta are

[1] multipath forwarding (NOT tested)
The returned network routing information by scheduler includes the calculated information based on hop counts. 