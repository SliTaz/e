package com.zbensoft.e.payment.api.DJEncryption.hsm.api;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.*;
import com.sun.jna.Pointer;
import com.zbensoft.e.payment.api.DJEncryption.hsm.api.DJHsmDef.*;

public interface DJHsmAPI extends Library {
//	DJHsmAPI hsmAPI = (DJHsmAPI)Native.loadLibrary("DJHsmAPI",DJHsmAPI.class);//Linux 
	DJHsmAPI hsmAPI = (DJHsmAPI)Native.loadLibrary("DJHsmAPI",DJHsmAPI.class);//Windows
	
	int SFF_OpenDevice(PointerByReference phDeviceHandle, String pucCfgFilePath);
	int SFF_CloseDevice(Pointer hDeviceHandle);	
	int SFF_OpenSession(Pointer hDeviceHandle, PointerByReference phSessionHandle);
	int SFF_CloseSession(Pointer phSessionHandle);
	int SFF_GetDeviceInfo(Pointer hSessionHandle, SFF_FIN_DEVICEINFO.ByReference pstDeviceInfo);
	int SFF_GetDeviceState(Pointer hSessionHandle, SFF_FIN_DEVICESTAT.ByReference pstDeviceStat);
	
	// -------------------------------------- 新增三个接口 -------------------------------------
	int SFF_OpenSessionSpecify(Pointer hDeviceHandle, int groupIndex, int encrytorIndex, PointerByReference phSessionHandle);
	int SFF_SyncKey(Pointer hSessionHandle, int srcGroupIndex, int srcEncrytorIndex, int desGroupIndex, int desEncrytorIndex, byte keyType);
	int SFF_BackupKey(Pointer hSessionHandle, int groupIndex, int encrytorIndex, byte keyType, String pBackFile);
	
	// -------------------------------------- 金融 IC 卡 -------------------------------------- 
	// 密钥管理
	int SFF_GenRandomKey(Pointer hSessionHandle, RQ_MSG_KR.ByReference pMsg_KR, RP_MSG_KS.ByReference pMsg_KS);
	int SFF_GenDerivateKey(Pointer hSessionHandle, RQ_MSG_KD.ByReference pMsg_KD, RP_MSG_KE.ByReference pMsg_KE);
	int SFF_ExportKeyWithTransKey(Pointer hSessionHandle, RQ_MSG_KH.ByReference pMsg_KH, RP_MSG_KI.ByReference pMsg_KI);
	int SFF_ImportKeyWithTransKey(Pointer hSessionHandle, RQ_MSG_KI.ByReference pMsg_KI, RP_MSG_KJ.ByReference pMsg_KJ);
	int SFF_ExportKeyWithProtectKey(Pointer hSessionHandle, RQ_MSG_SH.ByReference pMsg_SH, RP_MSG_SI.ByReference pMsg_SI);
	int SFF_ImportKeyWithProtectKey(Pointer hSessionHandle, RQ_MSG_SI.ByReference pMsg_SI, RP_MSG_SJ.ByReference pMsg_SJ);
	int SFF_GetSymKeyInfo(Pointer hSessionHandle, RQ_MSG_KG.ByReference pMsg_KG, RP_MSG_KH.ByReference pMsg_KH);
	int SFF_DeleteKey(Pointer hSessionHandle, RQ_MSG_KF.ByReference pMsg_KF);
	
    // GP规范发卡
	int SFF_KMCExportCardKey(Pointer hSessionHandle, RQ_MSG_G1.ByReference pMsg_G1, RP_MSG_G2.ByReference pMsg_G2);
	int SFF_KMCExportWorkKey(Pointer hSessionHandle, RQ_MSG_G2.ByReference pMsg_G2, RP_MSG_G3.ByReference pMsg_G3);
	int SFF_KMCEncSensitiveData(Pointer hSessionHandle, RQ_MSG_G3.ByReference pMsg_G3, RP_MSG_G4.ByReference pMsg_G4);
	int SFF_KMCEncData(Pointer hSessionHandle, RQ_MSG_G4.ByReference pMsg_G4, RP_MSG_G5.ByReference pMsg_G5);
	int SFF_KMCCalcCMAC(Pointer hSessionHandle, RQ_MSG_G5.ByReference pMsg_G5, RP_MSG_G6.ByReference pMsg_G6);
	int SFF_KMCVerifyRMAC(Pointer hSessionHandle, RQ_MSG_G6.ByReference pMsg_G6, RP_MSG_G7.ByReference pMsg_G7);
	int SFF_ExternalAuth(Pointer hSessionHandle, RQ_MSG_G7.ByReference pMsg_G7, RP_MSG_G8.ByReference pMsg_G8);
	int SFF_KMCExportSessionKey(Pointer hSessionHandle, RQ_MSG_G8.ByReference pMsg_G8, RP_MSG_G9.ByReference pMsg_G9);
	int SFF_KMCExportRSAKey(Pointer hSessionHandle, RQ_MSG_GF.ByReference pMsg_GF, RP_MSG_GG.ByReference pMsg_GG);
	int SFF_KMCExportSM2Key(Pointer hSessionHandle, RQ_MSG_G0.ByReference pMsg_G0, RP_MSG_G1.ByReference pMsg_G1);
	        
