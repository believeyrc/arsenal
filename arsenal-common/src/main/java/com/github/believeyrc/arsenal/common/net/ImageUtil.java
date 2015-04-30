package com.github.believeyrc.arsenal.common.net;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(ImageUtil.class);
	
	public static void resize(String imgPath, int width, int height, String prevfix, boolean force) {
		
		File imgFile = new File(imgPath);
		if (imgFile.exists()) {
            try {
            	String types = Arrays.toString(ImageIO.getReaderFormatNames());
    			String suffix = null;
    			if (imgFile.getName().indexOf(".") > -1) {
                    suffix = imgFile.getName().substring(imgFile.getName().lastIndexOf(".") + 1);
                }
    	
                if (suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase()) < 0){
                	LOG.error("the image type is illegal. the standard image type is {}." + types);
                    return ;
                }
				Image image = ImageIO.read(imgFile);
				if (! force) {
					int w = image.getWidth(null);
                    int h = image.getHeight(null);
					if ((w * 1.0) / width < (h * 1.0) / height) {
						if (w > width) {
							height = Integer.parseInt(new DecimalFormat("0").format(h * width / (w * 1.0)));
						}
					} else {
						if (h > height) {
							width = Integer.parseInt(new DecimalFormat("0").format(w * height / (h * 1.0)));
						}
					}
				}
				BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics graphics = bufferedImage.getGraphics();
				graphics.drawImage(image, 0, 0, width, height, Color.LIGHT_GRAY, null);
				graphics.dispose();
                ImageIO.write(bufferedImage, suffix, 
                		new File(imgPath.substring(0, imgPath.lastIndexOf("/") + 1) + prevfix + imgFile.getName()));
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
            
            
		} else {
			LOG.info("{} file not exists!", imgPath);
		}
		
	}
	
	public static void main(String[] args) {
		String imgPath = "D://pic/f.jpg";
		resize(imgPath, 280, 280, "280x280_", true);
		resize(imgPath, 100, 100, "100x100_", true);
	}

}
