package collegemate.university.sies;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: Srinath
 * Date: 2/2/14
 * Time: 9:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExpandNotice extends Activity {

    final String P_KEY="_id" ;
    final String KEY_TITLE="title";
    final String KEY_DATE="date";
    final String KEY_CONTENT="content";
    final String KEY_FROM="sender";
    final String TABLE_NAME="notices";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_expanded);
        int position=getIntent().getIntExtra("position",0);
        position=position-new LocalDBHandler(this).cursorCount();
        position=position*(-1);
        Cursor data=new LocalDBHandler(this).getNoticeData(position);

        TextView header=(TextView)findViewById(R.id.notice_header);
        TextView date=(TextView)findViewById(R.id.notice_date);
        TextView body=(TextView)findViewById(R.id.notice_date);
        TextView sender=(TextView)findViewById(R.id.notice_sender);
        data.moveToFirst();
        header.setText(data.getString(data.getColumnIndex(KEY_TITLE)));
        date.setText(data.getString(data.getColumnIndex(KEY_DATE)));
        body.setText(data.getString(data.getColumnIndex(KEY_CONTENT)));
        sender.setText(data.getString(data.getColumnIndex(KEY_FROM)));
        new LocalDBHandler(getApplicationContext()).markRead(position);

    }
}