	// PBOC/EMV规范交易功能
	int SFF_PBOCVerifyARQC(Pointer hSessionHandle, RQ_MSG_K6.ByReference pMsg_K6, RP_MSG_K7.ByReference pMsg_K7);
	int SFF_PBOCScriptEncrypt(Pointer hSessionHandle, RQ_MSG_K2.ByReference pMsg_K2, RP_MSG_K3.ByReference pMsg_K3);
	int SFF_PBOCScriptMAC(Pointer hSessionHandle, RQ_MSG_K4.ByReference pMsg_K4, RP_MSG_K5.ByReference pMsg_K5);
	        
	// 数据加解密
	int SFF_EncryptData(Pointer hSessionHandle, RQ_MSG_S3.ByReference pMsg_S3, RP_MSG_S4.ByReference pMsg_S4);
	int SFF_DecryptData(Pointer hSessionHandle, RQ_MSG_S4.ByReference pMsg_S4, RP_MSG_S5.ByReference pMsg_S5);
	int SFF_ConvertEncryptData(Pointer hSessionHandle, RQ_MSG_S5.ByReference pMsg_S5, RP_MSG_S6.ByReference pMsg_S6);
	        
	// 数据MAC运算
	int SFF_CalcMAC(Pointer hSessionHandle, RQ_MSG_D0.ByReference pMsg_D0, RP_MSG_D1.ByReference pMsg_D1);
	int SFF_VerifyMAC(Pointer hSessionHandle, RQ_MSG_D1.ByReference pMsg_D1, RP_MSG_D2.ByReference pMsg_D2);
	int SFF_CalcMACCommon(Pointer hSessionHandle, RQ_MSG_S0.ByReference pMsg_S0, RP_MSG_S1.ByReference pMsg_S1);
	int SFF_CalcHMAC(Pointer hSessionHandle, RQ_MSG_LR.ByReference pMsg_LR, RP_MSG_LS.ByReference pMsg_LS);
	        
	// 数据摘要
	int SFF_Digest(Pointer hSessionHandle, RQ_MSG_3C.ByReference pMsg_3C, RP_MSG_3D.ByReference pMsg_3D);
	int SFF_DigestInit(Pointer hSessionHandle, RQ_MSG_H1.ByReference pMsg_H1, RP_MSG_H2.ByReference pMsg_H2);
	int SFF_DigestUpdate(Pointer hSessionHandle, RQ_MSG_H2.ByReference pMsg_H2, RP_MSG_H3.ByReference pMsg_H3);
	int SFF_DigestFinal(Pointer hSessionHandle, RQ_MSG_H3.ByReference pMsg_H3, RP_MSG_H4.ByReference pMsg_H4);
	
	//其他
	int SFF_GenRandom(Pointer hSessionHandle, RQ_MSG_CR.ByReference pMsg_CR, RP_MSG_CS.ByReference pMsg_CS);

