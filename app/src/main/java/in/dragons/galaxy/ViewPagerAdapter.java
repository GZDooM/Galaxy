package in.dragons.galaxy;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    ViewPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new InstalledAppsFragment();
        } else if (position == 1) {
            return new UpdatableAppsFragment();
        } else {
            return new CategoryListFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.action_apps);
            case 1:
                return mContext.getString(R.string.action_updates);
            case 2:
                return mContext.getString(R.string.action_categories);
            default:
                return null;
        }
    }

}
