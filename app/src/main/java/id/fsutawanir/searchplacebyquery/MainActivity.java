package id.fsutawanir.searchplacebyquery;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;

/**
 * Search place by typing, with typing delay 3000 ms.
 * To simplify retrieving google place data process, use lib se.walkercrou:google-places-api-java
 *
 * @author Fanny Irawan Sutawanir (fannyirawans@gmail.com)
 * @see https://github.com/windy1/google-places-api-java
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Retrieved from google place API
     */
    private final static String GOOGLE_PLACE_API_KEY = "AIzaSyCZcwiFUqsnE7FIuT5Mc-VTT9tizbWXF1I";

    private GooglePlaces mGooglePlaces;
    private GpsTracker mGpsTracker;

    private EditText mPlaceText;
    private RecyclerView mRecyclerView;
    private PlaceAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlaceText = (EditText) findViewById(R.id.editText);
        mPlaceText.addTextChangedListener(new TextWatcher() {
            private Timer timer = new Timer();
            private final long DELAY = 3000; // milliseconds

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                                   @Override
                                   public void run() {
                                       String query = mPlaceText.getText().toString();
                                       if (query.trim().isEmpty()) {
                                           return;
                                       }
                                       new PlaceTask(mPlaceText.getText().toString()).execute();
                                   }
                               },
                        DELAY
                );
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new PlaceAdapter(new ArrayList<Place>() );
        mRecyclerView.setAdapter(mAdapter);

        mGpsTracker = new GpsTracker(this);
        mGooglePlaces = new GooglePlaces(GOOGLE_PLACE_API_KEY);
        if(!mGpsTracker.getIsGPSTrackingEnabled()) {
            mGpsTracker.showSettingsAlert();
        }
    }

    class PlaceAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final List<Place> mPlaces;

        public PlaceAdapter(List<Place> places) {
            mPlaces = places;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext() ).inflate(R.layout.content_place_row, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(mPlaces.get(position).getName() );
        }

        @Override
        public int getItemCount() {
            return mPlaces.size();
        }

        public void clear() {
            mPlaces.clear();
        }

        public void add(List<Place> places) {
            mPlaces.addAll(places);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.info_text);
        }
    }

    class PlaceTask extends AsyncTask<Void, Void, Void> {

        private final String mQuery;

        public PlaceTask(String query) {
            mQuery = query;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(!mGpsTracker.getIsGPSTrackingEnabled()) {
                return null;
            }
            if(mGpsTracker.getLatitude() == 0 || mGpsTracker.getLongitude() == 0) {
                return null;
            }

            List<Place> places = mGooglePlaces.getPlacesByQuery(mQuery, GooglePlaces.DEFAULT_RESULTS,
                    Param.name("location").value(String.format("%f, %f", mGpsTracker.getLatitude(), mGpsTracker.getLongitude())),
                    Param.name("radius").value(10000)
            );
            mAdapter.clear();
            mAdapter.add(places);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
        }
    }
}
