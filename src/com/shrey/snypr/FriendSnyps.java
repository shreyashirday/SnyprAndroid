package com.shrey.snypr;

import java.util.List;

import com.example.snypr.MainActivity;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.shrey.pojos.Photo;
import com.shrey.util.MyAdapter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class FriendSnyps extends Activity {
	Adapter adapter;
	ListView listview;
	ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
	public static ParseObject ph;
	ParseFile file;
	public static ParseUser u;
	boolean go;
	Context ctx;
	ActionBar actionbar;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_snyps);
		View view = this.getWindow().getDecorView();
		
		ctx = this;
		adapter = new Adapter(this);
		go = false;
		u = FriendPage.getUserAgain();
		if(u == null){
			ctx.startActivity(new Intent(ctx,MainActivity.class));
			
		}
		actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setTitle(u.getUsername() + "'s Snyps");
		//actionbar.setBackgroundDrawable(new ColorDrawable(Color.BLUE));
		Log.d("username atsnyps",u.getUsername());
		listview = (ListView)findViewById(R.id.imageList);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position,
					long id) {
				// TODO Auto-generated method stub
				query.whereEqualTo("username", u.getUsername());
				query.findInBackground(new FindCallback<ParseObject>(){

					@Override
					public void done(List<ParseObject> objs, ParseException e) {
						// TODO Auto-generated method stub
						if(e == null){
							ph = objs.get(objs.size()-1-position);
							file = ph.getParseFile("photo");
							if(file != null){
							Log.d("File name",file.getName());
							go = true;
							}
							ctx.startActivity(new Intent(ctx,FriendImageCloseup.class));
						}
						
					}
					
				});
	}
		});
	}
	
	private class Adapter extends ParseQueryAdapter<Photo>
	{

		public Adapter(Context context
				) {
			super(context, new ParseQueryAdapter.QueryFactory<Photo>() {

				@Override
				public ParseQuery<Photo> create() {
					ParseQuery<Photo> query = ParseQuery.getQuery("Photo");
					// TODO Auto-generated method stub
					query.whereEqualTo("username", u.getUsername());
					query.addDescendingOrder("createdAt");
					return query;
				}
				
			});
			
			
			
		}
		@Override
		public View getItemView(Photo photo, View v, ViewGroup parent){
			if(v ==null){
				v = View.inflate(getContext(), R.layout.adapter_item, null);
			}
			
			super.getItemView(photo, v, parent);
			
			ParseImageView imageView = (ParseImageView)v.findViewById(R.id.snyp_image);
			ParseFile file = photo.getParseFile("photo");
			if(file!=null){
				imageView.setParseFile(file);
				imageView.loadInBackground(new GetDataCallback(){

					@Override
					public void done(byte[] arg0, ParseException arg1) {
						// TODO Auto-generated method stub
						if(arg1 == null){
						Log.d("success","file got");
						}
					}
					
				});
			}
			
			return v;
		}

		
		
	}
	
	public static ParseObject returnFriendPhoto(){
		return ph;
	}
	
	public static ParseUser getUserThirdTime(){
		return u;
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    getMenuInflater().inflate(R.menu.main, menu);
	     super.onCreateOptionsMenu(menu);
	     
	     return true;
	}

	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        
	        case R.id.action_gohome:
	        	ctx.startActivity(new Intent(ctx, MainActivity.class));
	            return true;
	       
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
	
	

