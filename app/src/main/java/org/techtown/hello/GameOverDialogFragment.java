package org.techtown.hello;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class GameOverDialogFragment extends DialogFragment {

    private static final String ARG_CUR  = "cur";
    private static final String ARG_BEST = "best";
    private static final String ARG_NEW = "new";

    public static GameOverDialogFragment newInstance(long cur, long best, boolean isNew) {
        Bundle b = new Bundle();
        b.putLong(ARG_CUR, cur);
        b.putLong(ARG_BEST, best);
        b.putBoolean(ARG_NEW,isNew);
        GameOverDialogFragment f = new GameOverDialogFragment();
        f.setArguments(b);
        return f;
    }

    @NonNull
    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        long cur  = getArguments().getLong(ARG_CUR);
        long best = getArguments().getLong(ARG_BEST);
        boolean isNew = getArguments().getBoolean(ARG_NEW);

        String msg = "현재 기록 : " + cur  + "초\n"
                + "최고 기록 : " + best + "초";

        if(isNew){
            msg += "\n\n \uD83C\uDF89 최고 기록을 달성했습니다!";
        }

        return new AlertDialog.Builder(requireContext())
                .setTitle("GAME SET")
                .setMessage(msg)
                .setPositiveButton("Restart", (d, w) -> {
                    startActivity(new Intent(requireContext(), MainActivity.class));
                    requireActivity().finish();
                })
                .setNegativeButton("Home", (d, w) -> {
                    startActivity(new Intent(requireContext(), StartActivity.class));
                    requireActivity().finish();
                })
                .create();
    }
}
