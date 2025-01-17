# T3 Game.

This is a multi-player tic-tac-toe game implemented in Java. Key features include:
- Online matching mechanism for multiple players and multiple rooms.
- Read-time ranking board.
- Chatting with your opponent.
- Gracefully handle disconnect and reconnect of players. 
- Reactive UI layout.
- Use only javaRMI and Sockets for networking.

Please refer to [Software Manual](./Software_Manual_Tic-Tac-Toe.pdf) for full specification. 

Preview:
![Preview](preview.png)

Structures:
- src/main/java/T3: The UI client powered by JavaFX
- src/main/java/T3Sever: The backend server which handles multiple player connections.
- src/main/java/interfaces: The common interfaces which used both in the client and the server. 
- src/main/java/models: The common models which used both in the client and the server. 

That's all, thank you for reading it. 