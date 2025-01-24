package com.example.soil_map;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LegendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LegendFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    String currentLayer = "";
    ImageView legendImage;
    String unit = "";
    TextView legendText;
    LayerViewModel layerViewModel;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LegendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LegendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LegendFragment newInstance() {
        LegendFragment fragment = new LegendFragment();
        Bundle args = new Bundle();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_legend, container, false);

        legendImage = view.findViewById(R.id.legendImage);
//        legendImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        legendText = view.findViewById(R.id.legendText);

        layerViewModel = new ViewModelProvider(requireActivity()).get(LayerViewModel.class);
        layerViewModel.getSelectedLayer().observe(this, layerStructure -> {
            if (layerStructure != null)
            {
                currentLayer = layerStructure.getProperty();
                String imageURL = "https://geoserver.geoscience.vn:8443/geoserver/soil/wms?service=WMS&version=1.1.0&request=GetLegendGraphic" +
                        "&layer=soil:" + currentLayer +
                        "&format=image/png";
                Glide.with(this)
                        .load(imageURL)
                        .into(legendImage);

                String text = layerStructure.getName() + "\n" +
                        "Unit: " + layerStructure.getUnit();
                legendText.setText(text);
                legendImage.setVisibility(View.VISIBLE);
                legendText.setVisibility(View.VISIBLE);
            }
            else
            {
                legendImage.setVisibility(View.GONE);
                legendText.setVisibility(View.GONE);
            }
        });

        return view;
    }
}