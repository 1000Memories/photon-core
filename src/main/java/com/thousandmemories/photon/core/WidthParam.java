package com.thousandmemories.photon.core;

import com.yammer.dropwizard.jersey.params.AbstractParam;

// TODO: get rid of this

public class WidthParam extends AbstractParam<Integer> {
    public WidthParam(String input) {
        super(input);
    }

    @Override
    protected Integer parse(String s) throws Exception {
        return Integer.valueOf(s);
    }
}
