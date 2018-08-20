package hack.the.wap.musicinstrumentlessoner.myactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;
import hack.the.wap.musicinstrumentlessoner.R;
import hack.the.wap.musicinstrumentlessoner.debug.DebugImageMatch;
import hack.the.wap.musicinstrumentlessoner.model.dto.TemplateDto;
import hack.the.wap.musicinstrumentlessoner.model.dto.TemplatePracticeDto;
import hack.the.wap.musicinstrumentlessoner.myfragment.TemplateFragment;
import hack.the.wap.musicinstrumentlessoner.mylayout.TemplateNegativePracticeLayout;
import hack.the.wap.musicinstrumentlessoner.mylayout.TemplatePositivePracticeLayout;
import hack.the.wap.musicinstrumentlessoner.session.Session;

public class TemplateDetailActivity extends AppCompatActivity {
    private static Session session;
    private TemplateDetailActivity instance;
    private ArrayList<TemplatePracticeDto> templatePractices;
    private TemplateDto mainTemplate;
    private ImageView ivTemplateDetailLayLeftArrow;
    private ImageView ivTemplateDetailLayTeacher;
    private ImageView ivTemplateDetailLayMusician;
    private ImageView ivTemplateDetailActGuide;
    private TextView tvTemplateDetailLayName;
    private TextView tvTemplateDetailLayMusicianName;
    private TextView tvTemplateDetailLayTeacherNameSlot;
    private TextView tvTemplateDetailActGuide;
    private LinearLayout llActTemplateDetail;
    private LinearLayout llTemplateDetailLayTeacherListen;

    /*
        Global Instance
     */
    private int curPractice;
    private String curFile;
    private String rootDir;
    private String filePath;

    {
        session = Session.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_detail);
        ivTemplateDetailLayLeftArrow = findViewById(R.id.ivTemplateDetailLayLeftArrow);
        ivTemplateDetailLayTeacher = findViewById(R.id.ivTemplateDetailLayTeacher);
        ivTemplateDetailLayMusician = findViewById(R.id.ivTemplateDetailLayMusician);
        ivTemplateDetailActGuide = findViewById(R.id.ivTemplateDetailActGuide);
        tvTemplateDetailLayName = findViewById(R.id.tvTemplateDetailLayName);
        tvTemplateDetailLayMusicianName = findViewById(R.id.tvTemplateDetailLayMusicianName);
        tvTemplateDetailLayTeacherNameSlot = findViewById(R.id.tvTemplateDetailLayTeacherNameSlot);
        tvTemplateDetailActGuide = findViewById(R.id.tvTemplateDetailActGuide);
        llActTemplateDetail = findViewById(R.id.llActTemplateDetail);
        llTemplateDetailLayTeacherListen = findViewById(R.id.llTemplateDetailLayTeacherListen);
        instance = this;

        Intent intent = getIntent();
        mainTemplate = (TemplateDto) intent.getSerializableExtra("data");
        Log.e("SAFE", "onCreate >>> " + mainTemplate);

        ivTemplateDetailLayTeacher.setImageResource(DebugImageMatch.getImageFromName(mainTemplate.getOwner().getName()));
        ivTemplateDetailLayMusician.setImageResource(DebugImageMatch.getImageFromName(mainTemplate.getMusician()));
        tvTemplateDetailLayName.setText(mainTemplate.getMusicTitle());
        tvTemplateDetailLayMusicianName.setText(mainTemplate.getMusician());
        tvTemplateDetailLayTeacherNameSlot.setText(getResources().getString(R.string.tempalte_detail_act_teacher_pre)
                + mainTemplate.getOwner().getName());

