package com.msproject.myhome.dailycloset;

import android.os.Parcelable;

import java.io.Serializable;

public interface FileSaveNotifyListener extends Parcelable {
    public void notifyDatasetChanged();
}
