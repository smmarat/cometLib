Сomet (Long pooling) Library
============================

## Building the library

You can use ant to build the .jar file

## Using thr library

1. Add jar to your library folder and setup it as library in your IDE
2. Make instance of CometClient class and define abstract methods of Comet.CometListener interface and URLs to send and receive channels
3. Receive async responce from onData() callback
4. Make Req object and define Rec.SyncListener interface to send request and receive sync responce
5. Use CometClient.getInstance().add() method to send requests.





