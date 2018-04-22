package hu.fallen.bakingapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import hu.fallen.bakingapp.recipe.Recipe;
import hu.fallen.bakingapp.recipe.Step;

/**
 * A fragment representing a single Recipe detail screen.
 * This fragment is either contained in a {@link RecipeDetailsActivity}
 * in two-pane mode (on tablets) or a {@link RecipeStepActivity}
 * on handsets.
 */
public class RecipeStepFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM = "item";

    private Step mItem;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private Context context;
    private Uri uri;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeStepFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM)) {
            mItem = (Step) getArguments().getParcelable(ARG_ITEM);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getShortDescription());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_step, container, false);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.recipe_detail)).setText(mItem.getDescription());
            mPlayerView = rootView.findViewById(R.id.step_player);
            initializePlayer(rootView.getContext(), Uri.parse(mItem.getVideoURL()));
        }

        return rootView;
    }

    private void initializePlayer(Context context,  Uri uri) {
        this.context = context;
        this.uri = uri;
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector(), new DefaultLoadControl());
        mPlayerView.setPlayer(mExoPlayer);
        MediaSource mediaSource = new ExtractorMediaSource(
                uri,
                new DefaultDataSourceFactory(context, Util.getUserAgent(context, "Baking App")),
                new DefaultExtractorsFactory(),
                null,
                null);
        mExoPlayer.prepare(mediaSource);
        mExoPlayer.seekTo(currentPosition);
        mExoPlayer.setPlayWhenReady(true);
    }

    private long currentPosition = 0;

    @Override
    public void onPause() {
        mExoPlayer.setPlayWhenReady(false);
        currentPosition = mExoPlayer.getCurrentPosition();
        mExoPlayer.stop();
        mExoPlayer.release();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentPosition != 0) initializePlayer(context, uri);
    }

}
