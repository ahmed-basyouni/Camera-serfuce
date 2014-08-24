

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


/**
 * 
 * @author {Ahmed-Basyouni (the Alpha)}
 *
 */
public class CameraMask extends Activity implements SurfaceHolder.Callback {

	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private boolean isRunning;
	final Context context = this;
	public static Camera camera = null;
	private RelativeLayout preview;
	private Bitmap capturedBitmap, finalBitmap;
	private ImageView capturedImage;
	Button captureButton;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		preview = (RelativeLayout) findViewById(R.id.basePreview);// ANY BASE
																	// LAYOUT OF
																	// YOUR XML

		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);// SURFACEVIEW
																	// FOR THE
																	// PREVIEW
		surfaceHolder = surfaceView.getHolder(); // needed for the preview to
													// control the surface size
													// and format
		surfaceHolder.addCallback(this); // needed to handle surface view methods
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) // This
																	// method is
																	// deprecated
																	// since API
																	// 11
																	// it's done
																	// automatically
																	// and no
																	// longer
																	// needed
																	// but for
																	// backward
																	// compatibility
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// NEEDED
																			// FOR
																			// THE
																			// PREVIEW
		capturedImage = (ImageView) findViewById(R.id.camera_image);// needed
																	// for
																	// overlapping
																	// process

		captureButton = (Button) findViewById(R.id.button1); // THE BUTTON FOR
																// TAKING
		// PICTURE

		captureButton.setOnClickListener(new OnClickListener() { // THE BUTTON
																	// CODE
					public void onClick(View v) {
						camera.takePicture(null, null, mPicture);// TAKING THE
																	// PICTURE
																	// THE
																	// PictureCallback
																	// is called
					}
				});
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,// Setup
																			// THE
																			// PREVIEW
			int height) {
		if (isRunning) {
			camera.stopPreview();
		}
		Camera.Parameters camParams = camera.getParameters();
		Camera.Size size = camParams.getSupportedPreviewSizes().get(0);
		camParams.setPreviewSize(size.width, size.height);
		camera.setParameters(camParams);
		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
			isRunning = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera = Camera.open();// open camera
			camera.setDisplayOrientation(90); // set Orientation to portrait
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Something went wrong",
					Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) { // on surface destroy
															// release camera
															// object and stop
															// preview
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	public void TakeScreenshot() { // THIS METHOD TAKES A SCREENSHOT AND SAVES
									// IT AS .jpg
		Random random = new Random();
		int num = random.nextInt(2000); // PRODUCING A RANDOM NUMBER FOR FILE
										// NAME
		preview.setDrawingCacheEnabled(true); // get cache from your view
		preview.buildDrawingCache(true);
		Bitmap bmp = Bitmap.createBitmap(preview.getDrawingCache());// set
																	// bitmap
																	// from all
																	// the layer
																	// above
																	// your view
		preview.setDrawingCacheEnabled(false); // clear drawing cache
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.JPEG, 100, bos);
		byte[] bitmapdata = bos.toByteArray();

		String picIdentifier = String.valueOf(num);
		String myfile = "tes" + picIdentifier + ".jpeg";

		// get SDCard directory
		File dir_image = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + "MYImage");
		// create directory MYImage
		dir_image.mkdirs();

		// I CHOOSE TO SAVE
		// THE FILE IN THE SD CARD IN THE FOLDER "MYImage"

		try {
			File tmpFile = new File(dir_image, myfile);
			FileOutputStream fos = new FileOutputStream(tmpFile);
			fos.write(bitmapdata);
			fos.close();
			Toast.makeText(getApplicationContext(), "saved at:SD/MYImage",
					Toast.LENGTH_SHORT).show();
			capturedBitmap = null;
			capturedImage.setRotation(90);
			capturedImage.setImageBitmap(capturedBitmap); // RESETING THE
															// PREVIEW

			camera.startPreview(); // RESETING THE PREVIEW
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private PictureCallback mPicture = new PictureCallback() { // THIS METHOD
																// AND THE
																// METHOD BELOW
		// CONVERT THE CAPTURED IMAGE IN A JPG FILE AND SAVE IT

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			// hide button so it doesn't appear in image
			captureButton.setVisibility(View.INVISIBLE);
			File dir_image2 = new File(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ File.separator + "MYImage");
			dir_image2.mkdirs(); // AGAIN CHOOSING FOLDER FOR THE PICTURE(WHICH
									// IS LIKE A SURFACEVIEW
									// SCREENSHOT)

			File tmpFile = new File(dir_image2, "tmp.jpg");
			// save image to dir_image2 AND NAMING IT "tmp.jpg" OR
			// what suit you

			try { // SAVING
				FileOutputStream fos = new FileOutputStream(tmpFile);
				fos.write(data);
				fos.close();
				// grabImage();
			} catch (FileNotFoundException e) {
				Toast.makeText(getApplicationContext(), "somthing went wrong",
						Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "somthing went wrong",
						Toast.LENGTH_SHORT).show();
			}

			// till now if u save pic right away it would be save but in wrong
			// orientation
			// so the next piece of code is a proposal to fix that issue
			// we get the original bitmap and make a new one with right
			// orientation

			String path = (Environment.getExternalStorageDirectory()
					+ File.separator + "MYImage" + File.separator + "tmp.jpg");// get
																				// saved
																				// tmp
			capturedBitmap = BitmapFactory.decodeFile(path);// decode it into
															// bitmap
			Matrix matrix = new Matrix();
			matrix.postRotate(90);// make a new matrix set it's orientation

			finalBitmap = Bitmap.createBitmap(capturedBitmap, 0, 0,
					capturedBitmap.getWidth(), capturedBitmap.getHeight(),
					matrix, true);

			capturedImage.setImageBitmap(finalBitmap); // SETTING THE BitMap AS
														// IMAGE
			// IN AN IMAGEVIEW(SOMETHING
			// LIKE A BACKGROUNG FOR THE LAYOUT)
			tmpFile.delete(); // delete tmp file after it done it's job

			TakeScreenshot();// CALLING THIS METHOD TO TAKE A SCREENSHOT

		}
	};
}
