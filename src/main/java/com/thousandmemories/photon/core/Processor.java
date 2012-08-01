package com.thousandmemories.photon.core;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;
import org.imgscalr.AsyncScalr;
import org.imgscalr.Scalr;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

public class Processor {
    private static final Timer cropTimer = Metrics.newTimer(Processor.class, "crop", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
    private static final Timer rotateTimer = Metrics.newTimer(Processor.class, "rotate", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
    private static final Timer resizeTimer = Metrics.newTimer(Processor.class, "fitToWidth", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);

    static {
        System.setProperty(AsyncScalr.THREAD_COUNT_PROPERTY_NAME, "4");
    }

    public static BufferedImage rotate(BufferedImage image, Scalr.Rotation rotation) throws Exception {
        TimerContext rotateContext = rotateTimer.time();
        BufferedImage result = AsyncScalr.rotate(image, rotation).get();
        image.flush();
        rotateContext.stop();
        return result;
    }

    public static BufferedImage fitToWidth(BufferedImage image, int width) throws Exception {
        // TODO: Don't ever make an image bigger
        TimerContext resizeContext = resizeTimer.time();
        BufferedImage result = AsyncScalr.resize(image, Scalr.Mode.FIT_TO_WIDTH, width).get();
        image.flush();
        resizeContext.stop();
        return result;
    }

    public static BufferedImage crop(BufferedImage image, Rectangle bounds) throws Exception {
        TimerContext cropContext = cropTimer.time();
        BufferedImage result = AsyncScalr.crop(image, bounds.x, bounds.y, bounds.width, bounds.height).get();
        image.flush();
        cropContext.stop();
        return result;
    }
}
