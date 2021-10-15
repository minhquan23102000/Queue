package com.android.queue.firebase.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.UUID;

public class FirebaseStorageRequester {
    static final private String QR_CODE_FOLDER = "qrcode/";
    static final private String CLOUD_URL = "gs://queue-eb51b.appspot.com";
    private Context mContext;
    // Create a Cloud Storage reference from the app
    private final FirebaseStorage storage;

    public FirebaseStorageRequester(Context context) {
        mContext = context;
        storage = FirebaseStorage.getInstance();
    }

    public void uploadFile(Uri file, String roomKey) {
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl(CLOUD_URL).child(QR_CODE_FOLDER);
        StorageReference riversRef = storageRef.child(roomKey);
        UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(mContext, "Lỗi upload mã QR cho phòng chờ", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

    }

    public String initAnUniqueString (){
        return UUID.randomUUID().toString();
    }
}
