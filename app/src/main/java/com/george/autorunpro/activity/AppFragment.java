package com.george.autorunpro.activity;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import com.george.autorunpro.EventAdder;
import com.george.autorunpro.Pojo_fetch_data;
import com.george.autorunpro.R;
import com.george.autorunpro.SqlOperator;
import com.george.autorunpro.adapter.RecyclerviewAdapter;
import com.george.autorunpro.model.RecyclerScroll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private RecyclerviewAdapter recyclerviewAdapter;

    public AppFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AppFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppFragment newInstance(String param1, String param2) {
        AppFragment fragment = new AppFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_app, container, false);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.simple_grow);
        final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getContext(), EventAdder.class);
                startActivityForResult(new Intent(getContext(), EventAdder.class), 121);
            }
        });

        recyclerView = (RecyclerView) layout.findViewById(R.id.app_recycler_view);
        recyclerviewAdapter = new RecyclerviewAdapter(getActivity(), getData(getContext()),recyclerView);
        recyclerView.setAdapter(recyclerviewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //fab up down animation
        final  int fabMargin = 16;
        recyclerView.addOnScrollListener(new RecyclerScroll() {
            @Override
            public void show() {
                fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }
            @Override
            public void hide() {
                fab.animate().translationY(fab.getHeight() + fabMargin).setInterpolator(new AccelerateInterpolator(2)).start();
            }
        });
        //animation end

        //fab grow animation start
        fab.startAnimation(animation);
        return layout;
    }

    public static List<Pojo_fetch_data> getData(Context context) {

        List<Pojo_fetch_data> datalist = new ArrayList<>();
        ApplicationInfo applicationInfo = null;
        SqlOperator sqlOperator = new SqlOperator(context);
        Cursor c = sqlOperator.selectRecords();
        PackageManager packageManager = context.getPackageManager();
        if (c.moveToFirst()) {
            do {
                //checkn mode =1 for kill events
                if (c.getInt(c.getColumnIndex("mode")) == 1) {

                    datalist.get(datalist.size() - 1).stop_time = c.getString(c.getColumnIndex("time"));
                    continue;
                }

                try {
                    applicationInfo = packageManager.getApplicationInfo(c.getString(c.getColumnIndex("appname")), 0);

                }
                catch (final PackageManager.NameNotFoundException e) { }

                final String title = (String) ((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");
                //final Drawable icon = packageManager.getApplicationIcon(applicationInfo);
                datalist.add(new Pojo_fetch_data(

                        c.getInt(c.getColumnIndex("id")),        // datalist starts at index 0 //database id starts at 1
                        title,     // title                      //0th item has id 1
                        c.getString(c.getColumnIndex("time")),
                        "na",
                        c.getInt(c.getColumnIndex("status"))
                ));
            } while (c.moveToNext());
            c.close();
        }

        return datalist;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 121) {

            String alarmtype = data.getExtras().get("passed_item").toString();
            Cursor c = new SqlOperator(getContext()).selectRecord("select * from AppAlarms order by id desc limit 2");
            if(c != null) {
                c.moveToFirst();
                int id = c.getInt(c.getColumnIndex("id"));
                ApplicationInfo applicationInfo = null;
                PackageManager packageManager = getContext().getPackageManager();
                //app name from package name
                try {
                    applicationInfo = packageManager.getApplicationInfo(c.getString(c.getColumnIndex("appname")), 0);

                }
                catch (final PackageManager.NameNotFoundException e) { e.printStackTrace();}
                final String title = (String) ((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");
                //app name obtained
                String start_time = c.getString(c.getColumnIndex("time"));
                int status = c.getInt(c.getColumnIndex("status"));
                Pojo_fetch_data new_data;
                if (alarmtype.equals("mono")) {
                    new_data = new Pojo_fetch_data(id, title, start_time, "na", status);
                } else {
                    //start time time is the 2nd row as order is desc
                    c.moveToNext();
                    String stop_time = start_time;
                    start_time = c.getString(c.getColumnIndex("time"));//start time
                    new_data = new Pojo_fetch_data(id, title, start_time, stop_time, status);

                }
                recyclerviewAdapter.addData(new_data);
            }
            System.out.print("running onactivity");
        }
        System.out.print("running onactivity");
        super.onActivityResult(requestCode, resultCode, data);
    }//onactivity result end

} //fragment close
