package aegis.com.aegis.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
            fragment = new FragmentA();
        else if (position == 1)
            fragment = new FragmentB();
        /*else if (position == 2)
            fragment = new FragmentC();*/
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
            title = "Contacts";
        else if (position == 1)
            title = "Notifications";
        else if (position == 2)
            title = "Settings";

        return title;
    }
}
