package com.thousandmemories.photon.core;

import com.yammer.dropwizard.jersey.params.AbstractParam;
import org.imgscalr.Scalr;

public class RotationParam extends AbstractParam<Scalr.Rotation> {
    public RotationParam(String input) {
        super(input);
    }

    @Override
    protected Scalr.Rotation parse(String s) throws Exception {
        switch(Integer.parseInt(s)) {
            case 90: return Scalr.Rotation.CW_90;
            case 180: return Scalr.Rotation.CW_180;
            case 270: return Scalr.Rotation.CW_270;
            default: throw new Exception("Rotation parameter must be one of: 90, 180, 270.");
        }
    }
}
