package com.george.autorunpro.adapter;

import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.george.autorunpro.AlarmSet;
import com.george.autorunpro.EventAdder;
import com.george.autorunpro.Pojo_fetch_data;
import com.george.autorunpro.R;
import com.george.autorunpro.SqlOperator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Akshay on 15-Mar-16.
 */
public class RecyclerviewAdapter extends RecyclerView.Adapter <RecyclerviewAdapter.myViewHolder>{


    private LayoutInflater inflator;
    List<Pojo_fetch_data> datalist = Collections.emptyList();
    private int lastPosition = -1;
    private RecyclerView recyclerView;
    private Context context;
    SqlOperator sqlOperator;

    public RecyclerviewAdapter(Context context,List<Pojo_fetch_data> datalist,RecyclerView recyclerView){
        inflator = LayoutInflater.from(context);
        this.datalist = datalist;
        this.recyclerView = recyclerView;
        this.context = context;
        this.sqlOperator = new SqlOperator(context);

    }
    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = inflator.inflate(R.layout.fragment_home_card_list, parent,false);
        myViewHolder holder = new myViewHolder(view);
        return holder;
    }
    @Override
    public void onViewDetachedFromWindow(myViewHolder holder){

        holder.itemView.clearAnimation();

    }

    @Override
    public void onBindViewHolder(final myViewHolder holder, final int position) {

        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(),
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        final Pojo_fetch_data current_data = this.datalist.get(position);
        System.out.println("temp.id on onbind view holder ="+current_data.id);
        System.out.println("In onbindViewholder card position = "+position);

        //setting icon and appname from package name start

                ApplicationInfo applicationInfo = null;
                PackageManager packageManager = context.getPackageManager();
                //app name from package name
                try {
                    applicationInfo = packageManager.getApplicationInfo(current_data.appname, 0);

                }
                catch (final PackageManager.NameNotFoundException e) { e.printStackTrace();}
                final String title = (String) ((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");
                final Drawable icon = packageManager.getApplicationIcon(applicationInfo);
                icon.setBounds(0, 0, 80, 80);
                holder.appname.setCompoundDrawables(icon, null, null, null);
                holder.appname.setCompoundDrawablePadding((int)dp_to_px(7));
                holder.cardview.getLayoutParams().height = (int)dp_to_px(200);
                holder.appname.setText(title);

        //setting appname and icon end
        holder.stop_time.setVisibility(View.GONE);
        holder.stop.setVisibility(View.GONE);
        holder.stop_padding.setVisibility(View.GONE);
        holder.cardview.getLayoutParams().height = (int)dp_to_px(160);

        holder.start_time.setText(time_in_12hr(current_data.start_time));

        holder.weekdays.setText(current_data.weekday_status);



        holder.deleteImageButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                String[] id = new String[1];
                if(current_data.stop_time.equals("na")){

                    id[0] = Integer.toString(current_data.id);
                    AlarmSet.CancelAlarm(v.getContext(),current_data.id);
                    sqlOperator.delete(id);
                    System.out.print("in na deleting id="+current_data.id);
                    //   height = 150;
                }
                removeData(holder.getAdapterPosition(),datalist);
            }
        });

        holder.swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {

                    case R.id.onoffbtn:

                        if (!isChecked) {
                            Log.i("TAG","not-checked");
                            if(current_data.status == 1){

                                ContentValues cv = new ContentValues();
                                cv.put("status",0);

                                sqlOperator.updateRecord(current_data.id,cv);
                                AlarmSet.CancelAlarm(buttonView.getContext(),current_data.id);
                                datalist.get(position).status = 0;
                                current_data.status = 0;
                                Snackbar.make(holder.itemView, "App Timer Off",
                                        Snackbar.LENGTH_LONG).show();
                            }


                        } else {
                            Log.i("TAG","checked");
                            if(current_data.status == 0){
                                ContentValues cv = new ContentValues();

                                String query = "select * from AppAlarms where " +
                                        "(monday = 1 OR tuesday = 1 OR wednesday = 1 OR " +
                                        "thursday = 1 OR friday = 1 OR saturday = 1 OR sunday = 1) " +
                                        "AND id ="+current_data.id;
                                Cursor c = sqlOperator.selectRecord(query);

                                Calendar calendar = getCalender(current_data.start_time);

                                if (c.getCount() != 0)
                                    AlarmSet.SetRepeatAlarm(buttonView.getContext(),calendar,current_data.id);
                                else
                                    AlarmSet.setOnetimeTimer(buttonView.getContext(),calendar,current_data.id);

                                cv.put("status",1);
                                sqlOperator.updateRecord(current_data.id,cv);

                                datalist.get(position).status = 1;
                                current_data.status = 1;
                                Snackbar.make(holder.itemView, "App Timer On",
                                        Snackbar.LENGTH_LONG).show();
                            }

                        }
                        break;

                    default:
                        break;
                }
            }
        });

        if(current_data.status == 0)
            holder.swt.setChecked(false);
        else
            holder.swt.setChecked(true);
        holder.itemView.startAnimation(animation);
        lastPosition = position;
    }


    @UiThread
    protected void removeData(int position, List<Pojo_fetch_data> list) {

        list.remove(position);
        notifyItemRemoved(position);

    }
    @UiThread
    public void addData(Pojo_fetch_data data){

        this.datalist.add(data);
        notifyItemInserted(this.datalist.size() - 1);
        this.recyclerView.smoothScrollToPosition(this.datalist.size() - 1);

    }

    @Override
    public int getItemCount() {
        return this.datalist.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        TextView appname, start_time, stop_time, stop, stop_padding, weekdays, start;
        ImageButton deleteImageButton;
        CardView cardview;
        SwitchCompat swt;

        public myViewHolder(final View itemView) {
            super(itemView);

            appname = (TextView) itemView.findViewById(R.id.appname);
            start_time = (TextView) itemView.findViewById(R.id.start_time);
            stop_time = (TextView) itemView.findViewById(R.id.stop_time);
            stop = (TextView) itemView.findViewById(R.id.stop);
            start = (TextView) itemView.findViewById(R.id.start);
            stop_padding = (TextView) itemView.findViewById(R.id.padding1);
            weekdays = (TextView) itemView.findViewById(R.id.weekday);
            cardview = (CardView) itemView.findViewById(R.id.card_view);
            swt = (SwitchCompat) itemView.findViewById(R.id.onoffbtn);
            deleteImageButton = (ImageButton) itemView.findViewById(R.id.delete_button);


        }
    }



   private String time_in_12hr(String time){

       try{
           final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
           final Date dateObj = sdf.parse(time);
           String time_12hr = new SimpleDateFormat("hh:mm aa").format(dateObj).toString();
           return time_12hr;
       }catch (Exception e){
           e.printStackTrace();
           return time;
       }
   }

    protected  Calendar getCalender(String time){

        String[] splited = time.split(":");
        int hour =Integer.parseInt(splited[0]);
        int min = Integer.parseInt(splited[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 00);
        if(Calendar.getInstance().after(calendar)){
            // Move to tomorrow
            calendar.add(Calendar.DATE, 1);
        }
        return calendar;
    }
   protected  float dp_to_px(int dp){

       return dp * context.getResources().getDisplayMetrics().density;
   }



}//adapter end
