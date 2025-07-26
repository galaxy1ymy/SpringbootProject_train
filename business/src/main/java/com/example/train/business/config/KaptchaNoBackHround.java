package com.example.train.business.config;

import com.google.code.kaptcha.BackgroundProducer;
import com.google.code.kaptcha.util.Configurable;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class KaptchaNoBackHround extends Configurable implements BackgroundProducer {

    public KaptchaNoBackHround() {

    }
    @Override
    public BufferedImage addBackground(BufferedImage baseImage) {
       int width=baseImage.getWidth();
       int height=baseImage.getHeight();
       BufferedImage imageWidthBackground=new BufferedImage(width,height,1);
        Graphics2D graph=(Graphics2D) imageWidthBackground.getGraphics();
        graph.fill(new Rectangle2D.Double(0.0D, 0.0D, (double) width, (double) height) );
        graph.drawImage(baseImage, 0, 0, null);
        return imageWidthBackground;
    }
}
