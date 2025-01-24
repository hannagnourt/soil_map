package com.example.soil_map;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BasemapSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BasemapSelectionFragment extends BottomSheetDialogFragment {
    ArrayAdapter<String> adapter;
    ListView listView;
    ArrayList<String> basemaps;
    LayerViewModel layerViewModel;

    public BasemapSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment BasemapSelectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BasemapSelectionFragment newInstance(String param1, String param2) {
        BasemapSelectionFragment fragment = new BasemapSelectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_basemap_selection, container, false);

        listView = view.findViewById(R.id.basemap_listview);
        basemaps = new ArrayList<>();
        basemaps.add("Bản đồ đường phố");
        basemaps.add("Bản đồ vệ tinh");
        basemaps.add("Bản đồ địa hình");
        basemaps.add("Bản đồ vệ tinh và đường phố");

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, basemaps);
        listView.setAdapter(adapter);

        layerViewModel = new ViewModelProvider(requireActivity()).get(LayerViewModel.class);
        listView.setOnItemClickListener((parent, v, position, id) -> {
            layerViewModel.setBasemap(position+1);
        });
        return view;
    }
}