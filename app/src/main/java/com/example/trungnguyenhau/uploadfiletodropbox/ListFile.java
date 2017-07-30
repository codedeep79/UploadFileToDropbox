package com.example.trungnguyenhau.uploadfiletodropbox;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.util.ArrayList;

/**
 * Created by TRUNGNGUYENHAU on 6/1/2017.
 */

public class ListFile extends AsyncTask<Void, Void, ArrayList> {

    private DropboxAPI dropboxAPI;
    private String path;
    private Handler handler;

    public ListFile(DropboxAPI dropboxAPI, String path, Handler handler) {
        this.dropboxAPI = dropboxAPI;
        this.path = path;
        this.handler = handler;
    }

    @Override
    protected ArrayList doInBackground(Void... params) {
        ArrayList files = new ArrayList();
        try {
            DropboxAPI.Entry directory = dropboxAPI.metadata(path, 1000, null, true, null);
            for (DropboxAPI.Entry entry: directory.contents)
            {
                files.add(entry.fileName());

            }
        } catch (DropboxException e) {
            e.printStackTrace();
        }
        return files;
    }

    @Override
    protected void onPostExecute(ArrayList result) {
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("data", result);

        message.setData(bundle);
        handler.sendMessage(message);
    }
}
