package com.casasmap.blogandroid2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

//import android.widget.ShareActionProvider;

public class BlogPost extends AppCompatActivity {
    public String blogUriFinal;
    public Intent mIntent= new Intent(Intent.ACTION_SEND);;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_post);
        Intent intent = getIntent();
        Uri blogUri = intent.getData();
        blogUriFinal = blogUri.toString();
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(blogUri.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_blog_post, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        //Get the provider
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        //creating the intent
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        mIntent.setType("text/plain");
        mIntent.putExtra(Intent.EXTRA_TEXT, blogUriFinal);
        if(mShareActionProvider!=null){
            mShareActionProvider.setShareIntent(mIntent);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Toast.makeText(this, "carajoooo", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
