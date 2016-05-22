package voiceRecordUtils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

public class VoiceRecordUtils {

	private int frequency = 11025;// �����ʣ�ÿ��11025��������
	private int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO; // MONOΪ������
	private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;// �����ֶȣ�һ��������16���أ������2���ֽ�
	private String voiceName = new String("voiceRecordOne.pcm"); // ¼���ļ�����
	private final int audioSource = MediaRecorder.AudioSource.MIC; // Microphone(��˷�)

	private File file;

	private int bufferSize = 0;
	private AudioRecord audioRecord;
	private boolean isRecording = false; // �����ж��Ƿ���¼��

	public void initvoiceRecord(String voiceNameDefine) {

		file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/AudioRecorder/" + voiceNameDefine);

		// ɾ��֮ǰ�ļ�¼�������´���
		if (file.exists()) {
			file.delete();
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// ��Ҫ����С¼�������С
		bufferSize = AudioRecord.getMinBufferSize(frequency,
				channelConfiguration, audioEncoding);
		audioRecord = new AudioRecord(audioSource, frequency,
				channelConfiguration, audioEncoding, bufferSize);

	}

	public void startRecord() {
		new voiceRecordTask().execute();

	}

	public void pauseRecord() {
		isRecording = false;
	}

	public void continueRecord() {
		new voiceRecordTask().execute();
	}

	public void endRecord() {

		isRecording = false;

	}

	public void playRecord() {
		play();
	}

	public class voiceRecordTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			if (null == audioRecord) {
				initvoiceRecord(voiceName);
			}

			// ʹ�����������,������ͣ�����¼��ʱ����Դ�ļ�׷������
			try {
				RandomAccessFile randomAccessFile = new RandomAccessFile(file,
						"rw");
				// �������� why bufferSize/4 ???
				// byte[] byteArray = new byte[bufferSize/4];
				short[] shortArray = new short[bufferSize];

				// ��ʼ¼����Ƶ
				audioRecord.startRecording();

				// �ж��Ƿ�����¼��
				isRecording = true;
				while (true == isRecording) {

					int bufferReadResult = audioRecord.read(shortArray, 0,
							bufferSize);
					// ��ԭ�ļ���׷������
					randomAccessFile.seek(randomAccessFile.length());
					for (int i = 0; i < bufferReadResult; i++) {
						randomAccessFile.writeShort(shortArray[i]);
					}

					/*
					 * audioRecord.read(byteArray, 0, byteArray.length);
					 * 
					 * //��ԭ�ļ���׷������
					 * randomAccessFile.seek(randomAccessFile.length());
					 * randomAccessFile.write(byteArray, 0, byteArray.length);
					 */
				}

				// ֹͣ¼��
				audioRecord.stop();
				// �رսڵ���
				randomAccessFile.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

	}

	/**
	 * ����¼��
	 */
	public void play() {
		int voiceLength = (int) (file.length() / 2); // ???
		short[] music = new short[voiceLength];

		try {
			// �ֽ���
			InputStream inputStream = new FileInputStream(file);
			// ������
			BufferedInputStream bis = new BufferedInputStream(inputStream);
			// ���������ô��������ṩ�˸����read��������InputStream��ֻ��read();
			DataInputStream dataInputStream = new DataInputStream(bis);

			int i = 0;
			while (dataInputStream.available() > 0) {
				music[i] = dataInputStream.readShort();
				i++;
			}

			// �ر�������
			dataInputStream.close();

			AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					11025, AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, voiceLength * 2,
					AudioTrack.MODE_STREAM);
			// ��ʼ����
			audioTrack.play();

			// write the music buffer to the AudioTrack object
			audioTrack.write(music, 0, voiceLength);

			audioTrack.stop();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("AudioTrack", "Playback Failed");
			e.printStackTrace();
		}

	}

	public String getVoiceName() {
		return voiceName;
	}

	public void setVoiceName(String voiceName) {
		this.voiceName = voiceName;
	}

}
