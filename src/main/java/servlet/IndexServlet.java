package servlet;

import db.Rds;
import db.Tweet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.List;

/**
 * Created by slgu1 on 10/9/15.
 */
public class IndexServlet extends HttpServlet{

    //shared connection between all threads
    private static Rds rds;
    private static Logger logger;
    static {
        PropertyConfigurator.configure("/Users/slgu1/aws/tweetmap/log4j.properties");
        logger = LogManager.getLogger(IndexServlet.class);
        try {
            rds = new Rds();
            rds.init();
        }
        catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LinkedList <Tweet> tweetList;
        try {
            rds.init();
            tweetList = rds.getAll();
        }
        catch (Exception e) {
            FileWriter writer = new FileWriter("/Users/slgu1/aws/tweetmap/log/log.out");
            writer.write(e.toString());
            writer.close();
            logger.debug(e.getMessage());
            tweetList = null;
        }
        /*load data*/
        LinkedList <String> stringList = new LinkedList<String>();
        for (Tweet t: tweetList) {
            stringList.add(t.toString());
        }
        req.setAttribute("pos", String.join("\t",stringList));
        req.getRequestDispatcher("index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }
}