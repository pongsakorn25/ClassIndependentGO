package com.example.skyeye.ClassIndependentGO;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


public class CheckNameFragment extends Fragment {

    private ImageButton showmap;
    private String IDcourse, IDStudent;
    private Button showdatacheck;
    Bundle bundle;

    public CheckNameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_check_name, container, false);
        IDcourse = getArguments().getString("Id_Course");
        IDStudent = getArguments().getString("Id_Student");
        showmap = (ImageButton) rootView.findViewById(R.id.bshowmapS);

        showmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MapsStudentActivity.class);
                intent.putExtra("Id_Course", IDcourse);
                intent.putExtra("Id_Student", IDStudent);
                getActivity().startActivity(intent);

            }
        });

        showdatacheck = (Button) rootView.findViewById(R.id.btnShowdatacheckS);
        showdatacheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),DataCheckName.class);
                intent.putExtra("Id_Course", IDcourse);
                intent.putExtra("Id_Student", IDStudent);
                getActivity().startActivity(intent);
            }
        });
        return rootView;
    }


}
