package com.example.gametest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class PauseDialogFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final GameFragment game = (GameFragment)getActivity().getFragmentManager()
				.findFragmentById(getArguments().getInt("gameId"));
		
		builder.setMessage("Paused!");

        builder.setPositiveButton("Resume", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				game.run();
			}
		});
        
        builder.setCancelable(false);

        return builder.create();
	}
}
