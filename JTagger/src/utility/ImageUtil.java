package utility;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;

public class ImageUtil {
	public static Image makeSWTImage(Display display, java.awt.Image ai) throws Exception { 
		int width = ai.getWidth(null); 
		int height = ai.getHeight(null); 
		BufferedImage bufferedImage = 
				new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
		Graphics2D g2d = bufferedImage.createGraphics(); 
		g2d.drawImage(ai, 0, 0, null); 
		g2d.dispose(); 
		int[] data = 
			((DataBufferInt)bufferedImage.getData().getDataBuffer()) 
			.getData(); 
		ImageData imageData = 
			new ImageData(width, height, 24, 
			new PaletteData(0xFF0000, 0x00FF00, 0x0000FF)); 
			imageData.setPixels(0, 0, data.length, data, 0); 
		Image swtImage = new Image(display, imageData); 
		return swtImage; 
	} 
}
