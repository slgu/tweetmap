package servlet;

import com.google.gson.Gson;
import db.Rds;
import db.Tweet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedList;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.util.List;

/**
 * Created by slgu1 on 10/9/15.
 */
public class RdsServlet extends HttpServlet{

    //shared connection between all threads
    private static Rds rds;
    static {
        try {
            rds = new Rds();
            rds.init();
        }
        catch (Exception e) {
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LinkedList <Tweet> tweetList = null;
        Gson gson = new Gson();
        try {
            tweetList = rds.getAll();
        }
        catch (Exception e) {
        }
        resp.setHeader("Content-type", "application/json");
        LinkedList <HashMap <String, String> > list = new LinkedList<HashMap<String, String>>();
        for (Tweet item: tweetList) {
            list.add(item.toMap());
        }
        resp.getWriter().print(gson.toJson(list));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }
}