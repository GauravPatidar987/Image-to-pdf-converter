package com.hello;

import android.app.*;
import android.os.*;
import com.theartofdev.edmodo.cropper.*;
import android.net.Uri;
import java.util.List;
import java.util.ArrayList;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.content.ClipData;
import android.widget.Toast;
import java.io.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import com.itextpdf.text.*;

public class MainActivity extends Activity
{
	Uri croppedUri=null;
	Uri cropUriTo=null;
	private int REQ_PER=12;
	private int REQ_MULTI=1;
	private List<byte[]> uriList=new ArrayList<>();

	private int REQ_CROP=32;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		convertPdf();
		if (checkPermit())
		{
			selectMultipleImg();
			convertPdf();
		}
		else
		{
			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_PER);
		}

    }
	public void convertPdf()
	{
		Toast.makeText(this,"code",Toast.LENGTH_LONG).show();
		if (uriList.size() > 0)
		{
			try
			{
				Document document = new Document();

				String directoryPath = android.os.Environment.getExternalStorageDirectory().toString();

				PdfWriter.getInstance(document, new FileOutputStream(directoryPath + "/example.pdf"));
				try
				{
					document.open();
					for (int k=0;k < uriList.size();k++)
					{

						Image image = Image.getInstance(uriList.get(k));
						float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
							- document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
						//Toast.makeText(this, "" + scaler, Toast.LENGTH_LONG).show();
						image.scalePercent(scaler);

						image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP); 

						document.add(image);
					}
					document.close();
					openPdf();
				}
				catch (BadElementException e)
				{
					Toast.makeText(this, "1" + e.getMessage(), Toast.LENGTH_LONG).show();
				}
				catch (IOException e)
				{
					Toast.makeText(this, "2" + e.getMessage(), Toast.LENGTH_LONG).show();
				}  
			}
			catch (DocumentException e)
			{
				Toast.makeText(this, "3" + e.getMessage(), Toast.LENGTH_LONG).show();
			}
			catch (FileNotFoundException e)
			{
				Toast.makeText(this, "4" + e.getMessage(), Toast.LENGTH_LONG).show();
			} 
		}
		else
		{
			Toast.makeText(this, "no images selected", Toast.LENGTH_LONG).show();
		}

	}
	public boolean checkPermit()
	{
		if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
			return false;
		else
			return true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		// TODO: Implement this method
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQ_PER && grantResults.length > 0)
		{

			if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
				selectMultipleImg();

				convertPdf();
			}
		}
	}
	public void selectMultipleImg()
	{
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_MULTI);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_MULTI && resultCode == RESULT_OK)
		{
			if (data != null)
			{
				ClipData cd=data.getClipData();
				if (cd != null)
				{
					/*for (int z=0;z < cd.getItemCount();z++)
					{
						cropUriTo = cd.getItemAt(z).getUri();
						if (cropUriTo != null)
						{
							Intent i=new Intent(this, CropImageActivity.class);
							i.putExtra("curl", cropUriTo.toString());
							startActivityForResult(i, REQ_CROP);
						}
					}
					*/
				}
				Toast.makeText(this, "found byte[] " + uriList.size(), Toast.LENGTH_LONG).show();
				convertPdf();
			}
		}
		else if (requestCode == REQ_CROP)
		{
			if (data != null && data.getStringExtra("cu") != null)
			{
				croppedUri = Uri.parse(data.getStringExtra("cu"));
				
				convertUri2(croppedUri);
			}}

	}
	public void convertUri2(Uri uri)
	{
		try
		{
			InputStream iStream =   getContentResolver().openInputStream(uri);
			try
			{
				byte[] inputData = getBytes(iStream);
				uriList.add(inputData);
				//Toast.makeText(this,"7->"+uriList.size(),Toast.LENGTH_LONG).show();
			}
			catch (IOException e)
			{
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		catch (FileNotFoundException e)
		{
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}

	}
	public byte[] getBytes(InputStream inputStream) throws IOException
	{
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];

		int len = 0;
		while ((len = inputStream.read(buffer)) != -1)
		{
			byteBuffer.write(buffer, 0, len);
		}
		return byteBuffer.toByteArray();
    }
	public void openPdf()
	{
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/example.pdf");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/pdf");
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}
}
