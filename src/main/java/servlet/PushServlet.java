package servlet;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;

import com.google.gson.Gson;
import db.Tweet;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import server.TweetStream;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/push")
public class PushServlet {
    //all clients
    private static Set <Session> clients =
            Collections.synchronizedSet(new HashSet<Session>());
    private static Thread t;
    static {
        //load TweetStream
        try {
            Class.forName("server.TweetStream");

            //worker connect tweetstream server and websocket
            t = new Thread() {
                @Override
                public void run() {
                    Tweet tweet = TweetStream.queue.pop();
                    String txt = new Gson().toJson(tweet.toMap());
                    for (Session s: clients) {
                        //broadcast
                        try {
                            s.getBasicRemote().sendText(txt);
                        }
                        catch (IOException e) {

                        }
                    }
                }
            };
            t.start();
        }
        catch (ClassNotFoundException e) {

        }
    }
    @OnOpen
    public void onOpen(Session session) throws IOException {
        clients.add(session);
    }

    @OnMessage
    public String echo(String message, Session session) {
        Tweet tweet = TweetStream.queue.pop();
        return new Gson().toJson(tweet.toMap());
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {

    }
}
