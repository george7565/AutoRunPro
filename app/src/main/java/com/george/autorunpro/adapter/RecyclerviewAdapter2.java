package com.george.autorunpro.adapter;

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
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.george.autorunpro.AlarmSet;
import com.george.autorunpro.EventAdder;
import com.george.autorunpro.FunctionAdder;
import com.george.autorunpro.Pojo_fetch_data;
import com.george.autorunpro.R;
import com.george.autorunpro.SqlOperator;
import com.george.autorunpro.SqlOperator2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Akshay on 15-Mar-16.
 */
public class RecyclerviewAdapter2 extends RecyclerView.Adapter <RecyclerviewAdapter2.myViewHolder>{


    private LayoutInflater inflator;
    List<Pojo_fetch_data> datalist = Collections.emptyList();
    private int lastPosition = -1;
    private RecyclerView recyclerView;

    public RecyclerviewAdapter2(Context context,List<Pojo_fetch_data> datalist,RecyclerView recyclerView){
        inflator = LayoutInflater.from(context);
        this.datalist = datalist;
        this.recyclerView = recyclerView;

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
        holder.appname.setText(current_data.appname);
        holder.stop_time.setVisibility(View.VISIBLE);
        holder.stop.setVisibility(View.VISIBLE);
        holder.stop_padding.setVisibility(View.VISIBLE);
        holder.start_time.setText(time_in_12hr(current_data.start_time));
        if (! current_data.stop_time.equals("na") )
            holder.stop_time.setText(time_in_12hr(current_data.stop_time));
        else{

            holder.stop_time.setVisibility(View.GONE);
            holder.stop.setVisibility(View.GONE);
            holder.stop_padding.setVisibility(View.GONE);

            float height_pix = 160 * Resources.getSystem().getDisplayMetrics().density;
            holder.cardview.getLayoutParams().height = (int)height_pix;
        }


        if(current_data.status == 0)
            holder.swt.setChecked(false);
        else
            holder.swt.setChecked(true);
        holder.deleteImageButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                // Snackbar.make(v, "Alarm deleted",Snackbar.LENGTH_LONG).show();
                // System.out.println("In onbindViewholder temp.id  = "+temp.id);
                SqlOperator2 sqlOperator =new SqlOperator2(v.getContext());
                AlarmSet am = new AlarmSet();
                if(current_data.stop_time.equals("na")){

                    am.CancelAlarm(v.getContext(),current_data.id);
                    sqlOperator.delete(current_data.id);
                    System.out.print("in na deleting id="+current_data.id);
                    //   height = 150;
                }
                else {
                    System.out.print("deleting id=" +current_data.id + " and id + 1=" + current_data.id + 1);
                    am.CancelAlarm(v.getContext(),current_data.id);
                    sqlOperator.delete(current_data.id);
                    am.CancelAlarm(v.getContext(),current_data.id + 1);
                    sqlOperator.delete((current_data.id + 1));
                    //  height = 200;
                }
                System.out.println("inside onclick = "+current_data.id);
                //System.out.println("position = "+position);
                //System.out.println(datalist.get(position).appname);
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

                                SqlOperator2 sqlOperator = new SqlOperator2(buttonView.getContext());
                                ContentValues cv = new ContentValues();
                                cv.put("status",0);
                                AlarmSet am = new AlarmSet();

                                sqlOperator.updateRecord(current_data.id,cv);
                                am.CancelAlarm(buttonView.getContext(),current_data.id);
                                if(!current_data.stop_time.equals("na")){

                                    sqlOperator.updateRecord(current_data.id + 1,cv);
                                    am.CancelAlarm(buttonView.getContext(),current_data.id + 1);

                                }
                                datalist.get(position).status = 0;current_data.status = 0;
                                Snackbar.make(buttonView, current_data.appname +"timer switched Off",
                                        Snackbar.LENGTH_LONG).show();
                            }


                        } else {
                            Log.i("TAG","checked");
                            if(current_data.status == 0){
                                SqlOperator2 sqlOperator = new SqlOperator2(buttonView.getContext());
                                ContentValues cv = new ContentValues();
                                cv.put("status",1);
                                sqlOperator.updateRecord(current_data.id,cv);
                                String query = "select * from AppAlarms where " +
                                        "(monday = 1 OR tuesday = 1 OR wednesday = 1 OR " +
                                        "thursday = 1 OR friday = 1 OR saturday = 1 OR sunday = 1) " +
                                        "AND id ="+current_data.id;
                                Cursor c = sqlOperator.selectRecord(query);
                                // setting app start time calender

                                //calender setup end
                                Calendar calendar = getCalender(current_data.start_time);
                                AlarmSet am = new AlarmSet();
                                if (c != null)
                                    am.SetRepeatAlarm(buttonView.getContext(),calendar,current_data.id);
                                else
                                    am.setOnetimeTimer(buttonView.getContext(),calendar,current_data.id);
                                am.CancelAlarm(buttonView.getContext(),current_data.id);
                                if(!current_data.stop_time.equals("na")){

                                    sqlOperator.updateRecord(current_data.id + 1,cv);
                                    calendar = getCalender(current_data.stop_time);
                                    if(c != null)
                                        am.SetRepeatAlarm(buttonView.getContext(),calendar,current_data.id + 1);
                                    else
                                        am.setOnetimeTimer(buttonView.getContext(),calendar,current_data.id + 1);
                                }
                                datalist.get(position).status = 1;current_data.status = 1;
                                Snackbar.make(buttonView, current_data.appname+" timer switched On",
                                        Snackbar.LENGTH_LONG).show();
                            }

                        }
                        break;

                    default:
                        break;
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, FunctionAdder.class);
                context.startActivity(intent);
            }
        });

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

    class myViewHolder extends RecyclerView.ViewHolder{

        TextView appname,start_time,stop_time,stop,stop_padding;
        ImageView img;
        ImageButton deleteImageButton;
        CardView cardview;
        SwitchCompat swt;
        public myViewHolder(View itemView) {
            super(itemView);

            appname = (TextView) itemView.findViewById(R.id.appname);
            start_time = (TextView) itemView.findViewById(R.id.start_time);
            stop = (TextView) itemView.findViewById(R.id.stop);
            stop_padding = (TextView) itemView.findViewById(R.id.padding1);
            stop_time = (TextView) itemView.findViewById(R.id.stop_time);
            // img = (ImageView) itemView.findViewById(R.id.card_image);
            cardview =  (CardView) itemView.findViewById(R.id.card_view);
            swt =(SwitchCompat) itemView.findViewById(R.id.onoffbtn);


            deleteImageButton =
                    (ImageButton) itemView.findViewById(R.id.delete_button);

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
}//adapter end
