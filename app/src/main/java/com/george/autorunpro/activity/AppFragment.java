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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.george.autorunpro.EventAdder;
import com.george.autorunpro.Pojo_fetch_data;
import com.george.autorunpro.R;
import com.george.autorunpro.SqlOperator;
import com.george.autorunpro.adapter.RecyclerviewAdapter;
import java.util.ArrayList;
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

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EventAdder.class);
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView) layout.findViewById(R.id.app_recycler_view);
        recyclerviewAdapter = new RecyclerviewAdapter(getActivity(), getData(getContext()));
        recyclerView.setAdapter(recyclerviewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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

               // final String title = (String) ((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");
                //final Drawable icon = packageManager.getApplicationIcon(applicationInfo);
                datalist.add(new Pojo_fetch_data(

                        c.getInt(c.getColumnIndex("id")),        // datalist starts at index 0 //database id starts at 1
                        c.getString(c.getColumnIndex("appname")),     // title                      //0th item has id 1
                        c.getString(c.getColumnIndex("time")),
                        "na"
                ));
            } while (c.moveToNext());
            c.close();
        }

        return datalist;
    }

}
