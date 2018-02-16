package com.example.root.trabajofinal;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.root.trabajofinal.Gestores.GestorVideos;
import com.example.root.trabajofinal.Listeners.VideoListener;

import java.text.SimpleDateFormat;

public class VerVideo extends Activity {

    public static final String EXTRA_POSITION = "id_video";
    private int idVideo=-1;
    VideoView simpleVideoView;
    MediaController mediaControls;
    int progreso=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_video);

        int idVideo=getIntent().getIntExtra(EXTRA_POSITION,0);

        GestorVideos gestorVideos=GestorVideos.getGestorVideos(getApplicationContext());
        gestorVideos.getVideo(idVideo, new VideoListener() {
            @Override
            public void onResponseVideo(Multimedia video) {
                // Find your VideoView in your video_main.xml layout
                simpleVideoView = (VideoView) findViewById(R.id.simpleVideoView);

                if (mediaControls == null) {
                    // create an object of media controller class
                    mediaControls = new MediaController(VerVideo.this);
                    mediaControls.setAnchorView(simpleVideoView);
                }
                // set the media controller for video view
                simpleVideoView.setMediaController(mediaControls);
                // set the uri for the video view
                simpleVideoView.setVideoURI(Uri.parse(video.getPath()));
                // start a video
                simpleVideoView.start();
                simpleVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Toast.makeText(getApplicationContext(), "Oops Ah ocurrido un error...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
                        return false;
                    }
                });
                simpleVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                                                                     @Override
                                                                     public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                                                                         Log.e("","progreso: "+i);
                                                                         if(progreso!=i){
                                                                             progreso=i;
                                                                             Toast.makeText(getApplicationContext(),progreso+"%",Toast.LENGTH_SHORT).show();
                                                                         }
                                                                     }
                                                                 }
                        );
                    }
                });

                try{
                    TextView txtTitulo=(TextView)findViewById(R.id.txtTitulo);
                    TextView txtDescripcion=(TextView)findViewById(R.id.txtDescripcion);
                    TextView txtFechaSubida=(TextView)findViewById(R.id.txtFechaSubida);
                    TextView txtFechaCaptura=(TextView)findViewById(R.id.txtFechaCaptura);

                    SimpleDateFormat dt1=new SimpleDateFormat("dd-MM-yyyy");
                    txtTitulo.setText(video.getTitulo());
                    txtDescripcion.setText(video.getDescripcion());
                    txtFechaSubida.setText("Fecha de subida: "+dt1.format(video.getFechaSubida()));
                    txtFechaCaptura.setText("Fecha de filmación: "+dt1.format(video.getFechaCaptura()));

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"error en fecha",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
