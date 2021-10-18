package com.android.queue.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.queue.R;
import com.android.queue.SessionManager;
import com.android.queue.firebase.realtimedatabase.RoomEntryRequester;
import com.android.queue.models.RoomData;
import com.android.queue.utils.TimestampHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract.RoomEntry.RoomDataEntry;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HostRoomSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HostRoomSettingFragment extends Fragment {

    public static final String TAG = HostRoomSettingFragment.class.getName();

    //Init view
    private TextView timeStartTextView;
    private TextInputLayout maxParticipantTextInput;
    private Slider timeWaitSlider;
    private Slider timeDelaySlider;
    private MaterialTextView closeRoomBtn;
    private MaterialButton pauseRoomBtn;
    private TextInputLayout waitSettingInputLayout;
    private AutoCompleteTextView waitSettingTextView;

    //Init session manager and firebase service
    private SessionManager sessionManager;
    private RoomEntryRequester roomEntryRequester;

    //Init context and activity
    private Context mContext;
    private Activity mActivity;

    //Init model
    private RoomData thisRoom;

    //Data ref for this room
    private DatabaseReference currentRoomRef;

    //Init array string for wait setting
    private static final String[] WAIT_SETTING_VIEW_ITEM = new String[]{"Cân bằng", "Mặc định"};

    public static HostRoomSettingFragment newInstance() {
        HostRoomSettingFragment fragment = new HostRoomSettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        mContext = getContext();
        mActivity = getActivity();
        sessionManager = new SessionManager(mContext);
        roomEntryRequester = new RoomEntryRequester(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_host_room_setting, container, false);

        //Hook view
        timeStartTextView = view.findViewById(R.id.timeStartTv);
        timeDelaySlider = view.findViewById(R.id.timeDelaySlider);
        timeWaitSlider = view.findViewById(R.id.timeWaitSlider);
        maxParticipantTextInput = view.findViewById(R.id.maxParticipantTv);
        closeRoomBtn = view.findViewById(R.id.closeBtn);
        pauseRoomBtn = view.findViewById(R.id.pauseBtn);

        //Create layout for wait setting
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, R.layout.spinner_item_layout, WAIT_SETTING_VIEW_ITEM);
        waitSettingTextView = view.findViewById(R.id.filled_exposed_dropdown);
        waitSettingTextView.setAdapter(adapter);


        //Đóng phòng, nếu trong phòng còn người xếp hàng, không cho đóng. Ngược lại, xác nhận lại việc đóng phòng, khi đóng user sẽ quay lại trang chính và xóa session.
        closeRoomBtn.setOnClickListener(v -> {
            if (thisRoom != null) {
                if (thisRoom.totalParticipant > thisRoom.currentWait) {
                    Snackbar.make(view, "Người chờ còn trong phòng, không thể đóng", Snackbar.LENGTH_SHORT).show();
                } else {

                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(mContext)
                                                            .setTitle("Xác nhận đóng phòng")
                                                            .setMessage("Bạn có muốn đóng phòng. Khi đóng phòng bạn không thể quay lại phòng nữa.")
                            .setPositiveButton("Có", (dialog, which) -> {
                                currentRoomRef.child(RoomDataEntry.ROOT_NAME).child(RoomDataEntry.IS_CLOSE_ARM).setValue(true)
                                        .addOnSuccessListener(unused -> {
                                            sessionManager.clearUserCurrentRoom();
                                            mActivity.finish();
                                        }).addOnFailureListener(e -> {
                                    Toast.makeText(mContext, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                            }).setNeutralButton("Không", (dialog, which) -> {

                            });
                    alert.show();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentRoomRef = roomEntryRequester.find(sessionManager.getCurrentRoomKey());
        currentRoomRef.child(RoomDataEntry.ROOT_NAME).addValueEventListener(eventListener);
    }

    private final ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            //Get data transferred from firebase with getValue method from dataSnapshot
            thisRoom = snapshot.getValue(RoomData.class);
            Log.d(TAG, "onDataChange: "  + thisRoom.timeWait.floatValue() + thisRoom.maxParticipant);

            timeStartTextView.setText("Bắt đầu vào: " + TimestampHelper.toDatetime(thisRoom.timeStart));

            float timeWait = thisRoom.timeWait.floatValue();
            timeWaitSlider.setValue(timeWait);
            timeWaitSlider.setValueFrom(timeWait - 60);
            timeWaitSlider.setValueTo(timeWait + 60);

            float timeDelay = thisRoom.timeDelay.floatValue();
            timeDelaySlider.setValue(timeDelay);
            timeDelaySlider.setValueFrom(timeDelay - 30);
            timeDelaySlider.setValueTo(timeDelay+ 30.0f);

            maxParticipantTextInput.getEditText().setText(thisRoom.maxParticipant + "");

            waitSettingTextView.setText(WAIT_SETTING_VIEW_ITEM[getWaitIndex()], false);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Snackbar.make(getView(), "Lỗi đường truyền không ổn định", Snackbar.LENGTH_LONG).show();
        }
    };

    private String getWaitSetting() {
        String selectValue = waitSettingTextView.getText().toString().trim();
        if (selectValue.equals(WAIT_SETTING_VIEW_ITEM[0])) {
            return RoomDataEntry.BALANCE_WAIT;
        } else {
            return RoomDataEntry.CONSTANT_WAIT;
        }
    }

    private int getWaitIndex() {
       if (thisRoom.waitSetting.equals(RoomDataEntry.CONSTANT_WAIT)) {
           return 0;
       } else {
           return 1;
       }
    }
}