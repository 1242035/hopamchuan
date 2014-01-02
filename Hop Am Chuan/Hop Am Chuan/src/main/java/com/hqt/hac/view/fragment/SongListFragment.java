package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.hqt.hac.config.Config;
import com.hqt.hac.helper.adapter.InfinityAdapter;
import com.hqt.hac.helper.adapter.SongListAdapter;
import com.hqt.hac.helper.widget.InfinityListView;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dal.SongDataAccessLayer;
import com.hqt.hac.helper.widget.SongListRightMenuHandler;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.ArrayList;
import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Fragment uses for viewing songs as categories
 */
public class SongListFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        InfinityListView.ILoaderContent,
        IHacFragment {

    public static final String TAG = makeLogTag(SongListFragment.class);

    public int titleRes = R.string.title_activity_song_list_fragment;

    /** Main Activity for reference */
    private MainActivity activity;
    private InfinityListView mListView;
    private List<Song> songs;

    /** One popup menu for all items **/
    private PopupWindow popupWindow = null;

    /** Adapter for this fragment */
    private SongListAdapter songlistAdapter;

    /** Adapter use for loading when go to ending list */
    private InfinityAdapter infAdapter;

    /** song list mode
     * 0: recent songs
     * 1: new songs
     * 2: rand songs
     * **/
    private int songListMode = 0;

    public SongListFragment() {
    }



    @Override
    public int getTitle() {
        return titleRes;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);

        /** Spinner : create mAdapter for Spinner */
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner_method_list);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.
                createFromResource(getActivity().getApplicationContext(),
                        R.array.song_list_method, R.layout.custom_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner.setAdapter(adapter);    // Apply the mAdapter to the spinner
        spinner.setOnItemSelectedListener(this);    // because this fragment has implemented method


        /** Default song list **/
        //songs = SongDataAccessLayer.getRecentSongs(activity.getApplicationContext(), 0, 0);
        songs = new ArrayList<Song>();
        songlistAdapter = new SongListAdapter(activity, songs);

        /** ListView Configure */
        mListView = (InfinityListView) rootView.findViewById(R.id.list_view);
        /** config mode for this ListView.
         *  this ListView is full rich function. See document for more detail
         */
        mListView.setLoader(this);
        mListView.setFirstProcessLoading(true);
        mListView.setNumPerLoading(Config.DEFAULT_SONG_NUM_PER_LOAD);
        mListView.setRunningBackground(true);

        // Event for right menu click
        popupWindow = DialogUtils.createPopup(inflater, R.layout.popup_songlist_menu);
        SongListRightMenuHandler.setRightMenuEvents(activity, popupWindow);

        // Event received from mAdapter.
        songlistAdapter.contextMenuDelegate = new SongListAdapter.IContextMenu() {
            @Override
            public void onMenuClick(View view, Song song, ImageView theStar) {
                // Show the popup menu and set selectedSong, theStar
                SongListRightMenuHandler.openPopupMenu(view, song, theStar);
            }
        };

        // Event for Item Click on ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SongDetailFragment fragment = new SongDetailFragment();
                Bundle arguments = new Bundle();
                arguments.putParcelable("song", songs.get(position));
                fragment.setArguments(arguments);
                activity.switchFragmentNormal(fragment);
                activity.changeTitleBar(songs.get(position).title);
            }
        });

        return rootView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case 0:
                // Moi xem gan day
                //songs = SongDataAccessLayer.getRecentSongs(activity.getApplicationContext(), 0, Config.DEFAULT_SONG_LIST_COUNT);
                songs = new ArrayList<Song>();
                songlistAdapter.setSongs(songs);
                break;
            case 1:
                // Moi cap nhat
                // songs = SongDataAccessLayer.getNewSongs(activity.getApplicationContext(), 0, Config.DEFAULT_SONG_LIST_COUNT);
                songs = new ArrayList<Song>();
                songlistAdapter.setSongs(songs);
                break;
            case 2:
                // Bai hat ngau nhien
                // songs = SongDataAccessLayer.getRandSongs(activity.getApplicationContext(), Config.DEFAULT_SONG_LIST_COUNT);
                songs = new ArrayList<Song>();
                songlistAdapter.setSongs(songs);
                break;
            default:
                // do nothing
        }
        // Set mode
        songListMode = position;
        // Reset the ListView
        mListView.resetListView(songlistAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////// METHOD FOR ENDLESS LOADING //////////////////////////
    @Override
    public List load(int offset, int count) {
        NetworkUtils.stimulateNetwork(Config.SONG_LOAD_SMOOTHING_DELAY);
        List<Song> result;
        switch (songListMode) {
            case 0:
                result = SongDataAccessLayer.getRecentSongs(getActivity().getApplicationContext(), offset, count);
                break;
            case 1:
                result = SongDataAccessLayer.getNewSongs(getActivity().getApplicationContext(), offset, count);
                break;
            case 2:
                result = SongDataAccessLayer.getRandSongs(getActivity().getApplicationContext(), count);
                break;
            default:
                result = new ArrayList<Song>();
        }
        return result;
    }

}
