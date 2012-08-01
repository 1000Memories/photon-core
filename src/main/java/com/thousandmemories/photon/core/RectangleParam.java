package com.thousandmemories.photon.core;

import com.yammer.dropwizard.jersey.params.AbstractParam;

import java.awt.*;

public class RectangleParam extends AbstractParam<Rectangle> {
    public RectangleParam(String input) {
        super(input);
    }

    @Override
    protected Rectangle parse(String s) throws Exception {
        String[] parts = s.split(",");
        if (parts.length != 4) {
            throw new Exception("The crop field requires 4 arguments: x, y, width, and height.");
        }

        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        int w = Integer.parseInt(parts[2]);
        int h = Integer.parseInt(parts[3]);

        return new Rectangle(x, y, w, h);
    }
}
