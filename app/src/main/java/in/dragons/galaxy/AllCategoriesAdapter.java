package in.dragons.galaxy;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;
import com.percolate.caffeine.ViewUtils;

import java.util.ArrayList;
import java.util.Map;

public class AllCategoriesAdapter extends RecyclerView.Adapter<AllCategoriesAdapter.ViewHolder> {

    private Context context;
    private Map<String, String> categories;

    private Integer[] categoriesImg = {
            R.drawable.ic_art_design,
            R.drawable.ic_auto_vehicles,
            R.drawable.ic_beauty,
            R.drawable.ic_books_reference,
            R.drawable.ic_business,
            R.drawable.ic_comics,
            R.drawable.ic_communication,
            R.drawable.ic_dating,
            R.drawable.ic_education,
            R.drawable.ic_entertainment,
            R.drawable.ic_events,
            R.drawable.ic_family,
            R.drawable.ic_finance,
            R.drawable.ic_food_drink,
            R.drawable.ic_games,
            R.drawable.ic_health_fitness,
            R.drawable.ic_house_home,
            R.drawable.ic_libraries_demo,
            R.drawable.ic_lifestyle,
            R.drawable.ic_maps_navigation,
            R.drawable.ic_medical,
            R.drawable.ic_music__audio,
            R.drawable.ic_news_magazines,
            R.drawable.ic_parenting,
            R.drawable.ic_personalization,
            R.drawable.ic_photography,
            R.drawable.ic_productivity,
            R.drawable.ic_shopping,
            R.drawable.ic_social,
            R.drawable.ic_sports,
            R.drawable.ic_tools,
            R.drawable.ic_travel_local,
            R.drawable.ic_video_editors,
            R.drawable.ic_android_wear,
            R.drawable.ic_weather,
    };

    private View view;
    private ViewHolder viewHolder;
    private SharedPreferencesTranslator translator;

    public AllCategoriesAdapter(Context context, Map<String, String> categories) {
        this.categories = categories;
        this.context = context;
        translator = new SharedPreferencesTranslator(PreferenceManager.getDefaultSharedPreferences(context));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView topLabel;
        ImageView topImage;
        LinearLayout topContainer;

        ViewHolder(View v) {
            super(v);
            topLabel = ViewUtils.findViewById(v, R.id.top_cat_name);
            topImage = ViewUtils.findViewById(v, R.id.top_cat_img);
            topContainer = ViewUtils.findViewById(v, R.id.all_cat_container);
        }
    }

    @NonNull
    @Override
    public AllCategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.all_cat_item, parent, false);
        viewHolder = new ViewHolder(view);
        Aesthetic.get()
                .colorAccent()
                .take(1)
                .subscribe(color -> viewHolder.topImage.setColorFilter(color));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.topLabel.setText(translator.getString(new ArrayList<>(categories.keySet()).get(holder.getAdapterPosition())));
        holder.topImage.setImageDrawable(context.getResources().getDrawable(categoriesImg[holder.getAdapterPosition()]));
        holder.topContainer.setOnClickListener(v -> {
            GalaxyActivity activity = (GalaxyActivity) view.getContext();
            activity.setTitle(translator.getString(new ArrayList<>(categories.keySet()).get(holder.getAdapterPosition())));
            Fragment myFragment = new EndlessScrollFragment();
            Bundle arguments = new Bundle();
            arguments.putString("CategoryID", new ArrayList<>(categories.keySet()).get(holder.getAdapterPosition()));
            myFragment.setArguments(arguments);
            activity.getFragmentManager().beginTransaction().replace(R.id.content_frame, myFragment, "BAZINGA").addToBackStack(null).commit();
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
