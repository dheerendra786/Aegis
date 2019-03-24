package aegis.com.aegis.activity;

import aegis.com.aegis.R;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.jean.jcplayer.JcPlayerManagerListener;
import com.example.jean.jcplayer.general.JcStatus;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import java.io.File;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;

public class RecordingPlayerFragment extends Fragment
  implements RecordingPlayerRecyclerAdapter.onPlayerClick {

  private JcPlayerView jcPlayerView;
  private RecordingPlayerRecyclerAdapter adapter;
  private int recordingPosition = 0;

  final JcPlayerManagerListener listener = new JcPlayerManagerListener() {
    @Override
    public void onPreparedAudio(@NotNull JcStatus jcStatus) {

    }

    @Override
    public void onCompletedAudio() {
      if (adapter != null && recordingPosition != -1) {
        adapter.stopAudio(recordingPosition);
      }
    }

    @Override
    public void onPaused(@NotNull JcStatus jcStatus) {
      if (adapter != null && recordingPosition != -1) {
        adapter.playAudio(recordingPosition);
      }
    }

    @Override
    public void onContinueAudio(@NotNull JcStatus jcStatus) {

    }

    @Override
    public void onPlaying(@NotNull JcStatus jcStatus) {

    }

    @Override
    public void onTimeChanged(@NotNull JcStatus jcStatus) {

    }

    @Override
    public void onStopped(@NotNull JcStatus jcStatus) {

    }

    @Override
    public void onJcpError(@NotNull Throwable throwable) {

    }

    @Override
    public void onRepeat() {

    }
  };

  @Nullable
  @Override
  public View onCreateView(
    @NonNull LayoutInflater inflater,
    @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState
  ) {
    return inflater.inflate(R.layout.fragment_record_player_list, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (getActivity() != null
      && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
      == PackageManager.PERMISSION_GRANTED) {

      final File audioFileList = new File(Environment.getExternalStorageDirectory() + "/aegis");
      if (audioFileList.exists() && audioFileList.listFiles().length > 0) {
        final RecyclerView rvAudioList = view.findViewById(R.id.rvAudioList);
        jcPlayerView = view.findViewById(R.id.jcplayer);
        jcPlayerView.setJcPlayerManagerListener(listener);
        adapter = new RecordingPlayerRecyclerAdapter(audioFileList.listFiles(), this);
        rvAudioList.setAdapter(adapter);
        rvAudioList.setLayoutManager(new LinearLayoutManager(getActivity()));
        /*for (final File audioFile : audioFileList.listFiles()) {
          Log.d("RecordingPlayer", audioFile.getName());
        }*/
      }
    }
  }

  @Override
  public void onItemClick(int position) {
    if (adapter != null) {
      if (recordingPosition != position) {
        jcPlayerView.pause();
        jcPlayerView.clearAllAudio();
        jcPlayerView.setMyPlaylist(Collections.<JcAudio>emptyList());
        if (adapter != null) {
          jcPlayerView.playAudio(JcAudio.createFromFilePath(adapter.getItem(position).getAbsolutePath()));
        }
        recordingPosition = position;
        adapter.playAudio(position);
      } else {
        jcPlayerView.pause();
        recordingPosition = -1;
        adapter.stopAudio(position);
      }
    }
  }

  @Override
  public void onDestroy() {
    if (jcPlayerView != null) {
      jcPlayerView.kill();
    }
    super.onDestroy();
  }
}
