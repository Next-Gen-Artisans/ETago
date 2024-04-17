package com.nextgenartisans.etago.profile;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nextgenartisans.etago.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsActivity extends AppCompatActivity {

    // Declare the views
    private ImageButton statsBackBtn;

    //Declare chart views
    PieChart pieChart;
    BarChart barChart, barChart2;
    RelativeLayout rl;

    //Declare Firebase variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    // Initialize a HashMap to store the count of each capturedClasses
    HashMap<String, Integer> capturedClassesCount = new HashMap<>();
    List<BarEntry> barEntries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_blue));
        }

        setContentView(R.layout.activity_stats);

        //Buttons
        statsBackBtn = findViewById(R.id.stats_back_btn);

        //Set chart views
        pieChart = findViewById(R.id.pie_chart);
        barChart = findViewById(R.id.bar_chart);
        barChart2 = findViewById(R.id.bar_chart_2);
        rl = findViewById(R.id.stats_container);

        // Query the Firestore database for documents in the "CensorshipInstances" collection
        // where the "userID" field matches the current user's ID
        db.collection("CensorshipInstances")
                .whereEqualTo("userID", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Loop through each document in the query results
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get the "capturedClasses" field from the document, which is a Map
                                Map<String, Boolean> capturedClasses = (Map<String, Boolean>) document.get("capturedClasses");
                                // Loop through each entry in the Map
                                for (String key : capturedClasses.keySet()) {
                                    // Increment the count for this class in the capturedClassesCount Map
                                    capturedClassesCount.put(key, capturedClassesCount.getOrDefault(key, 0) + 1);
                                }
                            }

                            // Find the PieChart view by its ID
                            pieChart = findViewById(R.id.pie_chart);

                            // Create a list to hold the PieEntry objects
                            ArrayList<PieEntry> entries = new ArrayList<>();
                            // Loop through each entry in the capturedClassesCount Map
                            for (Map.Entry<String, Integer> entry : capturedClassesCount.entrySet()) {
                                // Create a new PieEntry with the count as the value and the class as the label
                                entries.add(new PieEntry(entry.getValue(), entry.getKey()));
                            }
                            // Create a PieDataSet with the entries and a label
                            PieDataSet dataSet = new PieDataSet(entries,"");

                            // Create a list to hold the colors for the PieDataSet
                            ArrayList<Integer> colors = new ArrayList<>();

                            // Add colors to the list
                            colors.add(ContextCompat.getColor(getApplicationContext(), R.color.chart_orange));
                            colors.add(ContextCompat.getColor(getApplicationContext(), R.color.chart_lightblue));
                            colors.add(ContextCompat.getColor(getApplicationContext(), R.color.chart_gray));
                            colors.add(ContextCompat.getColor(getApplicationContext(), R.color.chart_lightgray));
                            colors.add(ContextCompat.getColor(getApplicationContext(), R.color.chart_blue));

                            // Set the colors for the PieDataSet
                            dataSet.setColors(colors);
                            dataSet.setValueFormatter(new IValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                    return String.valueOf((int) value);
                                }
                            });

                            // Create a PieData with the PieDataSet
                            PieData data = new PieData(dataSet);
                            data.setValueTextSize(10f);
                            data.setValueTextColor(ContextCompat.getColor(getApplicationContext(), R.color.alt_black));

                            // Set the PieData for the PieChart
                            pieChart.setData(data);
                            pieChart.getDescription().setEnabled(false);

                            // Set the Typeface for the entry labels
                            pieChart.setEntryLabelTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat_mediumitalic));
                            pieChart.setCenterTextTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat_bold));

                            // Set the color for the entry labels
                            pieChart.setEntryLabelColor(ContextCompat.getColor(getApplicationContext(), R.color.alt_black));
                            // Set the text size for the entry labels
                            pieChart.setEntryLabelTextSize(12f);
                            // Set the center text for the PieChart
                            pieChart.setCenterText("Distribution of Detected Personal Information");
                            // Set the text size for the center text
                            pieChart.setCenterTextSize(14f);


                            // Animate the Y axis of the PieChart
                            pieChart.animateY(1000);

                            // Add chart Legends
                            Legend legend = pieChart.getLegend();

                            // Set the text size of the legend labels
                            legend.setTextSize(10f);

                            // Set the color of the legend labels
                            legend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.alt_black));

                            // Set the typeface of the legend labels
                            legend.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat_regular));

                            // Set the form size of the legend entries
                            legend.setFormSize(10f);

                            // Enable word wrapping
                            legend.setWordWrapEnabled(true);
                            legend.setXEntrySpace(10f);
                            legend.setYEntrySpace(10f);


                            // Set the legend to draw outside the PieChart
                            legend.setDrawInside(false);



                            // Refresh the PieChart
                            pieChart.invalidate();
                        } else {
                            // Log an error if the query was not successful
                            Log.w("StatsActivity", "Error getting documents.", task.getException());
                        }
                    }
                });



        // Query the Firestore database for documents in the "Users" collection
        // where the "userID" field matches the current user's ID
        db.collection("Users")
                .document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get the "apiCallsLimit" and "numCensoredImgs" fields from the document
                                int apiCallsLimit = document.getLong("apiCallsLimit").intValue();
                                int numCensoredImgs = document.getLong("numCensoredImgs").intValue();

                                // Create a list to hold the BarEntry objects
                                ArrayList<BarEntry> entries = new ArrayList<>();
                                entries.add(new BarEntry(0, apiCallsLimit));
                                entries.add(new BarEntry(1, numCensoredImgs));

                                // Create a BarDataSet with the entries and a label
                                BarDataSet dataSet = new BarDataSet(entries, "API Calls Left vs. API Calls Made");
                                dataSet.setColors(new int[] {R.color.chart_blue, R.color.chart_orange}, getApplicationContext());
                                dataSet.setValueTextSize(10f);

                                // Create a BarData with the BarDataSet
                                BarData data = new BarData(dataSet);
                                data.setValueFormatter(new IValueFormatter() {
                                    @Override
                                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                        return String.valueOf((int) value);
                                    }
                                });
                                data.setValueTextSize(10f);
                                data.setValueTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat_bold));

                                // Set the BarData for the BarChart
                                barChart.setData(data);
                                barChart.getDescription().setEnabled(false);
                                barChart.setDrawBarShadow(false);
                                barChart.setDrawGridBackground(false);
                                barChart.setDrawBarShadow(false);
                                barChart.setGridBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                // Animate the Y axis of the PieChart
                                barChart.animateY(1000);


                                //Set BarChart legend
                                Legend legend = barChart.getLegend();
                                legend.setTextSize(10f);
                                legend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.alt_black));
                                legend.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat_bolditalic));
                                legend.setFormSize(10f);
                                legend.setWordWrapEnabled(true);
                                legend.setXEntrySpace(10f);
                                legend.setYEntrySpace(10f);
                                legend.setDrawInside(false);

                                // Refresh the BarChart
                                barChart.invalidate();
                            } else {
                                Log.d("BarChart", "No such document");
                            }
                        } else {
                            Log.d("BarChart", "get failed with ", task.getException());
                        }
                    }
                });

        // Query the Firestore database for documents in the "SaveAndShareInstance" collection
        // where the "userID" field matches the current user's ID
        db.collection("SaveAndShareInstances")
                .document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get the "numSaveInstance" and "numShareInstance" fields from the document
                                int numSaveInstance = document.getLong("numSaveInstance").intValue();
                                int numShareInstance = document.getLong("numShareInstance").intValue();

                                // Create a list to hold the BarEntry objects
                                ArrayList<BarEntry> entries = new ArrayList<>();
                                entries.add(new BarEntry(0, numSaveInstance));
                                entries.add(new BarEntry(1, numShareInstance));

                                // Create a BarDataSet with the entries and a label
                                BarDataSet dataSet = new BarDataSet(entries, "Save vs. Share Instances");
                                dataSet.setColors(new int[] {R.color.chart_lightblue, R.color.chart_lightgray}, getApplicationContext());

                                // Create a BarData with the BarDataSet
                                BarData data = new BarData(dataSet);
                                data.setValueFormatter(new IValueFormatter() {
                                    @Override
                                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                        return String.valueOf((int) value);
                                    }
                                });
                                data.setValueTextSize(10f);
                                data.setValueTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat_bold));
                                dataSet.setValueTextSize(10f);


                                // Set the BarData for the BarChart
                                barChart2.setData(data);
                                barChart2.getDescription().setEnabled(false);
                                barChart2.setDrawBarShadow(false);
                                barChart2.setDrawGridBackground(false);
                                barChart2.setDrawBarShadow(false);
                                barChart2.setGridBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                barChart2.animateY(1000);

                                //Set BarChart legend
                                Legend legend = barChart2.getLegend();
                                legend.setTextSize(10f);
                                legend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.alt_black));
                                legend.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.montserrat_bolditalic));
                                legend.setFormSize(10f);
                                legend.setWordWrapEnabled(true);
                                legend.setXEntrySpace(10f);
                                legend.setYEntrySpace(10f);
                                legend.setDrawInside(false);

                                // Refresh the new BarChart
                                barChart2.invalidate();

                            } else {
                                Log.d("NewBarChart", "No such document");
                            }
                        } else {
                            Log.d("NewBarChart", "get failed with ", task.getException());
                        }
                    }
                });




        //BACK BUTTON TO PROFILE ACTIVITY
        statsBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}