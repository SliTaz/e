package com.zbensoft.e.payment.api.quartz.job.clap.firstImport.thread;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.quartz.job.clap.firstImport.factory.RecordFactory;
import com.zbensoft.e.payment.api.vo.clap.ClapBuyerHeader;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.DateUtil;



public class ReadThread extends Thread {
	
	private static final Logger log = LoggerFactory.getLogger(ReadThread.class);

	public ReadThread(String name) {
		super(name);
	}
	
	@Override
	public void run() {
		try {
//			File file = new File("/root/zhao/clap-data/con_clap4.csv");   
			String JOB_CLAP_DOWNLOAD_FILE_PATH = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_DOWNLOAD_FILE_PATH);
			String BYER_FILE_PREFIX = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_BYER_FILE_PREFIX);
			String BYER_FILE_DATEFORMATE = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_BYER_FILE_DATEFORMATE);
			int JOB_CLAP_SFTP_FILE_DAY_BEFORE =SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CLAP_SFTP_FILE_DAY_BEFORE);
			String buyerFileName = BYER_FILE_PREFIX+ DateUtil.convertDateToString(DateUtil.addDate(new Date(), JOB_CLAP_SFTP_FILE_DAY_BEFORE, 3), BYER_FILE_DATEFORMATE)+".txt";
			
			File file = new File(JOB_CLAP_DOWNLOAD_FILE_PATH + buyerFileName);  
			BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));    
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis,"utf-8"),5*1024*1024);// 用5M的缓冲读取文本文件  
			  
			String line = "";
			line = reader.readLine();
			ClapBuyerHeader header=new ClapBuyerHeader(line);
			RecordFactory.getInstance().setTotal(Double.valueOf(header.getTotalFileRecord()));
			while((line = reader.readLine()) != null){
				RecordFactory.getInstance().add(line);
				if(RecordFactory.getInstance().isLimit(20000)){
					Thread.sleep(1000);
				}
			}
		} catch (FileNotFoundException e) {
			log.error("", e);
		} catch (UnsupportedEncodingException e) {
			log.error("", e);
		} catch (IOException e) {
			log.error("", e);
		} catch (InterruptedException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}
		
	}
}
