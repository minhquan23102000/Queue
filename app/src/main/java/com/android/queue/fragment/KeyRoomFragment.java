package com.android.queue.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.queue.R;
import com.android.queue.SessionManager;
import com.android.queue.firebase.storage.FirebaseStorageRequester;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KeyRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KeyRoomFragment extends Fragment {


    //Init view
    private ImageView qrCodeImgView;
    private TextView roomKeyTv;
    private ImageButton downloadBtn;

    //Init session manager
    private SessionManager sessionManager;

    //Init firebase storage requester
    private FirebaseStorageRequester firebaseStorageRequester;

    public KeyRoomFragment() {
        // Required empty public constructor
    }

    public static KeyRoomFragment newInstance() {
        KeyRoomFragment fragment = new KeyRoomFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        sessionManager = new SessionManager(getContext());
        firebaseStorageRequester = new FirebaseStorageRequester(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_key_room, container, false);

        //Hook view
        qrCodeImgView = view.findViewById(R.id.qrcodeImg);
        roomKeyTv = view.findViewById(R.id.roomKeyTv);
        downloadBtn = view.findViewById(R.id.downloadBtn);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String roomKey = sessionManager.getCurrentRoomKey();

        //Fetch QR code into view from firebase
        firebaseStorageRequester.loadRoomQrCode(qrCodeImgView, roomKey);

        //Fetch room key into text view
        roomKeyTv.setText(roomKey);

    }
}