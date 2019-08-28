package com.example.dcris.myapplication.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.dcris.myapplication.PracticeActivity;
import com.example.dcris.myapplication.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PracticeFragment extends Fragment {

    private Button startQuizBtn;
    private Spinner difficultySpn;
    private Spinner categorySpn;
    private Spinner amountSpn;

    public PracticeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_practice, container, false);
        startQuizBtn = rootView.findViewById(R.id.fragpractice_btn_start);

        difficultySpn = rootView.findViewById(R.id.fragpractice_spn_difficulty);
        categorySpn = rootView.findViewById(R.id.fragpractice_spn_category);
        amountSpn = rootView.findViewById(R.id.fragpractice_spn_amount);

        difficultySpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals("Any"))
                {
                    String[] arraySpinner = new String[] {
                            "10","20","30", "40", "50"
                    };
                    Spinner s = rootView.findViewById(R.id.fragpractice_spn_amount);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arraySpinner);
                    s.setAdapter(adapter);
                }else{
                    String[] arraySpinner = new String[] {
                            "10","20","30"
                    };
                    Spinner s = rootView.findViewById(R.id.fragpractice_spn_amount);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arraySpinner);
                    s.setAdapter(adapter);
                }
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        startQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // String difficulty = difficultyET.getText().toString();
                String difficulty = difficultySpn.getSelectedItem().toString().toLowerCase();
                String category = categorySpn.getSelectedItem().toString();
                String amount = amountSpn.getSelectedItem().toString();

                int catID = 0;
                switch(category){
                    case "General Knowledge": catID=9; break;
                    case "Books": catID=10 ; break;
                    case "Movies": catID=11 ; break;
                    case "Video Games": catID=15; break;
                    case "Science&Nature": catID=17; break;
                    case "Computers": catID=18; break;
                    case "Geography": catID= 22; break;
                    case "History": catID= 23; break;
                    case "Any": catID=0; break;
                }

                Intent intent = new Intent(getActivity().getApplicationContext(), PracticeActivity.class);
                intent.putExtra("category", category);
                intent.putExtra("difficulty", difficulty);
                intent.putExtra("amount", amount);
                startActivity(intent);

                    //startQuizBtn.setBackgroundColor(Color.parseColor("#000000"));
                    //startActivity(new Intent(getActivity().getApplicationContext(), PracticeActivity.class));
            }
        });

        return rootView;
    }


}
