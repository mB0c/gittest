package com.gb.library.core.base;

import com.gb.library.core.data.RequestData;
import com.gb.library.core.data.ResponseData;

public interface IDataConverter {
    String convertRequest(RequestData requestData);
    ResponseData convertResponse(String resStr);
}
