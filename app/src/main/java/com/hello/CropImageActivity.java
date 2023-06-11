package com.hello;
import androidx.appcompat.app.*;
import android.os.*;
import android.content.*;
import com.theartofdev.edmodo.cropper.*;
import android.widget.*;
import android.net.*;

public class CropImageActivity extends AppCompatActivity
{
	Uri croppedUri;
	Uri cropUri;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		if (getIntent() != null && getIntent().getStringExtra("curl") != null)
		{
			cropUri = Uri.parse(getIntent().getStringExtra("curl"));
			CropImage.activity(cropUri)
				.start(this);}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
		{
			CropImage.ActivityResult result = CropImage.getActivityResult(data);
			if (resultCode == RESULT_OK)
			{
				croppedUri = result.getUri();
				Intent intent=new Intent();  
				intent.putExtra("cu", croppedUri.toString());  
				setResult(RESULT_OK, intent);
				//Toast.makeText(this, "5" + croppedUri.toString(), Toast.LENGTH_LONG).show();
				finish();
			}
			else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
			{
				Exception error = result.getError();
				Toast.makeText(this, "6" + error.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	}

}
