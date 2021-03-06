package com.shrey.snypr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.snypr.MainActivity;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shrey.pojos.Photo;
import com.shrey.pojos.SnypPoint;
import com.shrey.pojos.User;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.SurfaceHolder.Callback;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class CameraFragmentTwo extends Fragment {
	public static final String TAG = "CameraFragment";
	private Camera camera;
	private SurfaceView surfaceView;
	private ParseFile photoFile;
	private ImageButton photoButton;
	Register r;
	User user = new User();
	Context ctx;
	MainActivity ma;
	Photo photo;
	List<ParseFile> phos = new ArrayList<ParseFile>();
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_camera, parent, false);
		ctx = this.getActivity();
		photoButton = (ImageButton) v.findViewById(R.id.camera_photo_button);
		

		photo = new Photo();
		if (camera == null) {
			try {
				camera = Camera.open();
				photoButton.setEnabled(true);
			} catch (Exception e) {
				Log.e(TAG, "No camera with exception: " + e.getMessage());
				photoButton.setEnabled(false);
				Toast.makeText(getActivity(), "No camera detected",
						Toast.LENGTH_LONG).show();
			}
		}

		photoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (camera == null)
					return;
				camera.takePicture(new Camera.ShutterCallback() {

					@Override
					public void onShutter() {
						// nothing to do
					}

				}, null, new Camera.PictureCallback() {

					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						saveScaledPhoto(data);
						//ParseUser.getCurrentUser().increment("score",30);
						Toast.makeText(getActivity(), "Snypd!", Toast.LENGTH_SHORT).show();
						
			}
		});
			};
		});

		surfaceView = (SurfaceView) v.findViewById(R.id.camera_surface_view);
		SurfaceHolder holder = surfaceView.getHolder();
		holder.addCallback(new Callback() {

			public void surfaceCreated(SurfaceHolder holder) {
				try {
					if (camera != null) {
						camera.setDisplayOrientation(90);
						camera.setPreviewDisplay(holder);
						camera.startPreview();
					}
				} catch (IOException e) {
					Log.e(TAG, "Error setting up preview", e);
				}
			}
		
			
			

			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// nothing to do here
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				// nothing here
			}
		
			
		});
		return v;
	}
	


		
			

		
	

	

	/*
	 * ParseQueryAdapter loads ParseFiles into a ParseImageView at whatever size
	 * they are saved. Since we never need a full-size image in our app, we'll
	 * save a scaled one right away.
	 */
	private void saveScaledPhoto(byte[] data) {

		// Resize photo from camera byte array
		Bitmap snypImage = BitmapFactory.decodeByteArray(data, 0, data.length);
		Bitmap snypImageScaled = Bitmap.createScaledBitmap(snypImage, 2000, 2000
				* snypImage.getHeight() / snypImage.getWidth(), false);

		// Override Android default landscape orientation and save portrait
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		Bitmap rotatedScaledMealImage = Bitmap.createBitmap(snypImageScaled, 0,
				0, snypImageScaled.getWidth(), snypImageScaled.getHeight(),
				matrix, true);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		rotatedScaledMealImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

		byte[] scaledData = bos.toByteArray();

		// Save the scaled image to Parse
		photoFile = new ParseFile("snyp.jpg", scaledData);
		final ParseFile oldFile = ParseUser.getCurrentUser().getParseFile("ProfilePicture");
		photoFile.saveInBackground(new SaveCallback() {

			public void done(ParseException e) {
				if (e == null) {
					photo.addFile(photoFile);
					photo.addUser(ParseUser.getCurrentUser().getUsername());
					photo.put("isProfilePicture", true);
					photo.saveEventually();
					
					
					
					Log.d("save status",photoFile.getName() + " is saved!");
				} else {
					
					Toast.makeText(getActivity(),
							"Error saving: " + e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}
		

	/*
	 * Once the photo has saved successfully, we're ready to return to the
	 * NewMealFragment. When we added the CameraFragment to the back stack, we
	 * named it "NewMealFragment". Now we'll pop fragments off the back stack
	 * until we reach that Fragment.
	 */
	/**private void addPhotoToMealAndReturn(ParseFile photoFile) {
		((NewMealActivity) getActivity()).getCurrentMeal().setPhotoFile(
				photoFile);
		FragmentManager fm = getActivity().getFragmentManager();
		fm.popBackStack("NewMealFragment",
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
	*/

	
	public void onStart(){
		super.onStart();
		 
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (camera == null) {
			try {
				camera = Camera.open();
				photoButton.setEnabled(true);
			} catch (Exception e) {
				Log.i(TAG, "No camera: " + e.getMessage());
				photoButton.setEnabled(false);
				Toast.makeText(getActivity(), "No camera detected",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onPause() {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}
		super.onPause();
	}
	}
	

	
