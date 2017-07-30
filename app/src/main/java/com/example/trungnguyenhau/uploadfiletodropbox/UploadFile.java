package com.example.trungnguyenhau.uploadfiletodropbox;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by TRUNGNGUYENHAU on 6/1/2017.
 */

public class UploadFile extends AsyncTask<Void, Void, Boolean> {
    private DropboxAPI dropboxAPI;
    private String path;
    private Context context;

    public UploadFile(Context context, DropboxAPI dropboxAPI, String path) {
        super();
        this.dropboxAPI = dropboxAPI;
        this.path = path;
        this.context = context;
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        final File tempDropboxDirectory = context.getCacheDir();
        File tempFileToUpload;
        FileWriter fileWriter = null;

        try {
            tempFileToUpload = File.createTempFile("file", ".txt", tempDropboxDirectory);
            fileWriter = new FileWriter(tempFileToUpload);
            fileWriter.write("Toi Ten La Nguyen Trung Hau");
            fileWriter.close();

            FileInputStream fileInputStream = new FileInputStream(tempFileToUpload);
            dropboxAPI.putFile(path + "intro.txt", fileInputStream,
                    tempFileToUpload.length(), null, null);

            tempFileToUpload.delete();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DropboxException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result)
        {
            Toast.makeText(context, "File Has Been Uploaded", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(context, "Error occured while processing the upload request",
                    Toast.LENGTH_LONG).show();
        }

    }
}
