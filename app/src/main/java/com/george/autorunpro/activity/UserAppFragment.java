package com.george.autorunpro.activity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.george.autorunpro.EventAdder;
import com.george.autorunpro.Pojo_fetch_data;
import com.george.autorunpro.R;
import com.george.autorunpro.SqlOperator;
import java.util.ArrayList;
import java.util.List;


public class UserAppFragment extends Fragment {

    static ContentAdapter adapter;
    static RecyclerView recyclerView;
    static SqlOperator sqlOperator;
    public UserAppFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int next_mode,next_appname;
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                //  Toast.makeText(getContext(),"FAB",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), EventAdder.class);
                startActivity(intent);
            }
        });
        sqlOperator = new SqlOperator(getContext());
        Cursor c = sqlOperator.selectRecords();
        List<Pojo_fetch_data> datalist = new ArrayList<>();
        PackageManager packageManager = getContext().getPackageManager();
        ApplicationInfo applicationInfo = null;

        if (c.moveToFirst()) {
            do {

                try {
                    applicationInfo = packageManager.getApplicationInfo(c.getString(c.getColumnIndex("appname")), 0);
                } catch (final PackageManager.NameNotFoundException e) {
                }
                final String title = (String) ((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");
                final String stop_time,start_time;
                System.out.print("Title = " + title);
                if( c.getInt(c.getColumnIndex("id")) > 1 && ){
                    //checking mode and package names
                    if   ((c.getInt(c.getColumnIndex("id")) == last_id + 1)) &&
                         (title.equals(last_title))  &&
                         (c.getInt(c.getColumnIndex("mode")) == 1 && last_mode == 0)){
                             
                          stop_time =  c.getString(c.getColumnIndex("time"));
                          datalist.remove(last_id - 1) //datalist starts with 0   
                          
                            datalist.add(new Pojo_fetch_data(

                                   last_id,        // datalist starts at index 0 //database id starts at 1
                                   title,     // title                      //0th item has id 1
                                   start_time,
                                   stop_time"
                                               ));
                            continue;                   
                         }
                }
                
                datalist.add(new Pojo_fetch_data(

                        c.getInt(c.getColumnIndex("id")),        // datalist starts at index 0 //database id starts at 1
                        title,     // title                      //0th item has id 1
                        c.getString(c.getColumnIndex("time")),
                        "no:stop"
                ));
               //version 2 commit
               last_id = c.getInt(c.getColumnIndex("id"));
               last_mode = c.getInt(c.getColumnIndex("mode"));
               last_title = title;
               start_time = c.getString(c.getColumnIndex("time"));
            } while (c.moveToNext());
            c.close();
            //using recycle view cutomizing card views
            recyclerView = (RecyclerView) inflater.inflate(
                    R.layout.recycler_view, container, false);
            adapter = new ContentAdapter(datalist);
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        return recyclerView;
    }
    //view holder for cards data start
    public static class ViewHolder extends RecyclerView.ViewHolder {

        static TextView appname,start_time,stop_time;
        static ImageButton deleteImageButton;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_home_card_list, parent, false));
            appname = (TextView) itemView.findViewById(R.id.appname);
            start_time = (TextView) itemView.findViewById(R.id.start_time);
            stop_time = (TextView) itemView.findViewById(R.id.stop_time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, EventAdder.class);
                    context.startActivity(intent);
                }
            });
             deleteImageButton =
                    (ImageButton) itemView.findViewById(R.id.delete_button);
            ImageButton shareImageButton =
                    (ImageButton) itemView.findViewById(R.id.share_button);
            shareImageButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Added to Favorite",
                            Snackbar.LENGTH_LONG).show();
                }
            });

        }
    } //view holder end

    // start recycleview.adapter wrapping viewholder created
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Pojo_fetch_data> datalist = new ArrayList<>();

        public ContentAdapter(List<Pojo_fetch_data> datalist){
            this.datalist = datalist;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int positio) {

            final int position = holder.getAdapterPosition();
            final Pojo_fetch_data temp =  datalist.get(position);
            System.out.println("temp.id on onbind view holder ="+temp.id);
            System.out.println("In onbindViewholder card position = "+position);
            holder.appname.setText(temp.appname);
            holder.start_time.setText(temp.start_time);
            holder.stop_time.setText(temp.stop_time);

            holder.deleteImageButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    Snackbar.make(v, "Alarm deleted",
                            Snackbar.LENGTH_LONG).show();
                    // System.out.println("In onbindViewholder temp.id  = "+temp.id);
                    sqlOperator.delete(temp.id); //listview starts with 0
                    System.out.println("inside onclick = "+temp.id);
                    System.out.println("position = "+position);
                    System.out.println(datalist.get(position).appname);
                    removeData(position,datalist);

                }
            });

        }

        @Override
        public int getItemCount() {

            return datalist.size();
        }



    }
    // end recyclerview.adapter wrapping viewholder created

    @UiThread
    protected static void removeData(int position, List<Pojo_fetch_data> list) {

        list.remove(position);
        recyclerView.removeViewAt(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, list.size());
        adapter.notifyDataSetChanged();
        /*recyclerView.setAdapter(new ContentAdapter(list));
        recyclerView.invalidate();*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}


