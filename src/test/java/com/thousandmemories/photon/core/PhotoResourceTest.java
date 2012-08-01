package com.thousandmemories.photon.core;

import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import com.sun.jersey.api.client.ClientResponse;
import com.yammer.dropwizard.testing.ResourceTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class PhotoResourceTest extends ResourceTest {
    @Mock
    private PhotoProvider photoProvider;

    @Before
    public void setUp() throws Exception {
        Logger.getLogger("com.sun.jersey").setLevel(Level.WARNING);
    }

    private InputStream getImage(String imageName) throws IOException {
        return Resources.newInputStreamSupplier(Resources.getResource("images/" + imageName)).getInput();
    }

    @Override
    protected void setUpResources() throws Exception {
        addResource(new PhotoResource(photoProvider));
    }

    @Test
    public void testNoModifications() throws Exception {
        when(photoProvider.getPhotoInputStream("mf.jpg")).thenReturn(getImage("mf.jpg"));

        ClientResponse response = client().resource("/mf.jpg").get(ClientResponse.class);
        assertThat(response.getEntity(byte[].class), is(ByteStreams.toByteArray(getImage("mf.jpg"))));
        assertThat(response.getType().toString(), is("image/jpeg"));
    }

    @Test
    public void test404() throws Exception {
        when(photoProvider.getPhotoInputStream("doesntexist.jpg")).thenReturn(null);

        ClientResponse response = client().resource("/doesntexist.jpg").get(ClientResponse.class);
        assertThat(response.getStatus(), is(404));
    }

    @Test
    public void testFitWidth() throws Exception {
        when(photoProvider.getPhotoInputStream("mf.jpg")).thenReturn(getImage("mf.jpg"));

        ClientResponse response = client().resource("/mf.jpg;w=200").get(ClientResponse.class);
        assertThat(response.getType().toString(), is("image/jpeg"));
        BufferedImage result = ImageIO.read(response.getEntity(InputStream.class));
        assertThat(result.getWidth(), is(200));
    }

    @Test
    public void testRotate() throws Exception {
        BufferedImage initialImage = ImageIO.read(getImage("mf.jpg"));
        int initialHeight = initialImage.getHeight();
        int initialWidth = initialImage.getWidth();

        when(photoProvider.getPhotoInputStream("mf.jpg")).thenReturn(getImage("mf.jpg"));

        ClientResponse response = client().resource("/mf.jpg;r=90").get(ClientResponse.class);
        assertThat(response.getType().toString(), is("image/jpeg"));
        BufferedImage result = ImageIO.read(response.getEntity(InputStream.class));
        assertThat(result.getWidth(), is(initialHeight));
        assertThat(result.getHeight(), is(initialWidth));
    }

    @Test
    public void testCropping() throws Exception {
        int x = 10;
        int y = 100;
        int w = 20;
        int h = 200;

        when(photoProvider.getPhotoInputStream("mf.jpg")).thenReturn(getImage("mf.jpg"));

        ClientResponse response = client().resource("/mf.jpg;c=" + x + "," + y + "," + w + "," + h).get(ClientResponse.class);
        assertThat(response.getType().toString(), is("image/jpeg"));
        BufferedImage result = ImageIO.read(response.getEntity(InputStream.class));
        assertThat(result.getWidth(), is(w));
        assertThat(result.getHeight(), is(h));
    }

    @Test
    public void testPNG() throws Exception {
        when(photoProvider.getPhotoInputStream("liz.png")).thenReturn(getImage("liz.png"));

        ClientResponse response = client().resource("/liz.png;w=200").get(ClientResponse.class);
        assertThat(response.getType().toString(), is("image/png"));
        BufferedImage result = ImageIO.read(response.getEntity(InputStream.class));
        assertThat(result.getWidth(), is(200));
    }

    @Test
    public void testUnsupportedType() throws Exception {
        when(photoProvider.getPhotoInputStream("fake.tiff")).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));

        ClientResponse response = client().resource("/fake.tiff;w=200").get(ClientResponse.class);
        assertThat(response.getStatus(), is(501));
    }

}
