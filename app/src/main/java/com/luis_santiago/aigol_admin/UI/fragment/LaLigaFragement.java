package com.luis_santiago.aigol_admin.UI.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luis_santiago.aigol_admin.R;
import com.luis_santiago.aigol_admin.tools.adapters.PageAdapter;
import com.luis_santiago.aigol_admin.tools.utils.Keys;
import com.luis_santiago.aigol_admin.tools.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class LaLigaFragement extends Fragment {


    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    public LaLigaFragement() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_la_liga_fragement, container, false);
        init(view);
        //Setting the page adapter for every Fragment
        Bundle bundle = new Bundle();
        bundle.putString(Keys.KEY_LEAGUE, "LigaEspañola");

        PageAdapter mPagerAdapter = Utils.setUpFragment(getContext(), bundle);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        return view;
    }

    private void init(View v){
        mViewPager = v.findViewById(R.id.pager);
        mTabLayout = v.findViewById(R.id.tab_layout);
    }
}
