package com.george.autorunpro.activity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    static  PackageManager packageManager;
    static int backgroundRGB;

    public UserAppFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EventAdder.class);
                startActivity(intent);
            }
        });

        sqlOperator = new SqlOperator(getContext());
        Cursor c = sqlOperator.selectRecords();
        packageManager = getContext().getPackageManager();
        List<Pojo_fetch_data> datalist = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                //checkn mode =1 for kill events
                if(c.getInt(c.getColumnIndex("mode")) == 1){

                    datalist.get(datalist.size() - 1).stop_time = c.getString(c.getColumnIndex("time"));
                    continue;
                }

                datalist.add(new Pojo_fetch_data(

                        c.getInt(c.getColumnIndex("id")),        // datalist starts at index 0 //database id starts at 1
                        c.getString(c.getColumnIndex("appname")),     // title                      //0th item has id 1
                        c.getString(c.getColumnIndex("time")),
                        "na"
                ));
            } while (c.moveToNext());
            c.close();
            //using recycle view cutomizing card views
            recyclerView = (RecyclerView) inflater.inflate(
                    R.layout.recycler_view, container, false);
            adapter = new ContentAdapter(datalist);
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
        return recyclerView;
    }

    //view holder for cards data start
    public static class ViewHolder extends RecyclerView.ViewHolder {

        static TextView appname,start_time,stop_time;
        static ImageView img;
        static ImageButton deleteImageButton;
        static CardView cardview;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.fragment_home_card_list, parent, false));
            appname = (TextView) itemView.findViewById(R.id.appname);
            start_time = (TextView) itemView.findViewById(R.id.start_time);
            stop_time = (TextView) itemView.findViewById(R.id.stop_time);
            // img = (ImageView) itemView.findViewById(R.id.card_image);
            cardview =  (CardView) itemView.findViewById(R.id.card_view);
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
            ApplicationInfo applicationInfo = null;
            System.out.println("temp.id on onbind view holder ="+temp.id);
            System.out.println("In onbindViewholder card position = "+position);

            try {
                applicationInfo = packageManager.getApplicationInfo(temp.appname, 0);

            }
            catch (final PackageManager.NameNotFoundException e) { }

            final String title = (String) ((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");
            final Drawable icon = packageManager.getApplicationIcon(applicationInfo);
         /*   Palette.from(drawableToBitmap(icon)).generate(new Palette.PaletteAsyncListener() {
                public void onGenerated(Palette p) {
                   Palette.Swatch s = p.getDarkVibrantSwatch();
                   backgroundRGB = s.getRgb();
                }
            });*/
            Palette p = Palette.from(drawableToBitmap(icon)).generate();
            Palette.Swatch s = p.getLightVibrantSwatch();
            backgroundRGB = s.getRgb();
            holder.appname.setText(title);
            holder.start_time.setText(temp.start_time);
            holder.stop_time.setText(temp.stop_time);
            //holder.img.setImageDrawable(icon);
            //holder.img.setBackgroundColor(backgroundRGB);
            //holder.cardview.setCardBackgroundColor(backgroundRGB);
           // holder.cardview.setRadius(50);
            int height;
            holder.deleteImageButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                   // Snackbar.make(v, "Alarm deleted",Snackbar.LENGTH_LONG).show();
                    // System.out.println("In onbindViewholder temp.id  = "+temp.id);
                    if(temp.stop_time.equals("na")){
                        sqlOperator.delete(temp.id);
                        System.out.print("in na deleting id="+temp.id);
                     //   height = 150;
                    }
                    else {
                        System.out.print("deleting id=" + temp.id + " and id + 1=" + temp.id + 1);
                        sqlOperator.delete(temp.id);
                        sqlOperator.delete((temp.id + 1));
                      //  height = 200;
                    }

                    System.out.println("inside onclick = "+temp.id);
                    System.out.println("position = "+position);
                    System.out.println(datalist.get(position).appname);
                    removeData(position,datalist);

                }
            });
           // holder.cardview.setMinimumHeight(height);
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

    }




    @Override
    public void onDetach() {
        super.onDetach();
    }

   //method converting drawable to bitmap
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


}


