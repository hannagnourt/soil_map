package com.example.soil_map;

import static com.example.soil_map.LayerNameMapper.getFullName;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LayerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<LayerStructure> layers;
    private OnLayerClickListener listener;
    private LayerNameMapper layerNameMapper = new LayerNameMapper();

    private static final int PARENT_VIEW_TYPE = 0;
    private static final int CHILD_VIEW_TYPE = 1;

    public interface OnLayerClickListener {
        void onLayerClick(LayerStructure LayerStructure);
    }

    public LayerAdapter(List<LayerStructure> layers, OnLayerClickListener listener, LayerNameMapper layerNameMapper) {
        this.layers = layers;
        this.listener = listener;
        this.layerNameMapper = layerNameMapper;
    }

    public int getItemViewType(int position) {
        int cnt = 0;

        for (LayerStructure layer : layers) {
            if (position == cnt)
                return PARENT_VIEW_TYPE;

            cnt++;

            if (layer.getExpanded()) {
                int childCnt = layer.getLayerStructure().size();

                if (position < cnt + childCnt)
                    return CHILD_VIEW_TYPE;

                cnt += childCnt;
            }
        }

        return PARENT_VIEW_TYPE;
    }

    public void setLayers(List<LayerStructure> newLayers) {
        layers.clear();
        layers.addAll(newLayers); // Update the data source
        notifyDataSetChanged();  // Notify the adapter of the change
    }

    @NonNull
    @Override
    // Create a new ViewHolder for each item in the RecyclerView
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        if (viewType == PARENT_VIEW_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layer, parent, false);
            return new ParentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layer, parent, false);
            return new ChildViewHolder(view);
        }
    }

    @Override
    // Bind the data for each item in the RecyclerView
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int count = 0;
        for (LayerStructure layer : layers) {
            if (position == count) {
                // Bind parent view
                ((ParentViewHolder) holder).bind(layer);
                return;
            }

            count++;

            if (layer.getExpanded()) {
                List<Value> childItems = layer.getLayerStructure();
                int childCount = childItems.size();
                if (position < count + childCount) {
                    // Bind child view
                    Value value = childItems.get(position - count);
                    ((ChildViewHolder) holder).bind(value);
                    ((ChildViewHolder) holder).layerVisibilitySwitch.setOnCheckedChangeListener(null);
                    ((ChildViewHolder) holder).layerVisibilitySwitch.setChecked(value.getVisibility());
                    int finalCount = count;
                    ((ChildViewHolder) holder).layerVisibilitySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        // Handle LayerStructure visibility switch change event
                        buttonView.setEnabled(false);
                        Handler handler = new Handler(Looper.getMainLooper());
                        if (isChecked) {
                            int index = -1;
                            boolean found = false;
                            for (LayerStructure l: layers){
                                index++;

                                if (l.getExpanded())
                                    for (Value v: l.getLayerStructure())
                                    {
                                        index++;

                                        if (v.getVisibility())
                                        {
                                            v.setVisibility(false);
                                            int finalIndex = index;
                                            handler.post(() -> notifyItemChanged(finalIndex));
                                            found = true;
                                            break;
                                        }
                                    }

                                if (found)
                                    break;
                            }

                            value.setVisibility(true);
                        } else {
                            value.setVisibility(false);
                        }

                        handler.postDelayed(() -> buttonView.setEnabled(true), 100);
                    });

                    return;
                }

                count += childCount;
            }
        }
    }

    @Override
    public int getItemCount() {
        int cnt = 0;
        for (LayerStructure layer : layers) {
            cnt++;

            if (layer.getExpanded())
                cnt += layer.getLayerStructure().size();
        }

        return cnt;
    }

    class ParentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewLayerName;
        ImageView imageViewExpand;
        ImageButton layerCustomizeButton;
        Switch layerVisibilitySwitch;

        ParentViewHolder(View itemView) {
            super(itemView);
            textViewLayerName = itemView.findViewById(R.id.textView_layer_name);
            imageViewExpand = itemView.findViewById(R.id.imageView_expand);
            layerCustomizeButton = itemView.findViewById(R.id.layerCustomizeButton);
            layerVisibilitySwitch = itemView.findViewById(R.id.layerVisibilitySwitch);
        }

        void bind(LayerStructure layerStructure) {
            String text = getFullName(layerStructure.getProperty());
            textViewLayerName.setText(text);

            imageViewExpand.setImageResource(
                    layerStructure.getExpanded() ? R.drawable.down_arrow : R.drawable.right_arrow
            );

            imageViewExpand.setOnClickListener(v -> {
                imageViewExpand.setEnabled(false);

                boolean temp = !layerStructure.getExpanded();

                imageViewExpand.setImageResource(
                        temp ? R.drawable.down_arrow : R.drawable.right_arrow
                );

                layerStructure.setExpanded(temp);

                int childCount = layerStructure.getLayerStructure().size();

                if (temp)
                    new Handler(Looper.getMainLooper()).post(() ->
                            notifyItemRangeInserted(getAdapterPosition() + 1, childCount));
                else
                    new Handler(Looper.getMainLooper()).post(() ->
                            notifyItemRangeRemoved(getAdapterPosition() + 1, childCount));

                new Handler(Looper.getMainLooper()).postDelayed(() -> imageViewExpand.setEnabled(true), 100);
            });

            layerVisibilitySwitch.setVisibility(View.GONE);
        }
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView textViewLayerName;
        Switch layerVisibilitySwitch;
        ImageButton layerCustomizeButton;

        ChildViewHolder(View itemView) {
            super(itemView);
            textViewLayerName = itemView.findViewById(R.id.textView_layer_name);
            layerVisibilitySwitch = itemView.findViewById(R.id.layerVisibilitySwitch);
            layerCustomizeButton = itemView.findViewById(R.id.layerCustomizeButton);
        }

        void bind(Value value) {
            String text = value.getRange();
            textViewLayerName.setText(text);
        }
    }
}