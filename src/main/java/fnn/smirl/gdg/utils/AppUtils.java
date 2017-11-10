package fnn.smirl.gdg.utils;
import android.graphics.*;
import android.content.*;
import android.content.res.*;

public class AppUtils {

  public static Bitmap textToBitmap(String text, float textSize, int textColor) {
	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	paint.setTextSize(textSize);
	paint.setColor(textColor);
	paint.setTextAlign(Paint.Align.LEFT);
	float baseline = -paint.ascent();
	int width = (int)(paint.measureText(text) + 0.0f);
	int height = (int)(baseline + paint.descent() + 0.0f);
	Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	Canvas cnv = new Canvas(img);
	cnv.drawText(text, 0, baseline, paint);
	return img;
  }

  public static Bitmap multiLineTextToBitmap(Context context, String text, float textSize, int textColor) {
	Resources res = context.getResources();
	float scale = res.getDisplayMetrics().density;
	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	paint.setTextSize((int)textSize * scale);
	paint.setColor(textColor);
	paint.setTextAlign(Paint.Align.LEFT);
	float baseline = -paint.ascent();
	int height = (int)(baseline + paint.descent() + 0.0f);
	int x = 0;
	int y = height;
	int width = 0;

	int noOfLines = 0;
	for (String line : text.split("\n")) {
	  noOfLines++;
	  int  w = (int)(paint.measureText(line) + 0.0f);
	  if (w > width)width = w;
	}
	
	Bitmap img = Bitmap.createBitmap(width, height * noOfLines, Bitmap.Config.ARGB_8888);
	Canvas cnv = new Canvas(img);
	for (String s : text.split("\n")) {
	  int  w = (int)(paint.measureText(s) + 0.0f);
	  x = (width - w) / 2;
	  cnv.drawText(s, x, y, paint);
	  y += height;
	}
	return img;
  }
}
