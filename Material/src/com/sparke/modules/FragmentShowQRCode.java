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

	//控件
	private FrameLayout button_create_qrcode_image_store;
	private TextView button_create_qrcode_image_store_text;
	private FrameLayout mBack;
	private ImageView imageView_create_qrcode_image;
	private TextView textView_create_qrcode_show_path;
	
	//要生成二维码图片的数据
	public String qrcode_data;
	
	//生成的二维码图片
	Bitmap qrBitmap;
	
	//二维码图片名称
	private String qrImage_name = null;
	
	//二维码图片绝对路径
	private String image_path;

	/**
	 * 创建fragment时，传入数据为二维码图片的名称（即图片名称：xxx.png）和二维码数据
	 * @param image_name
	 * @param qrcode_data
	 * @return
	 */
	public static FragmentShowQRCode newInstance(String image_name,String qrcode_data) {

		FragmentShowQRCode fragment = new FragmentShowQRCode();
		Bundle args = new Bundle();
		
		//把传递参数（二维码名称和二维码数据）存入args中
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
			//获取传递进来的二维码数据
			qrcode_data = getArguments().getString("qrcode_data");
			
			//获取传进来的二维码图片名称
			qrImage_name = getArguments().getString("image_name");
		}
		
		
		//控件
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
		
		//调用EncodingHandler.createQRCode函数，生成二维码，传入数据为：字符串数据（qrcode_data），图片大小(256)
		//返回Bitmap
		try {
			qrBitmap = EncodingHandler.createQRCode(qrcode_data, 256);
			imageView_create_qrcode_image.setImageBitmap(qrBitmap);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//点击本地存储按钮
		button_create_qrcode_image_store.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//本地存储方法
				SavePicInLocal(qrBitmap);
			}
		});
		return rootView;
	}
	
	/**
     * 获得碎片所在的上下文
     * @return
     */
    private Context getContext() {
    	return getActivity();
    }
    
    /**
     * 把二维码图片存储在本地
     * @param bitmap
     */
    public void SavePicInLocal(Bitmap bitmap){
    	FileOutputStream fos = null;
    	BufferedOutputStream bos = null;
    	ByteArrayOutputStream baos = null;
    	try {
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			
			//字节数组输出流转成字节数组
			byte[] byteArray = baos.toByteArray();
			
			//二维码图片路径和名称
			//外部路径+图片名称
			File image_File = new File(getDir(), qrImage_name + ".png");
			
			//二维码的存储绝对路径（外部路径+图片名称）
			image_path = image_File.getAbsolutePath();
			
			if (!image_File.exists()) {
				image_File.createNewFile();
			}
			
			//将字节数组写入刚刚创建的图片文件中
			fos = new FileOutputStream(image_File);
			bos = new BufferedOutputStream(fos);
			bos.write(byteArray);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Toast.makeText(getContext(), "图片保存出错", Toast.LENGTH_SHORT).show();
		}finally{
			//关闭各种输出流
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
			button_create_qrcode_image_store_text.setText("保存成功");
			button_create_qrcode_image_store.setClickable(false);
			textView_create_qrcode_show_path.setText("保存至:"+image_path);
		}
    }
    
    /**
     * 获取图片存储的路径
     * @return file
     */
    public File getDir(){
    	File dir = new File(Environment.getExternalStorageDirectory(), "地大人二维码");
    	if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
    	
    }
}
