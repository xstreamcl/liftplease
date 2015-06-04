package in.co.liftplease.myapplication;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by yogesh.choudhary on 03/06/15.
 */
public class ListViewItem {
    public final String image_uri;       // the drawable for the ListView item ImageView
    public final String title;        // the text for the ListView item title
    public final int time_left;

    public ListViewItem(String image_uri, String title, int time_left) {
        this.image_uri = image_uri;
        this.title = title;
        this.time_left = time_left;
    }
}
