package com.zbensoft.e.payment.api.DJEncryption.hsm.factory;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.sun.jna.ptr.PointerByReference;
import com.zbensoft.e.payment.api.DJEncryption.hsm.api.DJHsmAPI;
import com.zbensoft.e.payment.api.DJEncryption.hsm.api.DJHsmErrCode;
import com.zbensoft.e.payment.api.DJEncryption.hsm.thread.MoniterDJEncryptionThread;
import com.zbensoft.e.payment.api.log.DJ_ENCRYPTION_LOG;

public class LoadDeviceFactory {
	
	private static final Logger log = LoggerFactory.getLogger(LoadDeviceFactory.class);

	private static LoadDeviceFactory instance = null;

	private static final Object uniqueLock = new Object();
	private static final Object objectLock = new Object();

	private PointerByReference phDeviceHandle = null;
	private PointerByReference pSessionHandle =null;
	private DJHsmAPI hsmAPI =null;
	private boolean isAlive=true;
	
	boolean isOpenDevice=true;
	boolean isOpenSession=true;
	public static LoadDeviceFactory getInstance() {
		if (null == instance) {
			synchronized (uniqueLock) {
				if (null == instance) {
					instance = new LoadDeviceFactory();
					instance.inti();
					new MoniterDJEncryptionThread("DJEncryptioin-Moniter-Thread").start();
				}
			}
		}
		return instance;
	}
	
	private void inti() {
		hsmAPI = DJHsmAPI.hsmAPI;
		String filePath = getHsmCfgFilePathName();
		if (filePath.length() == 0) {
			DJ_ENCRYPTION_LOG.INFO("Get the config file DJHsmAPI.ini path failed.");
			return;
		}

		// 得到设备句柄
		phDeviceHandle = new PointerByReference();
		if (hsmAPI.SFF_OpenDevice(phDeviceHandle, filePath) != DJHsmErrCode.ERR_OK.getValue()) {
			DJ_ENCRYPTION_LOG.INFO("SFF_OpenDevice failed");
			isOpenDevice=false;
			return;
		}
		// 延迟1秒钟
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			log.error("", e);
		}

		// 得到会话句柄
		pSessionHandle = new PointerByReference();
		int sessionResult = hsmAPI.SFF_OpenSession(phDeviceHandle.getValue(), pSessionHandle);
		if (sessionResult != DJHsmErrCode.ERR_OK.getValue()) {
			DJ_ENCRYPTION_LOG.INFO("SFF_OpenSession failed");
			hsmAPI.SFF_CloseDevice(phDeviceHandle.getValue());
			isOpenSession=false;
			return;
		}

	}
	public void reLoadDevice(){
		synchronized (objectLock) {
			if(hsmAPI!=null){
				close();
			}
			inti();
		}
		
	}

	private String getHsmCfgFilePathName() {
		synchronized (objectLock) {
			try {
				String resourceName=null;
				if(isWin()) {
					resourceName="classpath:DJHsmAPIWin.ini";
				}else{
					resourceName="classpath:DJHsmAPILinux.ini";
				}
				PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
				Resource rc = resolver.getResource(resourceName);
				if (rc.exists() && rc.getFile().exists()) {
					DJ_ENCRYPTION_LOG.INFO("file Path:" + rc.getFile().getAbsolutePath());
					return rc.getFile().getAbsolutePath();
				}
			} catch (IOException e) {
				log.error("getHsmCfgFilePathName get exception", e);
				DJ_ENCRYPTION_LOG.ERROR("getHsmCfgFilePathName get exception", e);
			}
			return null;
		}
	
	}
	
	


	public boolean isWin() {
		synchronized (objectLock) {
			String strOS = System.getProperty("os.name");
			boolean isWin = strOS.indexOf("Windows") >= 0 ? true : false;
			return isWin;
		}
	}
	
	
	public PointerByReference getPhDeviceHandle(){
		synchronized (objectLock) {
			if(phDeviceHandle!=null&&isOpenDevice){
				return phDeviceHandle;
			}
		}
		return null;
	}
	
	public PointerByReference getPSessionHandle(){
		synchronized (objectLock) {
			if(pSessionHandle!=null&&isOpenSession){
				return pSessionHandle;
			}
		}
		return null;
	}
	
	
	public DJHsmAPI getHsmAPI(){
		synchronized (objectLock) {
			if(hsmAPI!=null){
				return hsmAPI;
			}
		}
		return null;
	}
	public boolean getIsAlive(){
		synchronized (objectLock) {
			return isAlive;
		}
	}
	
	public void setIsAlive(boolean flag){
		synchronized (objectLock) {
			 isAlive=flag;
		}
	}
	
	public void close(){
		synchronized (objectLock) {
			if(hsmAPI!=null&&phDeviceHandle!=null&&pSessionHandle!=null){
				try {
					//关闭会话句柄
					hsmAPI.SFF_CloseSession(pSessionHandle.getValue());
					
					//关闭设备句柄
					hsmAPI.SFF_CloseDevice(phDeviceHandle.getValue());
				} catch (Exception e) {
					log.error("close get exception", e);
					DJ_ENCRYPTION_LOG.ERROR("close get exception", e);
				}
			}
		}
	}


}