        templatePractices = mainTemplate.getTemplatePractices();
        for (TemplatePracticeDto dto : templatePractices) {
            if (dto.getFileName() != null) {
                TemplatePositivePracticeLayout atom = new TemplatePositivePracticeLayout(this);
                atom.setCustomAttr(dto);
                atom.getIvTemplatePositivePracticeLayListen().setOnClickListener(v -> {
                    Intent posIntent = new Intent(this, PracticeDetailActivity.class);
                    posIntent.putExtra("data", dto);
                    posIntent.putExtra("main", mainTemplate);
                    startActivity(posIntent);
                });
                atom.getIvTemplatePositivePracticeLayView().setOnClickListener(v -> {
                    Intent posIntent = new Intent(this, PracticeListenActivity.class);
                    posIntent.putExtra("data", dto);
                    posIntent.putExtra("main", mainTemplate);
                    startActivity(posIntent);
                });
                llActTemplateDetail.addView(atom);
            } else {
                TemplateNegativePracticeLayout atom = new TemplateNegativePracticeLayout(this);
                atom.setCustomAttr(dto);
                atom.setOnClickListener(v -> {
                    rootDir = mkdir(dto);
                    filePath = rootDir + getResources().getString(R.string.file_default_name);
                    int requestCode = 0;
                    curPractice = dto.getPracticeId();
                    curFile = filePath;
                    AndroidAudioRecorder.with(this)
                            // Required
                            .setFilePath(filePath)
                            .setColor(R.color.colorPrimaryDark)
                            .setRequestCode(requestCode)

                            // Optional
                            .setSource(AudioSource.MIC)
                            .setChannel(AudioChannel.STEREO)
                            .setSampleRate(AudioSampleRate.HZ_48000)
                            .setAutoStart(true)
                            .setKeepDisplayOn(true)

                            // Start recording
                            .record();
                });
                llActTemplateDetail.addView(atom);
            }
        }

