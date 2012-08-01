package com.thousandmemories.photon.core;

import com.yammer.dropwizard.jersey.caching.CacheControl;
import com.yammer.dropwizard.logging.Log;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.annotation.Timed;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;


@Path("/{name}")
public class PhotoResource {
    private static final Log LOG = Log.forClass(PhotoResource.class);
    private static final Timer readTimer = Metrics.newTimer(PhotoResource.class, "read", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);


    private final PhotoProvider photoProvider;

    public PhotoResource(PhotoProvider photoProvider) {
        this.photoProvider = photoProvider;
    }

    @GET
    @Timed
    @CacheControl(immutable = true)
    public Response getPhoto(@PathParam("name") String name,
                             @MatrixParam("w") WidthParam width,
                             @MatrixParam("r") RotationParam rotateAngle,
                             @MatrixParam("c") RectangleParam crop) throws Exception {
        InputStream resultStream;


        InputStream imageStream = photoProvider.getPhotoInputStream(name);
        if (imageStream == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        String mimeType = URLConnection.guessContentTypeFromStream(imageStream);
        if (mimeType == null) {
            throw new WebApplicationException(501); // Not implemented
        }

        if (width != null || rotateAngle != null || crop != null) {
            BufferedImage image;
            try {
                TimerContext readContext = readTimer.time();
                image = ImageIO.read(imageStream);
                imageStream.close();
                readContext.stop();
            } finally {
                imageStream.close();
            }

            if (crop != null) {
                image = com.thousandmemories.photon.core.Processor.crop(image, crop.get());
            }

            if (rotateAngle != null) {
                image = com.thousandmemories.photon.core.Processor.rotate(image, rotateAngle.get());
            }

            if (width != null) {
                image = com.thousandmemories.photon.core.Processor.fitToWidth(image, width.get());
            }

            Iterator<ImageWriter> i = ImageIO.getImageWritersByMIMEType(mimeType);
            if (!i.hasNext()) {
                mimeType = "image/jpeg";
                i = ImageIO.getImageWritersByMIMEType(mimeType);
            }

            ImageWriter writer = i.next();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            writer.setOutput(new MemoryCacheImageOutputStream(os));
            writer.write(image);
            image.flush();
            image = null;
            resultStream = new ByteArrayInputStream(os.toByteArray());
        } else {
            resultStream = imageStream;
        }

        return Response.
                ok(resultStream, mimeType).
                build();

    }
}
