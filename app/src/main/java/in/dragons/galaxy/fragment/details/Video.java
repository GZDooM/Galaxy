package in.dragons.galaxy.fragment.details;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.percolate.caffeine.ViewUtils;
import com.squareup.picasso.Picasso;

import in.dragons.galaxy.DetailsFragment;
import in.dragons.galaxy.R;
import in.dragons.galaxy.model.App;

public class Video extends AbstractHelper {

    public Video(DetailsFragment detailsFragment, App app) {
        super(detailsFragment, app);
    }

    private String getID(String URL) {
        if (URL.contains("/youtu.be/"))
            URL = URL.substring(URL.lastIndexOf('/') + 1, URL.length());
        else if (URL.contains("feature"))
            URL = URL.substring(URL.indexOf('=') + 1, URL.lastIndexOf('&'));
        else
            URL = URL.substring(URL.indexOf('=') + 1, URL.length());
        return URL;
    }

    @Override
    public void draw() {
        if (TextUtils.isEmpty(app.getVideoUrl())) {
            return;
        }

        String vID = getID(app.getVideoUrl());
        String URL = "https://img.youtube.com/vi/" + vID + "/hqdefault.jpg";

        ImageView imageView = ViewUtils.findViewById(detailsFragment.getActivity(), R.id.thumbnail);
        Picasso.with(detailsFragment.getActivity())
                .load(URL)
                .fit()
                .centerCrop()
                .into(imageView);

        detailsFragment.getActivity().findViewById(R.id.app_video).setVisibility(View.VISIBLE);

        ImageView play = ViewUtils.findViewById(detailsFragment.getActivity(), R.id.vid_play);
        play.setOnClickListener(v -> {
            try {
                detailsFragment.getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(app.getVideoUrl())));
            } catch (ActivityNotFoundException ignored) {
            }
        });
    }
}