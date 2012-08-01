package com.thousandmemories.photon.core;

import java.io.IOException;
import java.io.InputStream;

public interface PhotoProvider {
    public InputStream getPhotoInputStream(String path) throws IOException;
}
