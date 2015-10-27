package com.casasmap.blogandroid2;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;

public class MainListActivity extends ListActivity {
    //public String[] mBlogPostTitlesFromJSON;
    public static final String TAG = MainListActivity.class.getSimpleName();
    public static final int NUMBER_OF_POSTS = 20;
    public JSONObject mBlogData;
    protected ProgressBar mProgressBar;
    public final String KEY_TITLE = "title";
    public final String KEY_AUTHOR = "author";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        if(isNetworkAvailable()) {
            //Toast.makeText(this, "You are now connected", Toast.LENGTH_SHORT).show();

            //starting the progressbar
            mProgressBar.setVisibility(View.VISIBLE);

            GetBlogPostsTask mGetBlogPostsTask = new GetBlogPostsTask();
            mGetBlogPostsTask.execute();

        }else
        {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();}

//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, OSSystems);
//        setListAdapter(arrayAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        try{
            JSONArray jsonPosts = mBlogData.getJSONArray("posts");
            JSONObject jsonPost = jsonPosts.getJSONObject(position);
            String blogUrl = jsonPost.getString("url");
            // starting the intent
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(blogUrl));
            startActivity(intent);


        }catch (Exception e){
            Log.d(TAG, "Exception caught!", e);

        }
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

    public void handleJsonResult(){
        mProgressBar.setVisibility(View.INVISIBLE);
        if(mBlogData == null){

            showErrorDialog();
        }else
            try {
                JSONArray postArray = mBlogData.getJSONArray("posts");
                ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
                for(int i = 0; i<postArray.length(); i++)
                {
                    JSONObject abc = postArray.getJSONObject(i);
                    String title = abc.getString(KEY_TITLE);
                    title = String.valueOf(Html.fromHtml(title));
                    String author = abc.getString(KEY_AUTHOR);
                    author = String.valueOf(Html.fromHtml(author));

                    HashMap<String, String> hashPost = new HashMap<String, String>();
                    hashPost.put(KEY_TITLE, title);
                    hashPost.put(KEY_AUTHOR, author);

                    arrayList.add(hashPost);


                   // mBlogPostTitlesFromJSON[i] = String.valueOf(Html.fromHtml(abc.getString("title")));

                }
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, OSSystems);
//        setListAdapter(arrayAdapter);
                //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mBlogPostTitlesFromJSON);
                String[] keys = {KEY_TITLE, KEY_AUTHOR};
                int[] ids = {android.R.id.text1, android.R.id.text2};

                SimpleAdapter adapter = new SimpleAdapter(this, arrayList, android.R.layout.simple_list_item_2, keys, ids );

                setListAdapter(adapter);

               // Log.d(TAG, mBlogData.toString(2));
            } catch (JSONException e) {
                Log.e(TAG, "Exception caught!" + e);
            }

    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title);
        builder.setMessage(R.string.message);
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
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
            handleJsonResult();

        }
    }
}
