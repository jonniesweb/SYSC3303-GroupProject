SYSC 3303 Group D
-----------------

UML
---

Look in the "Milestone 1 PDFs" directory for the main uml, subsystem and communication diagrams.



Instructions
------------

Implementation V1
-----------------

All the files mentioned below are in the src/testing/ directory


1. Player joins, moves about, reaches the door then leaves
   
   Run TestServerWithDoor.java, then launch TestDriver.java
   
2. Two players join, move around without touching each other and eventually reach the door and end game
   
   Run TestServerWithDoor.java, then launch TestDriver2.java
   
3. Two players move about and touch each other. One Player dies

   Run TestServerNoDoor.java, then launch TestDriver2.java


Implementation V2
-----------------

From the src/clientLogic/ directory, run ClientMain.java inbetween launching the Server and the TestDriver in any of the above tests.

Notice
------

It is recommended to launch the above commands in an IDE or kill the processes manually, since the client, test driver and server don't automatically exit.
