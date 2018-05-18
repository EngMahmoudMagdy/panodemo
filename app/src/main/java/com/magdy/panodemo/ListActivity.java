package com.magdy.panodemo;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ListActivity extends AppCompatActivity implements GridInfoListener{
    GridRecyclerAdapter gridAdapter;
    RecyclerView gridView;
    List<Place> places  ;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog dialog ;
    DBController controller ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        places = new ArrayList<>();
        gridView = findViewById(R.id.recyclerView);
        gridAdapter = new GridRecyclerAdapter(this, places, this);
        controller = new DBController(this);
        RecyclerView.LayoutManager layoutManager;
        if (getResources().getConfiguration().orientation == 2) {
            layoutManager = new StaggeredGridLayoutManager(3, 1);
        } else {
            layoutManager = new StaggeredGridLayoutManager(2, 1);
        }
        gridView.setHasFixedSize(false);
        gridView.setLayoutManager(layoutManager);
        gridView.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();
        swipeRefreshLayout =  findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadData();

            }
        });
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Downloading Loading...");
        downloadData();
    }
    public void downloadData()
    {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        swipeRefreshLayout.setRefreshing(true);
            dialog.show();
            //requestParams.put("placesJSON",controller.composeJSONfromSQLite());
            asyncHttpClient.get("http://192.168.1.140:2080/travelli/view_places.php", requestParams, new TextHttpResponseHandler() {

                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                    Log.v("data",responseBody);
                    dialog.hide();
                    swipeRefreshLayout.setRefreshing(false);
                    try {
                        JSONObject o = new JSONObject(responseBody);
                        JSONArray arr = o.getJSONArray("places");
                        places.clear();
                            for(int i=0; i<arr.length();i++){
                                JSONObject obj = (JSONObject)arr.get(i);
                                Place p = new Place();
                                p.setId(obj.getString("id"));
                                p.setName(obj.getString("name"));
                                p.setLongt(obj.getDouble("longt"));
                                p.setLat(obj.getDouble("lat"));
                                p.setImageLink(obj.getString("image"));
                                p.setDescription(obj.getString("description"));
                                places.add(p);
                            }
                        gridAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "DB download completed!", Toast.LENGTH_LONG).show();
                        gridAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    dialog.hide();
                    if(statusCode == 404){
                        Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    }else if(statusCode == 500){
                        Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }
    public void syncSQLiteMySQLDB()
    {

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        List<Place> newplaces = controller.getAllPlaces();
        if(newplaces.size()!=0)
        {
            if(controller.dbSyncCount()!=0)
            {
                dialog.show();
                requestParams.put("placesJSON",controller.composeJSONfromSQLite());
                asyncHttpClient.post("http://localhost:2080/travelli/insert_places.php", requestParams, new AsyncHttpResponseHandler() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        System.out.println(responseBody);
                        dialog.hide();
                        try {
                            JSONArray arr = new JSONArray(responseBody);
                            System.out.println(arr.length());
                            for(int i=0; i<arr.length();i++){
                                JSONObject obj = (JSONObject)arr.get(i);
                                System.out.println(obj.get("id"));
                                System.out.println(obj.get("status"));
                                controller.updateSyncStatus(obj.get("id").toString(),obj.get("status").toString());
                            }
                            Toast.makeText(getApplicationContext(), "DB Sync completed!", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        dialog.hide();
                        if(statusCode == 404){
                            Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                        }else if(statusCode == 500){
                            Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else{
                Toast.makeText(getApplicationContext(), "SQLite and Remote MySQL DBs are in Sync!", Toast.LENGTH_LONG).show();
            }
            }else{
            Toast.makeText(getApplicationContext(), "No data in SQLite DB, please do enter User name to perform Sync action", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setSelected(Place place) {
        Toast.makeText(getApplicationContext(), place.getDescription(), Toast.LENGTH_LONG).show();
    }
}
