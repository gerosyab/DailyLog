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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.UUID;

import io.realm.Realm;

public class NumberPickerDialog extends DialogFragment {

    AutoRepeatImageView minusImageView, plusImageView;
    EditText valueEditText;
    TextView titleTextView, unitTextView;
    long maxValue, mode;
    String titleStr, valueStr, unitStr;
    Record record;
    Realm realm;

    NumberPickerDialogListener mListener;

    public interface NumberPickerDialogListener {
        public void onNumberPickerDialogPositiveClick(DialogFragment dialog, IBinder iBinder, String dateStr);
        public void onNumberPickerDialogNegativeClick(DialogFragment dialog, IBinder iBinder);
        public void onNumberPickerDialogDeleteClick(DialogFragment dialog, IBinder iBinder, String dateStr);
    }

    public static NumberPickerDialog newInstance(String titleStr, String valueStr, String unitStr, long maxValue, long mode, Record record, Realm realm){
        NumberPickerDialog dialog = new NumberPickerDialog();

        Bundle args = new Bundle();
        args.putString("title", titleStr);
        args.putString("value", valueStr);
        args.putString("unit", unitStr);
        args.putLong("maxValue", maxValue);
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
        valueStr = getArguments().getString("value");
        unitStr = getArguments().getString("unit");
        maxValue = getArguments().getLong("maxValue");
        mode = getArguments().getLong("mode");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_number_picker, null);
        minusImageView = (AutoRepeatImageView) view.findViewById(R.id.numberPickerMinusImageView);
        plusImageView = (AutoRepeatImageView) view.findViewById(R.id.numberPickerPlusImageView);
        valueEditText = (EditText) view.findViewById(R.id.numberPickerValueEditText);
        titleTextView = (TextView) view.findViewById(R.id.numberPickerTitleTextView);
        unitTextView = (TextView) view.findViewById(R.id.numberPickerUnitTextView);

        valueEditText.setText(valueStr);
        titleTextView.setText(titleStr);
        unitTextView.setText(unitStr);

        valueEditText.setSelection(valueEditText.length());

        valueEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                MyLog.d("numberPicker", "beforeTextChanged entry: " + s + ", " + start + ", " + count + ", " + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MyLog.d("numberPicker", "onTextChanged entry: " + s + ", " + start + ", " + before + ", " + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                MyLog.d("numberPicker", "afterTextChanged entry: " + s);
                if (s.toString().length() == 0) {
                    s.append("0");
                }
//                else if(s.toString().equalsIgnoreCase("00")){
//                    s.clear();
//                }
                else if(s.length() > 1 && s.toString().startsWith("0")){
                    s.replace(0, 1, "");
                }
                else if(Long.parseLong(s.toString()) > maxValue){
                    s.replace(0, s.length(), String.valueOf(maxValue));
                }
            }
        });

        minusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long value = Long.parseLong(valueEditText.getText().toString());
                MyLog.d("numberPicker", "minusImageView.setOnClickListener : " + value);
                if(value == 0){

                }
                else{
                    value--;
                    valueEditText.setText("" + value);
                    valueEditText.setSelection(valueEditText.length());
                }
            }
        });

        plusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long value = Long.parseLong(valueEditText.getText().toString());
                MyLog.d("numberPicker", "plusImageView.setOnClickListener : " + value);
                if(value == maxValue){

                }
                else{
                    value++;
                    valueEditText.setText("" + value);
                    valueEditText.setSelection(valueEditText.length());
                }
            }
        });

        if(mode == StaticData.DIALOG_MODE_CREATE){
            builder.setView(view)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            long value = Long.parseLong(valueEditText.getText().toString());
                            if(value > 0) {
                                realm.beginTransaction();
                                Record newRecord = realm.createObject(Record.class, UUID.randomUUID().toString());
                                newRecord.setNumber(value);
                                newRecord.setCategoryId(record.getCategoryId());
                                newRecord.setDate(record.getDate());
                                //                            record.setNumber(Long.parseLong(valueEditText.getText().toString()));
                                MyLog.d("numberPicker", "positiveButton : " + record.getDate());
                                realm.insert(newRecord);
                                realm.commitTransaction();
                                mListener.onNumberPickerDialogPositiveClick(NumberPickerDialog.this, valueEditText.getWindowToken(), record.getDateString());
                            }
                            else {
                                dismiss();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onNumberPickerDialogNegativeClick(NumberPickerDialog.this, valueEditText.getWindowToken());
                            NumberPickerDialog.this.getDialog().cancel();
                        }
                    });
        }else if(mode == StaticData.DIALOG_MODE_EDIT){
            builder.setView(view)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            long value = Long.parseLong(valueEditText.getText().toString());
                            if(value > 0) {
                                realm.beginTransaction();
                                record.setNumber(Long.parseLong(valueEditText.getText().toString()));
                                MyLog.d("numberPicker", "positiveButton : " + record.getDate());
                                realm.insertOrUpdate(record);
                                realm.commitTransaction();
                                mListener.onNumberPickerDialogPositiveClick(NumberPickerDialog.this, valueEditText.getWindowToken(), record.getDateString());
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setMessage(getResources().getString(R.string.dialog_message_confirm_delete_record) + " [" + record.getDateString() + "]")
                                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String dateString = record.getDateString();
                                                realm.beginTransaction();
                                                record.deleteFromRealm();
                                                realm.commitTransaction();
                                                mListener.onNumberPickerDialogDeleteClick(NumberPickerDialog.this, valueEditText.getWindowToken(), dateString);
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
                                            mListener.onNumberPickerDialogDeleteClick(NumberPickerDialog.this, valueEditText.getWindowToken(), dateString);
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
                            mListener.onNumberPickerDialogNegativeClick(NumberPickerDialog.this, valueEditText.getWindowToken());
                            NumberPickerDialog.this.getDialog().cancel();
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
//            mListener = (NumberPickerDialogListener) activity;
//        } catch (ClassCastException e) {
//            // The activity doesn't implement the interface, throw exception
//            throw new ClassCastException(activity.toString()
//                    + " must implement NumberPickerDialogListener");
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NumberPickerDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NumberPickerDialogListener");
        }
    }
}
