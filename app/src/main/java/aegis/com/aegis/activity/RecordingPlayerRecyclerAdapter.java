package aegis.com.aegis.activity;

import aegis.com.aegis.R;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;

public class RecordingPlayerRecyclerAdapter extends RecyclerView.Adapter<RecordingPlayerRecyclerAdapter.ViewHolder> {

  private final File[] audioFileList;
  private final RecordingPlayerRecyclerAdapter.onPlayerClick onPlayerClick;
  private int stopRecordingPosition = -1;
  private int playRecordingPosition = -1;

  RecordingPlayerRecyclerAdapter(final File[] audioFileList, final onPlayerClick onPlayerClick) {
    this.audioFileList = audioFileList;
    this.onPlayerClick = onPlayerClick;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(
    @NonNull ViewGroup parent, int viewType
  ) {
    final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.tvMusicName.setText(audioFileList[position].getName());

    holder.ivPlayPause.setImageResource(R.drawable.ic_play);

    //if (stopRecordingPosition == position) {
    //  holder.ivPlayPause.setImageResource(R.drawable.ic_play);
    //  stopRecordingPosition = -1;
    //}

    if (playRecordingPosition == position) {
      holder.ivPlayPause.setImageResource(R.drawable.ic_pause);
    }

    holder.itemView.setTag(position);
    holder.itemView.setOnClickListener(clickListener);
  }

  @Override
  public int getItemCount() {
    return audioFileList.length;
  }

  final View.OnClickListener clickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      int position = (int) view.getTag();
      onPlayerClick.onItemClick(position);
    }
  };

  File getItem(int position) {
    return audioFileList[position];
  }

  void stopAudio(int position) {
    stopRecordingPosition = position;
    playRecordingPosition = -1;
    notifyDataSetChanged();
  }

  void playAudio(int position) {
    playRecordingPosition = position;
    stopRecordingPosition = -1;
    notifyDataSetChanged();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    TextView tvMusicName;
    ImageView ivPlayPause;

    ViewHolder(View itemView) {
      super(itemView);
      tvMusicName = itemView.findViewById(R.id.tvMusicName);
      ivPlayPause = itemView.findViewById(R.id.ivPlayPause);
    }
  }

  interface onPlayerClick {
    void onItemClick(int position);
  }
}