        viewSetListener();
    }

    private void viewSetListener() {
        ivTemplateDetailLayLeftArrow.setOnClickListener(v -> {
            finish();
        });
        llTemplateDetailLayTeacherListen.setOnClickListener(v -> {
            Intent teacherIntent = new Intent(this, TeacherPracticeDetailActivity.class);
            teacherIntent.putExtra("main", mainTemplate);
            startActivity(teacherIntent);
        });
        ivTemplateDetailActGuide.setOnClickListener(v -> {
            Intent guideIntent = new Intent(this, GuideActivity.class);
            guideIntent.putExtra("main", mainTemplate);
            startActivity(guideIntent);
        });
        tvTemplateDetailActGuide.setOnClickListener(v -> {
            Intent guideIntent = new Intent(this, GuideActivity.class);
            guideIntent.putExtra("main", mainTemplate);
            startActivity(guideIntent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                TemplatePracticeDto dto = new TemplatePracticeDto(curPractice, curFile);
                mainTemplate.getTemplatePractices().set(curPractice - 1, dto);
                AndroidAudioConverter.with(this)
                        .setFile(new File(filePath))
                        .setFormat(AudioFormat.MP3)
                        .setCallback(new IConvertCallback() {
                            @Override
                            public void onSuccess(File file) {
                                Log.d("test",file.getName());
                                dto.setFileName(file.getAbsolutePath());
                                dto.setPercent(0);
                                finish();
                                Log.e("TAG", "onSuccess: " + file.getAbsolutePath());

                                //*************upload to server***************
                                uploadFileToServer();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                e.printStackTrace();
                            }
                        })
                        .convert();

                session.getTemplates().get(mainTemplate.getMusicTitle()).getTemplatePractices().set(curPractice - 1, dto);
                session.showAllSession();
            } else if (resultCode == RESULT_CANCELED) {
                // Oops! User has canceled the recording
            }
        }
    }

    private String mkdir(TemplatePracticeDto dto) {
        File dir = new File(getResources().getString(R.string.file_default_dir) + mainTemplate.getMusicTitle());
        if (!dir.isDirectory()) {
            dir.mkdir();
        }
        dir = new File(getResources().getString(R.string.file_default_dir) + mainTemplate.getMusicTitle() + "/" + dto.getPracticeId());
        if (!dir.isDirectory()) {
            dir.mkdir();
        }
        return getResources().getString(R.string.file_default_dir) + mainTemplate.getMusicTitle() + "/" + dto.getPracticeId();
    }

    public void uploadFileToServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int serverResponseCode = 0;
                final String uploadFilePath = "/mnt/sdcard/Music/놀람 교향곡/1/";
                final String uploadFileName = "recorded_audio.mp3";
                String upLoadServerUrl = "http://192.168.43.43:3000/fileUploadServer";
                Log.d("url",upLoadServerUrl);


                String fileName = uploadFilePath+""+uploadFileName;
                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                String attachmentName = "userfile";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1*1024*1024;
                File sourceFile = new File(fileName);

                if(!sourceFile.isFile()){
                    Log.e("uploadFile","Source File not exist:"+uploadFilePath+""+uploadFileName);

                }else{
                    try{
                        //open a URL connection to the Servlet
                        //Log.d("please","done");
                        FileInputStream fileInputStream = new FileInputStream(sourceFile);
                        URL url = new URL(upLoadServerUrl);
                        //Log.d("url",sourceFile+upLoadServerUrl);

                        //open a HTTP connection to the URL
                        conn = (HttpURLConnection)url.openConnection();
                        conn.setDoInput(true);  //allow inputs
                        conn.setDoOutput(true); //allow outputs
                        conn.setUseCaches(false);   //dont use a cached copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection","Keep-Alive");
                        conn.setRequestProperty("Cache-Control", "no-cache");
                        //conn.setRequestProperty("ENCTYPE","multipart/form-data");
                        conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);
                        //conn.setRequestProperty("uploaded_file",fileName);

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens+boundary+lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\""+attachmentName+"\";filename=\""+fileName+"\""+lineEnd);
                        dos.writeBytes(lineEnd);

                        //create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable,maxBufferSize);
                        buffer = new byte[bufferSize];

                        //read file and write it into form
                        bytesRead = fileInputStream.read(buffer,0,bufferSize);

                        while(bytesRead>0){
                            dos.write(buffer,0,bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable,maxBufferSize);
                            bytesRead = fileInputStream.read(buffer,0,bufferSize);
                        }

                        //send multipart form data necessary after file data
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens+boundary+twoHyphens+lineEnd);

                        //responses from the server (code and message)
                        serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn.getResponseMessage();

                        Log.i("uploadFile","HTTP Response is : "+serverResponseMessage+":"+serverResponseCode);

                                        /*if(serverResponseCode==200){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    String msg = "File Upload Completed.\n\n See uploaded file here: \n\n"+uploadFileName;

                                                    //tvFile.setText(msg);
                                                   // Toast.makeText(MainActivity.this, "File Upload Complete", Toast.LENGTH_SHORT).show();
                                                    Log.d("please","File Upload Completed");
                                                }
                                            });
                                        }*/

                        //close the streams
                        fileInputStream.close();
                        dos.flush();
                        dos.close();
                    }catch (MalformedURLException ex){
                        //dialog.dismiss();
                        ex.printStackTrace();

                                        /*runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //tvFile.setText("MalformedURLException Exception: check script url.");
                                                //Toast.makeText(MainActivity.this, "MalfromedURLException", Toast.LENGTH_SHORT).show();
                                                Log.e("please","MalformedURLException");
                                            }
                                        });*/

                        Log.e("Upload file to server","error: "+ex.getMessage(),ex);
                    }catch (Exception e){
                        //dialog.dismiss();
                        e.printStackTrace();

                                        /*runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //tvFile.setText("Got Exception : see logcat");
                                                //Toast.makeText(MainActivity.this, "Got Exception : see logcat", Toast.LENGTH_SHORT).show();
                                            }
                                        });*/

                        Log.e("Upload file exception","Exception : "+e.getMessage(),e);
                    }
                    //dialog.dismiss();
                    //return serverResponseCode;
                }
            }
        }).start();
    }

}