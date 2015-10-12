package servlet;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.*;

import com.google.gson.Gson;
import db.Tweet;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import server.TweetStream;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/push")
public class PushServlet {
    //all clients
    private static ConcurrentHashMap <Session, Integer> clients = new
            ConcurrentHashMap<Session, Integer>();
    private static Thread t;
    static {
        //load TweetStream and init it
        try {
            TweetStream.initService();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //worker connect tweetstream server and websocket
        t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    while (clients.isEmpty());
                    Tweet tweet;
                    try {
                        tweet = TweetStream.queue.take();
                    }
                    catch (InterruptedException e) {
                        continue;
                    }
                    String txt = new Gson().toJson(tweet.toMap());
                    System.out.println(txt);
                    synchronized (clients) {
                        Enumeration <Session> enumeration = clients.keys();
                        while (enumeration.hasMoreElements()) {
                            Session s = enumeration.nextElement();
                            try {
                                s.getBasicRemote().sendText(txt);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        };
        t.start();
    }
    @OnOpen
    public void onOpen(Session session) throws IOException {
        clients.put(session, 1);
    }

    /*
    @OnMessage
    public String echo(String message, Session session) {
        Tweet tweet = TweetStream.queue.pop();
        return new Gson().toJson(tweet.toMap());

    }
    */

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
        clients.remove(session);
    }
    public static void main(String [] args) {
        while (true) {

        }
    }
}
