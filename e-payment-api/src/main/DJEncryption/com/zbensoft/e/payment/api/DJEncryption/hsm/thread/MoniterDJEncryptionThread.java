package com.zbensoft.e.payment.api.DJEncryption.hsm.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.zbensoft.e.payment.api.DJEncryption.hsm.api.DJHsmAPI;
import com.zbensoft.e.payment.api.DJEncryption.hsm.api.DJHsmErrCode;
import com.zbensoft.e.payment.api.DJEncryption.hsm.api.DJHsmDef.RP_MSG_3D;
import com.zbensoft.e.payment.api.DJEncryption.hsm.api.DJHsmDef.RQ_MSG_3C;
import com.zbensoft.e.payment.api.DJEncryption.hsm.factory.LoadDeviceFactory;
import com.zbensoft.e.payment.api.log.DJ_ENCRYPTION_LOG;

public class MoniterDJEncryptionThread extends Thread {
	
	private static final Logger log = LoggerFactory.getLogger(MoniterDJEncryptionThread.class);

	
	public MoniterDJEncryptionThread (String name){
		super(name);
	}

	@Override
	public void run() {
		while(true){
			if (!LoadDeviceFactory.getInstance().getIsAlive()) {
				boolean status = checkDJEncryptionStatus();
				if (!status) {
					LoadDeviceFactory.getInstance().setIsAlive(false);
					LoadDeviceFactory.getInstance().reLoadDevice();
					DJ_ENCRYPTION_LOG.INFO("[DJEncryption moniter] Try to reload device");
				} else {
					LoadDeviceFactory.getInstance().setIsAlive(true);
					DJ_ENCRYPTION_LOG.INFO("[DJEncryption moniter] Try to reload device");
				}

			}
			try {
				Thread.sleep(5*1000);
			} catch (InterruptedException e) {
				log.error("MoniterDJEncryptionThread sleep Error", e);
				DJ_ENCRYPTION_LOG.ERROR("MoniterDJEncryptionThread sleep Error", e);
			}
			
		}
	}
	
	
	private boolean checkDJEncryptionStatus() {  
	    boolean deviceStatus = false;  
	  
	    final ExecutorService exec = Executors.newFixedThreadPool(1);  
	    Callable<String> call = new Callable<String>() {  
	        public String call() throws Exception {  
	        	PointerByReference pointerByReference = LoadDeviceFactory.getInstance().getPSessionHandle();
	    		DJHsmAPI hsmAPI = LoadDeviceFactory.getInstance().getHsmAPI();
	    		if (pointerByReference != null) {
	    			long startTime=System.currentTimeMillis();
	    			String testMessagae="test u!";
	    			Pointer pSessionHandle = pointerByReference.getValue();
	    			RQ_MSG_3C.ByReference msg_3C = new RQ_MSG_3C.ByReference();
	    			RP_MSG_3D.ByReference msg_3D = new RP_MSG_3D.ByReference();

	    			msg_3C.setsHashAlgFlag("06".getBytes());
	    			msg_3C.setsDataLen(lengthFormat(testMessagae.length()).getBytes());
	    			msg_3C.setsData(testMessagae.getBytes());
	    			msg_3C.setsSeparator(";".getBytes());
	    			int iRet = hsmAPI.SFF_Digest(pSessionHandle, msg_3C, msg_3D);
	    			long useTime=System.currentTimeMillis()-startTime;
	    		
	    			if (!isCallSucc("DJhs256Encryption", iRet)) {
	    				DJ_ENCRYPTION_LOG.ERROR("[DJEncryption moniter] call hsmAPI failed, return null");
	    				return "false";
	    			}
	    			String result="6b93ceba3cc2fa38d094f8a68e060af29a7cee458fb4f646356ca90baa1bc5a1";
	    			if(!result.equals(bytesTo16(msg_3D.getsHash()))){
	    				DJ_ENCRYPTION_LOG.ERROR("[DJEncryption moniter] call hsmAPI failed, result not Right");
	    				return "false";
	    			}
	    			if(useTime>80){
						DJ_ENCRYPTION_LOG.ERROR("DJEncryptionUtil use time over 80ms");
						return "false";
					}
	    		} else {
	    			DJ_ENCRYPTION_LOG.ERROR("[DJEncryption moniter] get pointerByReference is null");
	    			return "false";
	    		}
	    		
	            return "true";  
	        }  
	    };  
	  
	    try {  
	        Future<String> future = exec.submit(call);  
	        String obj = future.get(2000, TimeUnit.MILLISECONDS);   
	        deviceStatus = Boolean.parseBoolean(obj);  
	        DJ_ENCRYPTION_LOG.INFO("[DJEncryption moniter] the return value from call is :" + obj);
	    } catch (TimeoutException ex) {  
			log.error("[DJEncryption moniter] test call time out",ex);
	    	DJ_ENCRYPTION_LOG.ERROR("[DJEncryption moniter] test call time out",ex);
	        deviceStatus = false;  
	    } catch (Exception e) {  
			log.error("[DJEncryption moniter] Unknow Exception",e);
	    	DJ_ENCRYPTION_LOG.ERROR("[DJEncryption moniter] Unknow Exception",e);
	        deviceStatus = false;  
	    }  
	    exec.shutdown();  
	  
	    return deviceStatus;  
	}  
	
	private String lengthFormat(int length) {
		int length_int = String.valueOf(length).length();
		StringBuffer stringBuffer = new StringBuffer();

		for (int i = 0; i < 4 - length_int; i++) {
			stringBuffer.append("0");
		}
		stringBuffer.append(String.valueOf(length));
		return stringBuffer.toString();
	}

	private boolean isCallSucc(String hsmAPIName, int retCode) {
		try {
			if (retCode != DJHsmErrCode.ERR_OK.getValue()) {
				return false;
			}
		} catch (Exception e) {
			log.error("[DJEncryption moniter] isCallSucc get exception", e);
			DJ_ENCRYPTION_LOG.ERROR("[DJEncryption moniter] isCallSucc get exception", e);
			return false;
		}
		return true;
	}
	
	private String bytesTo16(byte[] datas) {
		StringBuilder stringBuilder = new StringBuilder("");
		for (int i = 0; i < datas.length; i++) {

			int v = datas[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);

		}
		return stringBuilder.toString();
	}
}
