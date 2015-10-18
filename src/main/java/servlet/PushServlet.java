package servlet;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.server.ExportException;
import java.util.*;

import com.google.gson.Gson;
import db.Tweet;
import server.TweetStream;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/push")
public class PushServlet extends HttpServlet {

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

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
        clients.remove(session);
    }
}
