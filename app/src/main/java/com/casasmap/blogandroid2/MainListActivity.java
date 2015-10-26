package com.casasmap.blogandroid2;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainListActivity extends ListActivity {
    public String[] blogPostTitles;
    public static final String TAG = MainListActivity.class.getSimpleName();
    public static final int NUMBER_OF_POSTS = 20;
    public JSONObject mBlogData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        if(isNetworkAvailable()) {
            Toast.makeText(this, "You are now connected", Toast.LENGTH_SHORT).show();
            GetBlogPostsTask mGetBlogPostsTask = new GetBlogPostsTask();
            mGetBlogPostsTask.execute();

        }else
        {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();}

//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, OSSystems);
//        setListAdapter(arrayAdapter);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean networkInfoValue = false;
        if(networkInfo != null && networkInfo.isConnected())
        {networkInfoValue=true;}
        return networkInfoValue;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateList(){
        if(mBlogData == null){
            //TODO: something here
        }else
            try {
                JSONArray postArray = mBlogData.getJSONArray("posts");
                blogPostTitles = new String[postArray.length()];
                for(int i = 0; i<postArray.length(); i++)
                {
                    JSONObject abc = postArray.getJSONObject(i);
                    blogPostTitles[i] = String.valueOf(Html.fromHtml(abc.getString("title")));

                }
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, OSSystems);
//        setListAdapter(arrayAdapter);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, blogPostTitles);
                setListAdapter(arrayAdapter);
               // Log.d(TAG, mBlogData.toString(2));
            } catch (JSONException e) {
                Log.e(TAG, "Exception caught!" + e);
            }

    }
    private class GetBlogPostsTask extends AsyncTask<Object, Void, JSONObject>{
        private int responseCode = -1;

        @Override
        protected JSONObject doInBackground(Object... params) {

            JSONObject jsonObject = null;
            try {
                URL blogFeedUrl = new URL("http://blog.teamtreehouse.com/api/get_recent_summary/?count=" + NUMBER_OF_POSTS);
                HttpURLConnection connection = (HttpURLConnection) blogFeedUrl.openConnection();
                connection.connect();
                //let's see what response we get
                responseCode = connection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    InputStream inputStream = connection.getInputStream();
                    Reader reader = new InputStreamReader(inputStream);
                    int contentLenght = connection.getContentLength();
                    char[] charArray = new char[contentLenght];
                    reader.read(charArray);
                    String responseData = new String(charArray);
                    //Log.v(TAG, responseData);
                    //usign jsonobject
                    jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("status");
                    //dividing jsonObject
//                    JSONArray jsonArray = jsonObject.getJSONArray("posts");
//                    for(int i=1; i<=jsonArray.length(); i++ ){
//                        JSONObject jsonPost = jsonArray.getJSONObject(i);
//                        String title = jsonPost.getString("title");
//                    }
                }
                else{
                    Log.i(TAG, "Code:" + responseCode);
                }
            } catch (MalformedURLException e) {
                //e.printStackTrace();
                Log.e(TAG, "Exception caught: ", e);
            } catch (IOException e) {
                Log.e(TAG, "Exception caught", e);
            }catch (Exception e){
                Log.e(TAG, "Exception caught", e);
            }

            return jsonObject;
        }
        protected void onPostExecute(JSONObject result){
            mBlogData = result;
            updateList();

        }
    }
}