	// -------------------------------------- 雷卡racal兼容主机指令	 --------------------------------------       
	// 工作密钥管理
	int SFF_GenWorkKey(Pointer hSessionHandle, RQ_MSG_A0.ByReference pMsg_A0, RP_MSG_A1.ByReference pMsg_A1);
	int SFF_ComposeKey(Pointer hSessionHandle, RQ_MSG_A4.ByReference pMsg_A4, RP_MSG_A5.ByReference pMsg_A5);
	int SFF_ImportKey(Pointer hSessionHandle, RQ_MSG_A6.ByReference pMsg_A6, RP_MSG_A7.ByReference pMsg_A7);
	int SFF_ExportKey(Pointer hSessionHandle, RQ_MSG_A8.ByReference pMsg_A8, RP_MSG_A9.ByReference pMsg_A9);
	int SFF_GenZPK(Pointer hSessionHandle, RQ_MSG_IA.ByReference pMsg_IA, RP_MSG_IB.ByReference pMsg_IB);
	int SFF_ZPKEncZMK2LMK(Pointer hSessionHandle, RQ_MSG_FA.ByReference pMsg_FA, RP_MSG_FB.ByReference pMsg_FB);
	int SFF_ZPKEncLMK2ZMK(Pointer hSessionHandle, RQ_MSG_GC.ByReference pMsg_GC, RP_MSG_GD.ByReference pMsg_GD);
	int SFF_GenZEKorZAK(Pointer hSessionHandle, RQ_MSG_FI.ByReference pMsg_FI, RP_MSG_FJ.ByReference pMsg_FJ);
	int SFF_ZEKorZAKEncZMK2LMK(Pointer hSessionHandle, RQ_MSG_FK.ByReference pMsg_FK, RP_MSG_FL.ByReference pMsg_FL);
	int SFF_ZEKorZAKEncLMK2ZMK(Pointer hSessionHandle, RQ_MSG_FM.ByReference pMsg_FM, RP_MSG_FN.ByReference pMsg_FN);
	int SFF_GenTMKorTPKorPVK(Pointer hSessionHandle, RQ_MSG_HC.ByReference pMsg_HC, RP_MSG_HD.ByReference pMsg_HD);
	int SFF_GenTAK(Pointer hSessionHandle, RQ_MSG_HA.ByReference pMsg_HA, RP_MSG_HB.ByReference pMsg_HB);
	       
	// 消息验证MAC运算
	int SFF_CalcMACWithTAK(Pointer hSessionHandle, RQ_MSG_MA.ByReference pMsg_MA, RP_MSG_MB.ByReference pMsg_MB);
	int SFF_VerifyMACWithTAK(Pointer hSessionHandle, RQ_MSG_MC.ByReference pMsg_MC);
	int SFF_CalcMACWithZAK(Pointer hSessionHandle, RQ_MSG_MQ.ByReference pMsg_MQ, RP_MSG_MR.ByReference pMsg_MR);
	int SFF_CalcCBCMACWithZPK(Pointer hSessionHandle, RQ_MSG_UQ.ByReference pMsg_UQ, RP_MSG_UR.ByReference pMsg_UR);
	int SFF_CalcCBCMACWithZAKTAK(Pointer hSessionHandle, RQ_MSG_MU.ByReference pMsg_MU, RP_MSG_MV.ByReference pMsg_MV);
	int SFF_CalcX9MACWithZAKTAK(Pointer hSessionHandle, RQ_MSG_MS.ByReference pMsg_MS, RP_MSG_MT.ByReference pMsg_MT);
	        
	// PIN产生与加密
	int SFF_GenRandomPIN(Pointer hSessionHandle, RQ_MSG_JA.ByReference pMsg_JA, RP_MSG_JB.ByReference pMsg_JB);
	int SFF_EncryptPINWithLMK(Pointer hSessionHandle, RQ_MSG_BA.ByReference pMsg_BA, RP_MSG_BB.ByReference pMsg_BB);
	int SFF_DecryptPINWithLMK(Pointer hSessionHandle, RQ_MSG_NG.ByReference pMsg_NG, RP_MSG_NH.ByReference pMsg_NH);
	        
