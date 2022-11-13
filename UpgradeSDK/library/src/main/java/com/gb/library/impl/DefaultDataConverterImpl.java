package com.gb.library.impl;

import com.gb.library.core.base.IDataConverter;
import com.gb.library.core.data.RequestData;
import com.gb.library.core.data.ResponseData;
import com.google.gson.Gson;

public class DefaultDataConverterImpl implements IDataConverter {


    @Override
    public String convertRequest(RequestData requestData) {
        return new Gson().toJson(requestData);
    }

    @Override
    public ResponseData convertResponse(String resStr) {
        return new Gson().fromJson(resStr, ResponseData.class);
    }
}
