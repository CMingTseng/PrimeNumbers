package info.dvkr.primenumbers.view.MainActivity;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.dvkr.primenumbers.R;
import info.dvkr.primenumbers.domain.model.PrimeNumber;

public class MainActivityRecyclerViewAdapter extends
        RecyclerView.Adapter<MainActivityRecyclerViewAdapter.PrimeNumberViewHolder> {

    private final List<PrimeNumber> primeNumbers = new ArrayList<>();
    private final String adapterThreadText;
    private final String adapterPrimeNumberText;

    private final LayoutParams layoutParamsEnd = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    private final LayoutParams layoutParamsStart = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    private final Drawable roundStart;
    private final Drawable roundEnd;

    public MainActivityRecyclerViewAdapter(@NonNull final Context context,
                                           @NonNull final List<PrimeNumber> primeNumbers) {
        if (context == null)
            throw new IllegalArgumentException("Context list is cannot be null");
        if (primeNumbers == null)
            throw new IllegalArgumentException("PrimeNumber list is cannot be null");

        adapterThreadText = context.getString(R.string.recycleView_ThreadNumber);
        adapterPrimeNumberText = context.getString(R.string.recycleView_primeNumber);

        this.primeNumbers.clear();
        this.primeNumbers.addAll(primeNumbers);

        layoutParamsStart.gravity = Gravity.START;
        layoutParamsEnd.gravity = Gravity.END;

        roundStart = ContextCompat.getDrawable(context, R.drawable.text_round_start);
        roundEnd = ContextCompat.getDrawable(context, R.drawable.text_round_end);
    }

    @Override
    public PrimeNumberViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View rowView = inflater.inflate(R.layout.activity_main_recycle_row, parent, false);
        return new PrimeNumberViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final PrimeNumberViewHolder holder, final int position) {
        final PrimeNumber primeNumber = primeNumbers.get(position);

        if (primeNumber == null)
            throw new IllegalArgumentException("PrimeNumber list is cannot be null");

        holder.primeNumberThread.setText(String.format(adapterThreadText, primeNumber.getIntervalId()));
        holder.primeNumber.setText(String.format(adapterPrimeNumberText, primeNumber.getPrimeNumber()));

        if (primeNumber.getIntervalId() % 2 == 0) {
            holder.primeNumberThread.setLayoutParams(layoutParamsEnd);
            holder.primeNumberThread.setBackground(roundStart);
            holder.primeNumber.setLayoutParams(layoutParamsEnd);
        } else {
            holder.primeNumberThread.setLayoutParams(layoutParamsStart);
            holder.primeNumberThread.setBackground(roundEnd);
            holder.primeNumber.setLayoutParams(layoutParamsStart);
        }
    }

    @Override
    public int getItemCount() {
        if (primeNumbers == null) return 0;
        return primeNumbers.size();
    }

    // TODO Update to use DiffUtil
    public void updateData(@NonNull final List<PrimeNumber> primeNumbers) {
        if (primeNumbers == null)
            throw new IllegalArgumentException("PrimeNumber list is cannot be null");
        this.primeNumbers.clear();
        this.primeNumbers.addAll(primeNumbers);
        notifyDataSetChanged();
    }

    class PrimeNumberViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        TextView primeNumberThread;
        TextView primeNumber;

        PrimeNumberViewHolder(View view) {
            super(view);
            this.primeNumberThread = view.findViewById(R.id.textView_primeNumber_thread);
            this.primeNumber = view.findViewById(R.id.textView_primeNumber);
        }
    }
}
