 package com.example.soil_map;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

 /**
 * A simple {@link Fragment} subclass.
 * Use the {@link LayerSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LayerSelectionFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

     RecyclerView recyclerViewLayers;
     LayerAdapter layerAdapter;
     List<LayerStructure> layers = new ArrayList<>();
     LayerViewModel layerViewModel;
     LayerNameMapper layerNameMapper = new LayerNameMapper();

     Button btnAddLayer;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LayerSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LayerSelectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LayerSelectionFragment newInstance() {
        LayerSelectionFragment fragment = new LayerSelectionFragment();
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
     public void onSaveInstanceState(@NonNull Bundle outState) {
         super.onSaveInstanceState(outState);
         outState.putSerializable("layers", new ArrayList<>(layers));
     }


     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layer_selection, container, false);
        setRetainInstance(true);

        recyclerViewLayers = view.findViewById(R.id.recyclerView_layers_1);

        layerNameMapper.initialize(requireContext());

        recyclerViewLayers.setLayoutManager(new LinearLayoutManager(requireContext()));

        layerViewModel = new ViewModelProvider(requireActivity()).get(LayerViewModel.class);
        if (layerViewModel.getLayers() == null || layerViewModel.getLayers().isEmpty())
        {
            layers = loadLayersFromJson(requireContext());
            layerViewModel.setLayers(layers);
        }
        else
            layers = layerViewModel.getLayers();

        if (layerAdapter == null)
        {
             layerAdapter = new LayerAdapter(layers, LayerStructure -> {
                 layerViewModel.setSelectedLayer(LayerStructure);
             }, layerNameMapper);
        }
        else
            layerAdapter.notifyDataSetChanged();

        recyclerViewLayers.setAdapter(layerAdapter);

        btnAddLayer = view.findViewById(R.id.btn_add_layer);
        btnAddLayer.setOnClickListener(v -> {
            // Handle add layer button click event
            layers.add(new LayerStructure());
            layerAdapter.notifyDataSetChanged();
        });

        return view;
    }

    public List<LayerStructure> loadLayersFromJson(Context context) {
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.layers);
            InputStreamReader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            Type layerListType = new TypeToken<List<LayerStructure>>() {}.getType();
            return gson.fromJson(jsonObject.get("layers"), layerListType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

     @Override
     public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
         super.onViewCreated(view, savedInstanceState);


     }
 }