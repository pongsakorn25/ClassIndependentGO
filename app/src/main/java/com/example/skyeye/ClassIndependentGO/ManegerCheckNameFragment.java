package com.example.skyeye.ClassIndependentGO;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManegerCheckNameFragment extends Fragment {

    private ImageButton showmap;
    private Button showdatacheck;
    private String IDcourse;
    Bundle bundle;
    
    public ManegerCheckNameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_maneger_check_name, container, false);
        IDcourse = getArguments().getString("Id_Course");
        
        showmap = (ImageButton) rootView.findViewById(R.id.bshowmapT);

        showmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MapsTeacherActivity.class);
                intent.putExtra("Id_Course", IDcourse);
                getActivity().startActivity(intent);
                
            }
        });

        showdatacheck = (Button) rootView.findViewById(R.id.btnShowdatacheck);
        showdatacheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),DataStudentCheck.class);
                intent.putExtra("Id_Course", IDcourse);
                getActivity().startActivity(intent);
            }
        });

        return rootView;
    }

}
