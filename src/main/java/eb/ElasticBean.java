package eb;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient;
import com.amazonaws.services.elasticbeanstalk.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.Date;

/**
 * Created by slgu1 on 10/8/15.
 */
public class ElasticBean {

    private static final String appPath = "/Users/slgu1/aws/tweetmap/target/tweetmap.war";
    public void init() throws IOException{
        credentials = new PropertiesCredentials(
                ElasticBean.class.getResourceAsStream("AwsCredentials.properties"));
        PropertyConfigurator.configure("log4j.properties");
        eb = new AWSElasticBeanstalkClient(credentials);
        s3 = new AmazonS3Client();
    }

    public void setup(String appName, String appDesc, String envName, String envDesc) throws IOException {
        this.appName = appName;
        this.appDesc = appDesc;
        this.envName = envName;
        this.envDesc = envDesc;
        //create application
        eb.createApplication(new CreateApplicationRequest().
                withApplicationName(appName).withDescription(appDesc));

        //create environment
        eb.createEnvironment(new CreateEnvironmentRequest().withEnvironmentName(envName)
                .withApplicationName(appName)
                .withSolutionStackName("32bit Amazon Linux running Tomcat 7")
                .withDescription(envDesc));
    }
    public void deploy(String appVersionDesc) throws IOException{
        //create S3 storage to upload the web files
        File f = new File(appPath);
        String fileName = f.getName();
        String bucketName = eb.createStorageLocation().getS3Bucket();
        s3.putObject(bucketName, fileName, f);

        //upload application
        String version_number = fileName + "_" + new Date().toString();
        eb.createApplicationVersion(new CreateApplicationVersionRequest().withApplicationName(appName)
                .withDescription(appVersionDesc).withVersionLabel(version_number)
                .withAutoCreateApplication(true)
                .withSourceBundle(new S3Location(bucketName, fileName)));

        //update version
        eb.updateEnvironment(new UpdateEnvironmentRequest()
                .withEnvironmentName(envName)
                .withVersionLabel(version_number));
    }

    private AWSCredentials credentials;
    private AWSElasticBeanstalkClient eb;
    private AmazonS3 s3;
    private String appName;
    private String appDesc;
    private String envName;
    private String envDesc;
    public static void main(String[] args) {
        ElasticBean proxyer = new ElasticBean();
        try {
            proxyer.init();
            proxyer.setup("tweetmap1", "slgu@tweetmap", "tomcat7", "slgu@tomcat7");
            //wait for ready
            proxyer.deploy("update test application");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}