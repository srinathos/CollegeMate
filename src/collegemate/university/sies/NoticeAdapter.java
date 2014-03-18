package collegemate.university.sies;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: Shata
 * Date: 2/6/14
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class NoticeAdapter extends CursorAdapter{

    //private LayoutInflater mLayoutInflator;
    Context context;
    Cursor cursor;
    final String P_KEY="_id" ;
    final String KEY_TITLE="title";
    final String KEY_DATE="date";
    final String KEY_CONTENT="content";
    final String KEY_FROM="sender";
    final String TABLE_NAME="notices";
    final String KEY_MESSAGE_STATUS="message_status";

    public NoticeAdapter(Context context, Cursor cursor)
    {
        super(context,cursor,0);
        this.context=context;
        this.cursor=cursor;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater  mLayoutInflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mLayoutInflator.inflate(R.layout.notice_list_row_layout,parent,false);
        return view;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView NoticeHeader=(TextView) view.findViewById(R.id.notice_row_noticehead);
        TextView NoticeSender=(TextView) view.findViewById(R.id.notice_row_noticesender);
        ImageView status_image=(ImageView) view.findViewById(R.id.notice_row_messagestatus);
        int status=cursor.getInt(cursor.getColumnIndex(KEY_MESSAGE_STATUS));
        if(status==1)
        {
            status_image.setImageResource(R.drawable.navigation_accept);
            //Log.d("NMe","Read Notice");
        }
        else
            status_image.setImageResource(R.drawable.content_email);
        NoticeHeader.setText(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
        NoticeSender.setText(cursor.getString(cursor.getColumnIndex(KEY_FROM)));
    }
}
