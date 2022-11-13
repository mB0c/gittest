package com.gb.library.core.base;

import androidx.annotation.NonNull;

public interface IDataCallback<T> {
   void onSuccess(T data);
   void onFail(@NonNull UpgradeException exception);
}