	// PIN密文转换
	int SFF_EncPINTPK2LMK(Pointer hSessionHandle, RQ_MSG_JC.ByReference pMsg_JC, RP_MSG_JD.ByReference pMsg_JD);
	int SFF_EncPINZPK2LMK(Pointer hSessionHandle, RQ_MSG_JE.ByReference pMsg_JE, RP_MSG_JF.ByReference pMsg_JF);
	int SFF_EncPINLMK2ZPK(Pointer hSessionHandle, RQ_MSG_JG.ByReference pMsg_JG, RP_MSG_JH.ByReference pMsg_JH);
	int SFF_EncPINTPK2ZPK(Pointer hSessionHandle, RQ_MSG_CA.ByReference pMsg_CA, RP_MSG_CB.ByReference pMsg_CB);
	int SFF_EncPINZPK2ZPK(Pointer hSessionHandle, RQ_MSG_CC.ByReference pMsg_CC, RP_MSG_CD.ByReference pMsg_CD);
	int SFF_EncPINTPKZPK(Pointer hSessionHandle, RQ_MSG_TI.ByReference pMsg_TI, RP_MSG_TJ.ByReference pMsg_TJ);
	int SFF_EncCharPINPubK2ZPK(Pointer hSessionHandle, RQ_MSG_N6.ByReference pMsg_N6, RP_MSG_N7.ByReference pMsg_N7);
	int SFF_EncDigitPINPubK2ZPK(Pointer hSessionHandle, RQ_MSG_N8.ByReference pMsg_N8, RP_MSG_N9.ByReference pMsg_N9);
	int SFF_EncPINZPK2Private(Pointer hSessionHandle, RQ_MSG_CB.ByReference pMsg_CB, RP_MSG_CC.ByReference pMsg_CC);
	        
	// PIN验证
	int SFF_IBMGenPINOffset(Pointer hSessionHandle, RQ_MSG_DE.ByReference pMsg_DE, RP_MSG_DF.ByReference pMsg_DF);
	int SFF_IBMGenPIN(Pointer hSessionHandle, RQ_MSG_EE.ByReference pMsg_EE, RP_MSG_EF.ByReference pMsg_EF);
	int SFF_IBMVerifyTerminalPIN(Pointer hSessionHandle, RQ_MSG_DA.ByReference pMsg_DA);
	int SFF_IBMVerifyExchangePIN(Pointer hSessionHandle, RQ_MSG_EA.ByReference pMsg_EA);
	int SFF_VISAGenPVV(Pointer hSessionHandle, RQ_MSG_DG.ByReference pMsg_DG, RP_MSG_DH.ByReference pMsg_DH);
	        
	// CVV运算
	int SFF_VISAGenCVV(Pointer hSessionHandle, RQ_MSG_CW.ByReference pMsg_CW, RP_MSG_CX.ByReference pMsg_CX);
	int SFF_VISAVerifyCVV(Pointer hSessionHandle, RQ_MSG_CY.ByReference pMsg_CY);
	        
	// 数据加解密
	int SFF_EncDecData(Pointer hSessionHandle, RQ_MSG_E0.ByReference pMsg_E0, RP_MSG_E1.ByReference pMsg_E1);
	        
	// 信函打印
	int SFF_PrintLoadFormat(Pointer hSessionHandle, RQ_MSG_PA.ByReference pMsg_PA);
	int SFF_PrintPIN(Pointer hSessionHandle, RQ_MSG_PE.ByReference pMsg_PE, RP_MSG_PF.ByReference pMsg_PF, int bWait);
	int SFF_PrintKey(Pointer hSessionHandle, RQ_MSG_NE.ByReference pMsg_NE, RP_MSG_NF.ByReference pMsg_NF, int bWait);
	int SFF_PrintKeyDivision(Pointer hSessionHandle, RQ_MSG_A2.ByReference pMsg_A2, RP_MSG_A3.ByReference pMsg_A3, int bWait);
	int SFF_PrintPINVerify(Pointer hSessionHandle, RQ_MSG_PG.ByReference pMsg_PG);
	        
	// 其他
	int SFF_GetEncryptorInfo(Pointer hSessionHandle, RP_MSG_ND.ByReference pMsg_ND);

    // OTP动态口令
	int SFF_OTPGenSeed(Pointer hSessionHandle, RQ_MSG_F3.ByReference pMsg_F3, RP_MSG_F4.ByReference pMsg_F4);
	int SFF_OTPDecSeed(Pointer hSessionHandle, RQ_MSG_F4.ByReference pMsg_F4, RP_MSG_F5.ByReference pMsg_F5);
	int SFF_OTPGenDynamicPWD(Pointer hSessionHandle, RQ_MSG_F5.ByReference pMsg_F5, RP_MSG_F6.ByReference pMsg_F6);

