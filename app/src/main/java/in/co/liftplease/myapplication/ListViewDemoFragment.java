package in.co.liftplease.myapplication;

import android.content.res.Resources;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yogesh.choudhary on 03/06/15.
 */
public class ListViewDemoFragment extends ListFragment {
    private List<ListViewItem> mItems;
    SessionManager session;
    String userProfileImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // initialize the items list
        mItems = new ArrayList<ListViewItem>();
        Resources resources = getResources();
        session = new SessionManager(getActivity());

        HashMap<String, String> user = session.getUserDetails();
        userProfileImage = user.get(SessionManager.KEY_IMAGE);

        JSONArray listArray = (JSONArray)((MapsActivity) getActivity()).getListData();

        for(int i = 0; i < listArray.length(); i++)
        {
            try {
                JSONObject object = listArray.getJSONObject(i);
//                String trip_creation_time = object.getString("trip_creation_time");
                int time_left = (int)(Double.parseDouble(object.getString("trip_creation_time")) -  (System.currentTimeMillis())/1000);
                mItems.add(new ListViewItem(object.getString("image"), object.getString("name"), time_left));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // initialize and set the list adapter
        setListAdapter(new ListViewDemoAdapter(getActivity(), mItems));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove the dividers from the ListView of the ListFragment
//        getListView().setDivider(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        ListViewItem item = mItems.get(position);

        // do something
        Toast.makeText(getActivity(), item.title, Toast.LENGTH_SHORT).show();
    }
}
