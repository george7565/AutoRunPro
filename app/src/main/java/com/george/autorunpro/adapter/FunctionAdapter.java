package com.george.autorunpro.adapter;

/**
 * Created by Akshay on 13-Feb-16.
 */
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.george.autorunpro.R;

public class FunctionAdapter extends BaseAdapter {

    ArrayList<String> functionList;
    Activity context;


    public FunctionAdapter(Activity context, ArrayList<String> functions) {
        super();
        this.context = context;
        this.functionList = functions;
    }

    private class ViewHolder {
        TextView func;
    }

    public int getCount() {
        return functionList.size();
    }

    public Object getItem(int position) {
        return functionList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.apklist_item, null);
            holder = new ViewHolder();

            holder.func = (TextView) convertView.findViewById(R.id.appname);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //getting icons from position
        String uri = "func" + (position+1);
        int resID = context.getResources().getIdentifier(uri , "drawable", context.getPackageName());
        Drawable icon = ContextCompat.getDrawable(context,resID);
        String funcName = (String) getItem(position);
        icon.setBounds(0, 0, 80, 80);
        holder.func.setCompoundDrawables(icon, null, null, null);
        holder.func.setCompoundDrawablePadding(15);
        holder.func.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        holder.func.setText(funcName);
        return convertView;
    }
}
