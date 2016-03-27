package com.xinqi.ihandwh.OrderSeats;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xinqi.ihandwh.Model.SeatInfo;
import com.xinqi.ihandwh.R;

/**
 * Created by presisco on 2015/11/16.
 */
public class SeatInfoAdapter extends RecyclerView.Adapter<SeatInfoAdapter.ViewHolder> {
    private static final String TAG = "SeatInfoAdapter";
    private Context parent;
    private SeatInfo[] mDataSet;
    private int mCardDefaultColor;
    private int mCardPickedColor;
// BEGIN_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView seatView;
        private static CardView lastPickedCard=null;
        private static int lastPickedPos=-1;
        private final int defaultColor;
        private final int pickedColor;

        public ViewHolder(View v,int _defaultcolor,int _pickedcolor) {
            super(v);
            // Define click listener for the ViewHolder's View.
            cardView = (CardView) v.findViewById(R.id.seatInfoCardView);
            seatView=(TextView)v.findViewById(R.id.seatIdTextView);
            defaultColor=_defaultcolor;
            pickedColor=_pickedcolor;

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lastPickedPos != -1)
                        lastPickedCard.setCardBackgroundColor(defaultColor);
                    //设置选中项颜色并记录
                    cardView.setCardBackgroundColor(pickedColor);
                    lastPickedPos = getAdapterPosition();
                    lastPickedCard = cardView;
                    Log.d(TAG, "Element " + lastPickedPos + " clicked.");
                }
            });
        }

        public CardView getCardView() {
            return cardView;
        }
        public TextView getTextView(){
            return seatView;
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public SeatInfoAdapter(SeatInfo[] dataSet,Context context) {
        parent=context;
        mDataSet = dataSet;
    }

    public void updateDataSet(SeatInfo[] dataSet)
    {   mDataSet=dataSet;}

    public void setPickColors(int _default,int _picked){
        mCardDefaultColor=_default;
        mCardPickedColor=_picked;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.seat_info_cardview, viewGroup, false);
        return new ViewHolder(v,mCardDefaultColor,mCardPickedColor);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element

        viewHolder.getTextView().setText(mDataSet[position].id);
        if(position==ViewHolder.lastPickedPos){
            viewHolder.getCardView().setCardBackgroundColor(mCardPickedColor);
        }else{
            viewHolder.getCardView().setCardBackgroundColor(mCardDefaultColor);
        }
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
    public SeatInfo getSelectedSeat() {
        if(ViewHolder.lastPickedPos==-1) {
            return null;
        }else{
            return mDataSet[ViewHolder.lastPickedPos];
        }
    }
    public void clearSelection(){
        ViewHolder.lastPickedPos=-1;
        ViewHolder.lastPickedCard=null;
    }
}