	// -------------------------------------- 非对称 -------------------------------------- 
	// RSA
	int SFF_RSAGenKey(Pointer hSessionHandle, RQ_MSG_EI.ByReference pMsg_EI, RP_MSG_EJ.ByReference pMsg_EJ);
	int SFF_RSALoadKey(Pointer hSessionHandle, RQ_MSG_EJ.ByReference pMsg_EJ);
	int SFF_RSAGetPubKey(Pointer hSessionHandle, RQ_MSG_ER.ByReference pMsg_ER, RP_MSG_ES.ByReference pMsg_ES);
	int SFF_RSAPubEncrypt(Pointer hSessionHandle, RQ_MSG_3A.ByReference pMsg_3A, RP_MSG_3B.ByReference pMsg_3B);
	int SFF_RSAPriDecrypt(Pointer hSessionHandle, RQ_MSG_3B.ByReference pMsg_3B, RP_MSG_3C.ByReference pMsg_3C);
	int SFF_RSASign(Pointer hSessionHandle, RQ_MSG_EW.ByReference pMsg_EW, RP_MSG_EX.ByReference pMsg_EX);
	int SFF_RSAVerify(Pointer hSessionHandle, RQ_MSG_EY.ByReference pMsg_EY);
	int SFF_RSAExportKey(Pointer hSessionHandle, RQ_MSG_TR.ByReference pMsg_TR, RP_MSG_TS.ByReference pMsg_TS);
	int SFF_RSAImportKey(Pointer hSessionHandle, RQ_MSG_TS.ByReference pMsg_TS, RP_MSG_TT.ByReference pMsg_TT);
	int SFF_RSAExportSymKey(Pointer hSessionHandle, RQ_MSG_TV.ByReference pMsg_TV, RP_MSG_TW.ByReference pMsg_TW);
	int SFF_RSAImportSymKey(Pointer hSessionHandle, RQ_MSG_TW.ByReference pMsg_TW, RP_MSG_TX.ByReference pMsg_TX);
	int SFF_RSAGenPubKeyMAC(Pointer hSessionHandle, RQ_MSG_EO.ByReference pMsg_EO, RP_MSG_EP.ByReference pMsg_EP);
	
	// SM2
	int SFF_SM2GenKey(Pointer hSessionHandle, RQ_MSG_E7.ByReference pMsg_E7, RP_MSG_E8.ByReference pMsg_E8);
	int SFF_SM2LoadKey(Pointer hSessionHandle, RQ_MSG_E1.ByReference pMsg_E1);
	int SFF_SM2GetPubKey(Pointer hSessionHandle, RQ_MSG_E2.ByReference pMsg_E2, RP_MSG_E3.ByReference pMsg_E3);
	int SFF_SM2PubEncrypt(Pointer hSessionHandle, RQ_MSG_E3.ByReference pMsg_E3, RP_MSG_E4.ByReference pMsg_E4);
	int SFF_SM2PriDecrypt(Pointer hSessionHandle, RQ_MSG_E4.ByReference pMsg_E4, RP_MSG_E5.ByReference pMsg_E5);
	int SFF_SM2Sign(Pointer hSessionHandle, RQ_MSG_E5.ByReference pMsg_E5, RP_MSG_E6.ByReference pMsg_E6);
	int SFF_SM2Verify(Pointer hSessionHandle, RQ_MSG_E6.ByReference pMsg_E6);
	int SFF_SM2SignDigest(Pointer hSessionHandle, RQ_MSG_ED.ByReference pMsg_ED, RP_MSG_EE.ByReference pMsg_EE);
	int SFF_SM2VerifyDigest(Pointer hSessionHandle, RQ_MSG_EF.ByReference pMsg_EF);
	int SFF_SM2ExportKey(Pointer hSessionHandle, RQ_MSG_TT.ByReference pMsg_TT, RP_MSG_TU.ByReference pMsg_TU);
	int SFF_SM2ImportKey(Pointer hSessionHandle, RQ_MSG_TU.ByReference pMsg_TU, RP_MSG_TV.ByReference pMsg_TV);
	int SFF_SM2ExportSymKey(Pointer hSessionHandle, RQ_MSG_TX.ByReference pMsg_TX, RP_MSG_TY.ByReference pMsg_TY);
	int SFF_SM2ImportSymKey(Pointer hSessionHandle, RQ_MSG_TY.ByReference pMsg_TY, RP_MSG_TZ.ByReference pMsg_TZ);
	int SFF_SM2GenPubKeyMAC(Pointer hSessionHandle, RQ_MSG_TQ.ByReference pMsg_TQ, RP_MSG_TR.ByReference pMsg_TR);
}
