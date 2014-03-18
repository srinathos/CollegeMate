package collegemate.university.sies;


import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Srinath
 * Date: 20/1/14
 * Time: 6:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class OnlineDbDownloader {
    int error_code=0;
    JSONArray JSON;
    String IMEI;
    final String url="http://speed.byethost16.com/notices";

    public OnlineDbDownloader(String IMEI)
    {
        this.IMEI=IMEI;
    }

    public void downloadData()
    {
        try
        {
            Log.d("NMe", "Downloading...");
            //List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            //nameValuePairs.add(new BasicNameValuePair("AUTH",params[1]));
            DefaultHttpClient httpClient=new DefaultHttpClient();
            HttpPost post=new HttpPost(url);
            //post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            Log.d("NMe", "All set");
            HttpResponse response=httpClient.execute(post);
            JSON=new JSONArray(EntityUtils.toString(response.getEntity()));
            Log.d("NMe","Downloaded");
            if(JSON.toString().equalsIgnoreCase("[{\"AUTH\":\"INVALID\"}]"))
                error_code=3;
        }
        catch(Exception e)
        {
            error_code=2;
        }
    }

    public JSONArray getJSON()
    {
        return JSON;
    }

    public int getErrorCode()
    {
        return error_code;
    }

}
