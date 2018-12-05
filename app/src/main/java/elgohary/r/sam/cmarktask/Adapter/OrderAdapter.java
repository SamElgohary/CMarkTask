package elgohary.r.sam.cmarktask.Adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import elgohary.r.sam.cmarktask.Activities.HomeActivity;
import elgohary.r.sam.cmarktask.Activities.OrderActivity;
import elgohary.r.sam.cmarktask.Model.Order;
import elgohary.r.sam.cmarktask.R;

public class OrderAdapter extends ArrayAdapter<Order> implements ListAdapter {

    //private final ColorMatrixColorFilter grayscaleFilter;
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<Order> mGridData = new ArrayList<Order>();

    public OrderAdapter(HomeActivity mContext, int layoutResourceId, ArrayList<Order> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }


    /**
     * Updates grid data and refresh grid items.
     *
     * @param mGridData
     */
    public void setGridData(ArrayList<Order> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.order_title);
            holder.describtionTextView = (TextView) row.findViewById(R.id.OrderDescribtion);
            holder.imageView = (ImageView) row.findViewById(R.id.order_img);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Order order = mGridData.get(position);
        holder.titleTextView.setText(order.getOrderTitle());
        holder.describtionTextView.setText(order.getOrderDescription());


        Picasso.get().load(order.getOrderImg()).into(holder.imageView);
        return row;
    }

    static class ViewHolder {
        TextView titleTextView;
        TextView describtionTextView;
        ImageView imageView;
    }
}