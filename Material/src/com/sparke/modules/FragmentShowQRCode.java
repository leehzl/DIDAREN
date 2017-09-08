package com.sparke.modules;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.material.R;
import com.google.zxing.WriterException;
import com.zxing.encoding.EncodingHandler;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentShowQRCode extends Fragment {

	//�ؼ�
	private FrameLayout button_create_qrcode_image_store;
	private TextView button_create_qrcode_image_store_text;
	private FrameLayout mBack;
	private ImageView imageView_create_qrcode_image;
	private TextView textView_create_qrcode_show_path;
	
	//Ҫ���ɶ�ά��ͼƬ������
	public String qrcode_data;
	
	//���ɵĶ�ά��ͼƬ
	Bitmap qrBitmap;
	
	//��ά��ͼƬ����
	private String qrImage_name = null;
	
	//��ά��ͼƬ����·��
	private String image_path;

	/**
	 * ����fragmentʱ����������Ϊ��ά��ͼƬ�����ƣ���ͼƬ���ƣ�xxx.png���Ͷ�ά������
	 * @param image_name
	 * @param qrcode_data
	 * @return
	 */
	public static FragmentShowQRCode newInstance(String image_name,String qrcode_data) {

		FragmentShowQRCode fragment = new FragmentShowQRCode();
		Bundle args = new Bundle();
		
		//�Ѵ��ݲ�������ά�����ƺͶ�ά�����ݣ�����args��
		args.putString("image_name", image_name);
		args.putString("qrcode_data", qrcode_data);
		fragment.setArguments(args);
		return fragment;
	}

	public FragmentShowQRCode() {
	}
	

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = null;
		rootView = inflater.inflate(R.layout.fragment_create_qrcode_image,
				container, false);
		
		if(getArguments() != null){
			//��ȡ���ݽ����Ķ�ά������
			qrcode_data = getArguments().getString("qrcode_data");
			
			//��ȡ�������Ķ�ά��ͼƬ����
			qrImage_name = getArguments().getString("image_name");
		}
		
		
		//�ؼ�
		button_create_qrcode_image_store = (FrameLayout)rootView.findViewById(R.id.button_create_qrcode_image_store);
		button_create_qrcode_image_store_text = (TextView) rootView.findViewById(R.id.button_create_qrcode_image_store_text);
		imageView_create_qrcode_image = (ImageView)rootView.findViewById(R.id.imageView_create_qrcode_image);
		textView_create_qrcode_show_path = (TextView)rootView.findViewById(R.id.textView_create_qrcode_show_path);
		
		mBack = (FrameLayout) rootView.findViewById(R.id.button_create_qrcode_image_back);
		mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fragmentManager = getActivity()
						.getSupportFragmentManager();
				FragmentTransaction transaction = fragmentManager
						.beginTransaction();
				transaction.replace(R.id.container,
						FragmentCreateQRCode.newInstance(1));
				transaction.commit();
			}
		});
		
		//����EncodingHandler.createQRCode���������ɶ�ά�룬��������Ϊ���ַ������ݣ�qrcode_data����ͼƬ��С(256)
		//����Bitmap
		try {
			qrBitmap = EncodingHandler.createQRCode(qrcode_data, 256);
			imageView_create_qrcode_image.setImageBitmap(qrBitmap);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//������ش洢��ť
		button_create_qrcode_image_store.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//���ش洢����
				SavePicInLocal(qrBitmap);
			}
		});
		return rootView;
	}
	
	/**
     * �����Ƭ���ڵ�������
     * @return
     */
    private Context getContext() {
    	return getActivity();
    }
    
    /**
     * �Ѷ�ά��ͼƬ�洢�ڱ���
     * @param bitmap
     */
    public void SavePicInLocal(Bitmap bitmap){
    	FileOutputStream fos = null;
    	BufferedOutputStream bos = null;
    	ByteArrayOutputStream baos = null;
    	try {
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			
			//�ֽ����������ת���ֽ�����
			byte[] byteArray = baos.toByteArray();
			
			//��ά��ͼƬ·��������
			//�ⲿ·��+ͼƬ����
			File image_File = new File(getDir(), qrImage_name + ".png");
			
			//��ά��Ĵ洢����·�����ⲿ·��+ͼƬ���ƣ�
			image_path = image_File.getAbsolutePath();
			
			if (!image_File.exists()) {
				image_File.createNewFile();
			}
			
			//���ֽ�����д��ոմ�����ͼƬ�ļ���
			fos = new FileOutputStream(image_File);
			bos = new BufferedOutputStream(fos);
			bos.write(byteArray);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Toast.makeText(getContext(), "ͼƬ�������", Toast.LENGTH_SHORT).show();
		}finally{
			//�رո��������
			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
			button_create_qrcode_image_store_text.setText("����ɹ�");
			button_create_qrcode_image_store.setClickable(false);
			textView_create_qrcode_show_path.setText("������:"+image_path);
		}
    }
    
    /**
     * ��ȡͼƬ�洢��·��
     * @return file
     */
    public File getDir(){
    	File dir = new File(Environment.getExternalStorageDirectory(), "�ش��˶�ά��");
    	if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
    	
    }
}
