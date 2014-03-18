package collegemate.university.sies;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.*;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

//NAVIGATION DRAWER
/*import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout; */



public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    final String P_KEY="_id" ;
    final String KEY_TITLE="title";
    final String KEY_DATE="date";
    final String KEY_CONTENT="content";
    final String KEY_FROM="sender";
    final String TABLE_NAME="notices";
    int refresh_flag=0;
    ListView list;
    Cursor headData;
    NoticeAdapter adapter;
    int error_code=0;

    //String debug_temp[]={"Notice Board","Profile","Performance","Attendance"};
    /*
    *   Navigation Drawer Variables
    *       Attendance
    *       Performance
    *       Profile
    *   private String[] mDrawerStrings;
    *   private DrawerLayout mDrawerLayout;
    *   private ListView mDrawerList;
    */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_list);
        //ListView drawerList=(ListView)findViewById(R.id.left_drawer);
        populateListView();
    }

    @Override
    protected void onResume() {

        Log.d("NMe","Recalling cursor");
        headData=new LocalDBHandler(getApplicationContext()).getNotice();
        Log.d("NMe","Data set changed");
        adapter.changeCursor(headData);
        adapter.notifyDataSetChanged();
    }

    private void populateListView()
    {
        //setContentView(R.layout.notice_list);
        list=(ListView)findViewById(R.id.notice_list);
        LocalDBHandler object=new LocalDBHandler(getApplicationContext());
        String[] data=new String[2];
        if(object.doesExists())
        {
            //DB EXISTS!
            Log.d("NMe","Database exists; Diving in");
            headData=object.getNotice();
            adapter = new NoticeAdapter(this,headData);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent=new Intent(getApplicationContext(),ExpandNotice.class);
                    Log.d("Nme",""+position);
                    intent.putExtra("position",position);
                    startActivity(intent);
                    //overridePendingTransition(R.layout.transition_fade_in,R.layout.transition_fade_out);
                }
            });
        }
        else
        {
            //NO DB
            Log.d("NMe","No data! pushing shit!");
            if(checkConnection()==1)
            {
                downloadJSON ob1=new downloadJSON();
                ob1.execute();
                error_code=ob1.getErrorCode();
            }
            else
            {
                Log.d("NMe","No connection!");
                error_code=1;
                displayMessage(error_code);
            }
        }
    }

    private void refresh()
    {
        if(checkConnection()==1)
        {
            new downloadJSON().execute();
            refresh_flag=1;
            Log.d("NMe","Recalling list");
        }
        else
        {
            error_code=1;
            displayMessage(error_code);
        }

    }

    private void pushJSONtoDB(JSONArray jsonArray)
    {
        try
        {
            Log.d("NMe","Inserting");
            for(int i=jsonArray.length()-1;i>=0;i--)
            {
                JSONObject object=jsonArray.getJSONObject(i);
                String dataPacket[]=new String[5];
                dataPacket[0]=object.getString("id");
                dataPacket[1]=object.getString("title");
                dataPacket[2]=object.getString("date");
                dataPacket[3]=object.getString("content");
                dataPacket[4]=object.getString("from");
                new LocalDBHandler(this).insert(dataPacket);
            }
        }
        catch(JSONException exception)
        {
            Log.d("YO","Json exception");
            exception.printStackTrace();
        }
        Log.d("NMe","Insert complete");
        populateListView();
    }

    private void pushJSONtoDB(JSONArray jsonArray,int count)
    {
        try
        {
            Log.d("NMe","Inserting");
            for(int i=count-1;i>=0;i--)
            {
                JSONObject object=jsonArray.getJSONObject(i);
                String dataPacket[]=new String[5];
                dataPacket[0]=object.getString("id");
                dataPacket[1]=object.getString("title");
                dataPacket[2]=object.getString("date");
                dataPacket[3]=object.getString("content");
                dataPacket[4]=object.getString("from");
                new LocalDBHandler(this).insert(dataPacket);
            }
        }
        catch(JSONException exception)
        {
            Log.d("NMe","Interpret Error: Received file corrupt");
            exception.printStackTrace();
        }
        Log.d("NMe", "Insert complete");
        populateListView();
    }


    private void displayMessage(int error_code)
    {
        String[] values={"Network error!","Server error!","Authorization failure","Internal error! Contact Admin!","New updates!"};
        Context context=getApplicationContext();
        Toast.makeText(context, values[error_code - 1], Toast.LENGTH_SHORT).show();
        //ActionBar actionBar=getActionBar();
        //actionBar.show();
    }

    private String getIMEI()
    {
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }
    private int checkConnection()
    {
        final ConnectivityManager ComMgr=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo nwInfo=ComMgr.getActiveNetworkInfo();
        if(nwInfo!=null && nwInfo.isConnected())
            return 1;
        Log.d("NMe","No Connection!");
        return 0;
    }



    private void updateChecker(JSONArray jsonArray)
    {

        int localDbCount=new LocalDBHandler(this).cursorCount();
        int dataCount=jsonArray.length();
        if(dataCount!=localDbCount)
        {
            Log.d("NMe","Update Exists!");
            Log.d("NMe",""+(dataCount-localDbCount));
            pushJSONtoDB(jsonArray, (dataCount - localDbCount));
        }
        else
        {
            Log.d("NMe","No update");
            populateListView();
        }
    }


    public class downloadJSON extends AsyncTask<Void,Void,JSONArray>{

        JSONArray jsonArray;
        OnlineDbDownloader downloaderObject = new OnlineDbDownloader(getIMEI());
        int error_code;
        @Override
        protected JSONArray doInBackground(Void... params) {
            downloaderObject.downloadData();
            jsonArray=downloaderObject.getJSON();
            error_code=downloaderObject.getErrorCode();
            return jsonArray;
        }

        @Override
        protected void onPreExecute() {
            setContentView(R.layout.splash);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            setContentView(R.layout.notice_list);
            if(refresh_flag==1)
                updateChecker(jsonArray);
            else
                pushJSONtoDB(jsonArray);
        }

        public int getErrorCode()
        {
            return error_code;
        }
    }



    //---------------------------------------ACTION BAR AND NAVIGATION DRAWER--------------------------------------------//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.layout.action_bar_layout,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_refresh)
            refresh();
        return super.onOptionsItemSelected(item);
    }
}
