package yolo.octo.dangerzone;

import yolo.octo.dangerzone.core.GameFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class PauseDialogFragment extends DialogFragment {
	GameFragment game = null;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		game = (GameFragment)getActivity().getFragmentManager()
				.findFragmentById(getArguments().getInt("gameId"));
		
		builder.setMessage("Paused!");

        builder.setPositiveButton("Resume", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				game.run();
			}
		});

        return builder.create();
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		if (game != null)
			game.run();
	}
}
