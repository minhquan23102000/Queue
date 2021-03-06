package com.android.queue.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.android.queue.firebase.realtimedatabase.QueueDatabaseContract.RoomEntry.RoomDataEntry;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


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
    private static final String[] WAIT_SETTING_VIEW_ITEM = new String[]{"C??n b???ng", "M???c ?????nh"};

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
        View view = inflater.inflate(R.layout.fragment_host_room_setting, container, false);

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


        //????ng ph??ng, n???u trong ph??ng c??n ng?????i x???p h??ng, kh??ng cho ????ng. Ng?????c l???i, x??c nh???n l???i vi???c ????ng ph??ng,
        // khi ????ng user s??? quay l???i trang ch??nh v?? x??a session.
        closeRoomBtn.setOnClickListener(v -> {
            if (thisRoom != null) {
                if (thisRoom.totalParticipant > thisRoom.currentWait) {
                    Snackbar.make(view, "Ng?????i ch??? c??n trong ph??ng, kh??ng th??? ????ng", Snackbar.LENGTH_SHORT).show();
                } else {

                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(mContext)
                            .setTitle("X??c nh???n ????ng ph??ng")
                            .setMessage("B???n c?? mu???n ????ng ph??ng. Khi ????ng ph??ng b???n kh??ng th??? quay l???i ph??ng n???a.")
                            .setPositiveButton("C??", (dialog, which) -> {
                                currentRoomRef.child(RoomDataEntry.ROOT_NAME).child(RoomDataEntry.IS_CLOSE_ARM).setValue(true)
                                        .addOnSuccessListener(unused -> {
                                            sessionManager.clearUserCurrentRoom();
                                            mActivity.finish();
                                        }).addOnFailureListener(e -> Toast.makeText(mContext, "L???i: " + e.getMessage(), Toast.LENGTH_LONG).show());
                            }).setNeutralButton("Kh??ng", (dialog, which) -> {

                            });
                    alert.show();
                }
            }
        });

        //T???m d???ng ph??ng, khi ph??ng ch??? t???m d???ng s??? kh??ng cho ng?????i x???p h??ng tham gia n???a.
        // Khi t???m ch???, ch??? ph??ng ph???i nh???p kho???ng th???i gian t???m ch???.
        // Khi ph??ng ch??? ??ang t???m d??ng, button n??y s??? hi???n th??? d?????i d???ng v?? c?? ch???c n??ng m??? ph??ng ch???.
        pauseRoomBtn.setOnClickListener(v -> {
            if (thisRoom != null) {
                //N???u ph??ng ch??? ch??a b???t ?????u, kh??ng th??? t???m d???ng
                if (thisRoom.timeStart >= System.currentTimeMillis()) {
                    Toast.makeText(mContext,
                            "Ph??ng ch??? ch??a ?????n th???i gian b???t ?????u. Kh??ng th??? t???m d???ng", Toast.LENGTH_LONG).show();
                } else {
                    //N???u ph??ng ch??? ??ang pause. S??? c?? ch???c n??ng m??? ph??ng ch???.
                    if (thisRoom.isPause) {
                        currentRoomRef.child(RoomDataEntry.ROOT_NAME).child(RoomDataEntry.IS_PAUSE_ARM).setValue(false)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(mContext, "M??? ph??ng th??nh c??ng", Toast.LENGTH_SHORT).show();
                                    pauseRoomBtn.setText("T???m d???ng");
                                }).addOnFailureListener(e -> Toast.makeText(mContext, "L???i: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    } else {
                        //B???t ?????ng h??? ????? user ch???n kho???ng th???i gian t???m d???ng
                        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                                .setTimeFormat(TimeFormat.CLOCK_24H)
                                .setHour(1)
                                .setMinute(10)
                                .setTitleText("T???m d???ng trong v??ng")
                                .build();

                        picker.show(getParentFragmentManager(), TAG);
                        picker.addOnPositiveButtonClickListener(v1 -> {
                            //L???y gi??? t???m d???ng
                            Timestamp timeStart = new Timestamp(System.currentTimeMillis() +
                                    TimeUnit.MINUTES.toMillis(picker.getMinute()) + TimeUnit.HOURS.toMillis(picker.getHour()));
                            //Update ?????ng b??? hai tr?????ng timeStart v?? isPause, ch??ng ta s??? t???o m???t hashmap
                            HashMap<String, Object> updateField = new HashMap<>();
                            updateField.put(RoomDataEntry.IS_PAUSE_ARM, true);
                            updateField.put(RoomDataEntry.TIME_START_ARM, timeStart.getTime());
                            //Update l??n firebase
                            currentRoomRef.child(RoomDataEntry.ROOT_NAME).updateChildren(updateField)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(mContext, "???? t???m d???ng ph??ng th??nh c??ng", Toast.LENGTH_SHORT).show();
                                        pauseRoomBtn.setText("T???m d???ng");
                                    }).addOnFailureListener(e -> Toast.makeText(mContext, "L???i: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        });
                        picker.addOnCancelListener(dialog -> {

                        });

                    }
                }
            }
        });

        //Th??m key end event cho maxParticipant Input Edit Text, ????? khi user d???ng nh???p v?? ???n enter th?? s??? t??? ?????ng update l??n firebase
        final TextInputEditText maxParticipantEditText = (TextInputEditText) maxParticipantTextInput.getEditText();
        maxParticipantEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    long maxParticipant = Long.parseLong(maxParticipantEditText.getText().toString());
                    currentRoomRef.child(RoomDataEntry.ROOT_NAME)
                            .child(RoomDataEntry.MAX_PARTICIPANT_ARM)
                            .setValue(maxParticipant)
                            .addOnFailureListener(e -> Toast.makeText(mContext, "L???i: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    return true;
                }
                return false;
            }
        });

        //Th??m event cho spinner TimeDelay  ????? c???p nh???t data l??n firebase
        timeDelaySlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                currentRoomRef.child(RoomDataEntry.ROOT_NAME)
                        .child(RoomDataEntry.TIME_DELAY_ARM)
                        .setValue(slider.getValue())
                        .addOnFailureListener(e -> Toast.makeText(mContext, "L???i: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });

        //Th??m event cho spinner TimeWait ????? c???p nh???t data l??n firebase
        timeWaitSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                currentRoomRef.child(RoomDataEntry.ROOT_NAME)
                        .child(RoomDataEntry.TIME_WAIT_ARM)
                        .setValue(slider.getValue())
                        .addOnFailureListener(e -> Toast.makeText(mContext, "L???i: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });

        //Th??m formatter cho slider
        timeWaitSlider.setLabelFormatter(value -> (int)value + " ph??t");
        timeDelaySlider.setLabelFormatter(value -> value + " ph??t");

        //Th??m event cho autocomplete textview waitSetting, ????? update l??n firebase.
        waitSettingTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String waitSetting = getWaitSetting(WAIT_SETTING_VIEW_ITEM[position]);
                currentRoomRef.child(RoomDataEntry.ROOT_NAME)
                        .child(RoomDataEntry.WAIT_SETTING_ARM)
                        .setValue(waitSetting)
                        .addOnFailureListener(e -> Toast.makeText(mContext, "L???i: " + e.getMessage(), Toast.LENGTH_LONG).show());
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

            timeStartTextView.setText("B???t ?????u v??o: " + TimestampHelper.toDatetime(thisRoom.timeStart));

            float timeWait = thisRoom.timeWait.floatValue();
            timeWaitSlider.setValue(timeWait);

            float timeDelay = thisRoom.timeDelay.floatValue();
            timeDelaySlider.setValue(timeDelay);

            maxParticipantTextInput.getEditText().setText(thisRoom.maxParticipant + "");

            waitSettingTextView.setText(WAIT_SETTING_VIEW_ITEM[getWaitIndex()], false);

            if (thisRoom.isPause) {
                pauseRoomBtn.setText("M??? ph??ng ch???");
            } else {
                pauseRoomBtn.setText("T???m d???ng");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Snackbar.make(getView(), "L???i ???????ng truy???n kh??ng ???n ?????nh", Snackbar.LENGTH_LONG).show();
        }
    };

    private String getWaitSetting(String selectValue) {
        if (selectValue.equals(WAIT_SETTING_VIEW_ITEM[0])) {
            return RoomDataEntry.BALANCE_WAIT;
        } else {
            return RoomDataEntry.CONSTANT_WAIT;
        }
    }

    private int getWaitIndex() {
        if (thisRoom.waitSetting.equals(RoomDataEntry.CONSTANT_WAIT)) {
            return 1;
        } else {
            return 0;
        }
    }
}