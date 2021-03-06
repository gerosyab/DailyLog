package net.gerosyab.dailylog.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.gerosyab.dailylog.R;
import net.gerosyab.dailylog.data.Record;
import net.gerosyab.dailylog.data.StaticData;
import net.gerosyab.dailylog.util.MyLog;
import net.gerosyab.dailylog.view.AutoRepeatImageView;

import java.util.UUID;

import io.realm.Realm;

public class MessagePopupDialog extends DialogFragment {

    EditText messageEditText;
    TextView titleTextView, characterCountTextView;
    String titleStr, messageStr, characterCountStr;
    long maxLength, mode;
    Record record;
    Realm realm;

    MessagePopupDialogListener mListener;

    public interface MessagePopupDialogListener {
        public void onMessagePopupDialogPositiveClick(DialogFragment dialog, IBinder iBinder, String dateStr);
        public void onMessagePopupDialogNegativeClick(DialogFragment dialog, IBinder iBinder);
        public void onMessagePopupDialogDeleteClick(DialogFragment dialog, IBinder iBinder, String dateStr);
    }

    public static MessagePopupDialog newInstance(String titleStr, String messageStr, long maxLength, long mode, Record record, Realm realm){
        MessagePopupDialog dialog = new MessagePopupDialog();

        Bundle args = new Bundle();
        args.putString("title", titleStr);
        args.putString("message", messageStr);
        args.putLong("maxLength", maxLength);
        args.putLong("mode", mode);
        dialog.setArguments(args);
        dialog.setRecord(record);
        dialog.setRealm(realm);

        return dialog;
    }

    public void setRecord(Record record){
        this.record = record;
    }

    public void setRealm(Realm realm){
        this.realm = realm;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        titleStr = getArguments().getString("title");
        messageStr = getArguments().getString("message");
        maxLength = getArguments().getLong("maxLength");
        mode = getArguments().getLong("mode");

        MyLog.d("messagePopup", "messageStr : " + messageStr + ", maxLength : " + maxLength + ", mode : " + mode);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_message_popup, null);
        messageEditText = (EditText) view.findViewById(R.id.messagePopupEditText);
        titleTextView = (TextView) view.findViewById(R.id.messagePopupTitleTextView);
        characterCountTextView = (TextView) view.findViewById(R.id.messagePopupCharCountTextView);

        messageEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter((int)maxLength)});
        if(messageStr == null || messageStr.equalsIgnoreCase("")){
            messageEditText.setText("");
        }
        else{
            messageEditText.setText(messageStr);
        }
        titleTextView.setText(titleStr);
        if(messageStr == null || messageStr.equalsIgnoreCase("")){
            characterCountTextView.setText("0 / " + maxLength);
        }
        else{
            characterCountTextView.setText(messageStr.length() + " / " + maxLength);
        }


        messageEditText.setSelection(messageEditText.length());

        messageEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                MyLog.d("messagePopup", "beforeTextChanged entry: " + s + ", " + start + ", " + count + ", " + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MyLog.d("messagePopup", "onTextChanged entry: " + s + ", " + start + ", " + before + ", " + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                MyLog.d("messagePopup", "afterTextChanged entry: " + s);
                characterCountTextView.setText(s.length() + " / " + maxLength);
            }
        });

        if(mode == StaticData.DIALOG_MODE_CREATE){
            builder.setView(view)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String str = messageEditText.getText().toString();
                            if(str.length() > 0) {
                                realm.beginTransaction();
                                Record newRecord = realm.createObject(Record.class, UUID.randomUUID().toString());
                                newRecord.setString(str);
                                newRecord.setCategoryId(record.getCategoryId());
                                newRecord.setDate(record.getDate());
//                                record.setString(str);
//                                MyLog.d("messagePopup", "positiveButton : " + record.getDate());
                                realm.insert(newRecord);
                                realm.commitTransaction();
                                mListener.onMessagePopupDialogPositiveClick(MessagePopupDialog.this, messageEditText.getWindowToken(), record.getDateString());
                            }
                            else{
                                dismiss();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onMessagePopupDialogNegativeClick(MessagePopupDialog.this, messageEditText.getWindowToken());
                            MessagePopupDialog.this.getDialog().cancel();
                        }
                    });
        }else if(mode == StaticData.DIALOG_MODE_EDIT){
            builder.setView(view)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String str = messageEditText.getText().toString();
                            if(str.length() > 0) {
                                realm.beginTransaction();
                                record.setString(str);
                                MyLog.d("messagePopup", "positiveButton : " + record.getDate());
                                realm.insertOrUpdate(record);
                                realm.commitTransaction();
                                mListener.onMessagePopupDialogPositiveClick(MessagePopupDialog.this, messageEditText.getWindowToken(), record.getDateString());
                            }
                            else{
//                                dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setMessage(getResources().getString(R.string.dialog_message_confirm_delete_record) + " [" + record.getDateString() + "]")
                                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String dateString = record.getDateString();
                                                realm.beginTransaction();
                                                record.deleteFromRealm();
                                                realm.commitTransaction();
                                                mListener.onMessagePopupDialogDeleteClick(MessagePopupDialog.this, messageEditText.getWindowToken(), dateString);
                                                dismiss();
                                            }
                                        })
                                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).show();
                            }
                        }
                    })
                    .setNeutralButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage(getResources().getString(R.string.dialog_message_confirm_delete_record) + " [" + record.getDateString() + "]")
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String dateString = record.getDateString();
                                            realm.beginTransaction();
                                            record.deleteFromRealm();
                                            realm.commitTransaction();
                                            mListener.onMessagePopupDialogDeleteClick(MessagePopupDialog.this, messageEditText.getWindowToken(), dateString);
                                            dismiss();
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onMessagePopupDialogNegativeClick(MessagePopupDialog.this, messageEditText.getWindowToken());
                            MessagePopupDialog.this.getDialog().cancel();
                        }
                    });
        }

        return builder.create();
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        // Verify that the host activity implements the callback interface
//        try {
//            // Instantiate the NoticeDialogListener so we can send events to the host
//            mListener = (MessagePopupDialogListener) activity;
//        } catch (ClassCastException e) {
//            // The activity doesn't implement the interface, throw exception
//            throw new ClassCastException(activity.toString()
//                    + " must implement MessagePopupDialogListener");
//        }
//    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (MessagePopupDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement MessagePopupDialogListener");
        }
    }
}
