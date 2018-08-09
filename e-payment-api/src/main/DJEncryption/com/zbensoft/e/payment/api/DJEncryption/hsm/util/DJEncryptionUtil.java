package com.zbensoft.e.payment.api.DJEncryption.hsm.util;

import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.zbensoft.e.payment.api.DJEncryption.hsm.api.DJHsmAPI;
import com.zbensoft.e.payment.api.DJEncryption.hsm.api.DJHsmDef.RP_MSG_3D;
import com.zbensoft.e.payment.api.DJEncryption.hsm.api.DJHsmDef.RQ_MSG_3C;
import com.zbensoft.e.payment.api.DJEncryption.hsm.api.DJHsmErrCode;
import com.zbensoft.e.payment.api.DJEncryption.hsm.factory.LoadDeviceFactory;
import com.zbensoft.e.payment.api.log.DJ_ENCRYPTION_LOG;

public class DJEncryptionUtil {
	
	private static final Logger log = LoggerFactory.getLogger(DJEncryptionUtil.class);

	
	public static String DJhs256Encryption(String messageData) throws IOException {
		PointerByReference pointerByReference = LoadDeviceFactory.getInstance().getPSessionHandle();
		DJHsmAPI hsmAPI = LoadDeviceFactory.getInstance().getHsmAPI();
		boolean isAlive=LoadDeviceFactory.getInstance().getIsAlive();
		if (isAlive) {//判读机器是否可用
			if (pointerByReference != null) {//判断指针是否为空
				long startTime=System.currentTimeMillis();
				Pointer pSessionHandle = pointerByReference.getValue();
				RQ_MSG_3C.ByReference msg_3C = new RQ_MSG_3C.ByReference();
				RP_MSG_3D.ByReference msg_3D = new RP_MSG_3D.ByReference();

				msg_3C.setsHashAlgFlag("06".getBytes());
				msg_3C.setsDataLen(lengthFormat(messageData.length()).getBytes());
				msg_3C.setsData(messageData.getBytes());
				msg_3C.setsSeparator(";".getBytes());
				int iRet = hsmAPI.SFF_Digest(pSessionHandle, msg_3C, msg_3D);
				long useTime=System.currentTimeMillis()-startTime;
				if (!isCallSucc("DJhs256Encryption", iRet)) {//判断返回码是否正确
					DJ_ENCRYPTION_LOG.ERROR("DJEncryptionUtil call hsmAPI failed, return null");
					 LoadDeviceFactory.getInstance().setIsAlive(false);
					return DigestUtils.sha256Hex(messageData);
				}
				if(useTime>80){//判断是否超时
					DJ_ENCRYPTION_LOG.ERROR("DJEncryptionUtil use time over 80ms");
					LoadDeviceFactory.getInstance().setIsAlive(false);
				}
				byte[] dataMWByte = msg_3D.getsHash();
				return bytesTo16(dataMWByte);
			} else {
				LoadDeviceFactory.getInstance().setIsAlive(false);
				DJ_ENCRYPTION_LOG.ERROR("DJEncryptionUtil get pointerByReference is null, tye to ues soft encryption");
			}
		}else{
			DJ_ENCRYPTION_LOG.ERROR("DJEncryption is not alive, tye to ues soft encryption");
		}
		return DigestUtils.sha256Hex(messageData);

	}
	

	private static String lengthFormat(int length) {
		int length_int = String.valueOf(length).length();
		StringBuffer stringBuffer = new StringBuffer();

		for (int i = 0; i < 4 - length_int; i++) {
			stringBuffer.append("0");
		}
		stringBuffer.append(String.valueOf(length));
		return stringBuffer.toString();
	}

	public static boolean isCallSucc(String hsmAPIName, int retCode) {
		try {
			if (retCode != DJHsmErrCode.ERR_OK.getValue()) {
				DJ_ENCRYPTION_LOG.ERROR("DJEncryptionUtil Called response error, ErrCode:"+retCode);
				return false;
			}
		} catch (Exception e) {
			log.error("DJEncryptionUtil isCallSucc get exception", e);
			DJ_ENCRYPTION_LOG.ERROR("DJEncryptionUtil isCallSucc get exception", e);
		}
		return true;
	}

	public static String bytesTo16(byte[] datas) {
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
