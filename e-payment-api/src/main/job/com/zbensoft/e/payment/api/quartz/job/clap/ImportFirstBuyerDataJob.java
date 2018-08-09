package com.zbensoft.e.payment.api.quartz.job.clap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.quartz.job.clap.firstImport.factory.RecordFactory;
import com.zbensoft.e.payment.api.quartz.job.clap.firstImport.thread.EnCodeThread2;
import com.zbensoft.e.payment.api.quartz.job.clap.firstImport.thread.ReadThread;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.api.vo.clap.ClapBuyer;
import com.zbensoft.e.payment.api.vo.clap.ClapBuyerHeader;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.mutliThread.MultiThreadManage;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.ConsumerUser;

/**
 * 测试
 * 
 * 0 10 0/2 * * ?
 * 
 * @author xieqiang
 *
 */
public class ImportFirstBuyerDataJob implements Job {
	private static final Logger log = LoggerFactory.getLogger(ClapIncrementalAbs.class);
	private static String key = "ImportFirstBuyerDataJob";
	
	
	@Value("${password.default}")
	private String DEFAULT_PASSWORD;

	@Value("${payPassword.default}")
	private String DEFAULT_PAYPASSWORD;
	ConsumerUserService consumerUserService = SpringBeanUtil.getBean(ConsumerUserService.class);
	ConsumerUserClapService consumerUserClapService = SpringBeanUtil.getBean(ConsumerUserClapService.class);;
	
	
	
	String JOB_CLAP_DOWNLOAD_FILE_PATH = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_DOWNLOAD_FILE_PATH);
	String BYER_FILE_PREFIX = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_BYER_FILE_PREFIX);
	String BYER_FILE_DATEFORMATE = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_BYER_FILE_DATEFORMATE);
	int JOB_CLAP_SFTP_FILE_DAY_BEFORE =SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CLAP_SFTP_FILE_DAY_BEFORE);
	String buyerFileName = BYER_FILE_PREFIX+ DateUtil.convertDateToString(DateUtil.addDate(new Date(), JOB_CLAP_SFTP_FILE_DAY_BEFORE, 3), BYER_FILE_DATEFORMATE)+".txt";
	
	String JOB_CLAP_DIF_FILE_PATH = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_DIF_FILE_PATH);
	
	
	private List<String> buyerTmpList=new ArrayList<>();
	
	

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		new ReadThread("Reading-BuyerFileThread").start();
		TASK_LOG.INFO(String.format("%s Start", key));
		MultiThreadManage.getInstance().addThread(EnCodeThread2.class, 90,100, 100);
		
		
		while (true) {
			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
				log.error(String.format("%s  ", key), e);
				TASK_LOG.ERROR(String.format("%s ", key), e);
			}

			if (RecordFactory.getInstance().getListSieze() == 0) {
				TASK_LOG.INFO(String.format("%s End", key));
				try {
					Thread.sleep(20 * 60 * 1000);
				} catch (InterruptedException e) {
					log.error(String.format("%s ", key), e);
					TASK_LOG.ERROR(String.format("%s ", key), e);
				}
				TASK_LOG.INFO(String.format("<====ClapBuyer Check Start===>"));
				if (executeClapBuyerChackJob()) {
					TASK_LOG.INFO(String.format("<====ClapBuyer Check End===>"));
					TASK_LOG.INFO(String.format("DIF records in /data/master/zben/file/clap/dif/DIF_%s", buyerFileName));
				}
				break;
			}else{
				TASK_LOG.INFO("File read to DB finish:==========>>>>>>>>>["+RecordFactory.getInstance().showPercent()+"]");
			}
		}
		
	}
	
	
	
	protected boolean executeClapBuyerChackJob() {
		boolean isSucc = true;
		isSucc = readBuyerFileFindDif();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s readBuyerFileFindDif is error", key));
			return false;
		} else {
			TASK_LOG.INFO(String.format("%s readBuyerFileFindDif is Success", key));
		}
		isSucc = wirteFile();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s wirteFile is error", key));
			return false;
		} else {
			TASK_LOG.INFO(String.format("%s wirteFile is Success", key));
		}
		return true;
	}
	

	
/***Buyer functions Start***/		
	
	/**
	 * Buyer入库方法
	 * @return
	 */
	private boolean readBuyerFileFindDif() {
		BufferedReader reader=null;
		try {
			
			File file =getClapBuyerFile();//获取文件
			if (file.exists() && file.isFile()) {//判断文件是否存在
				BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
				reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);// 用5M的缓冲读取文本文件
				String line = reader.readLine();//读取文件头
				if (line != null) {//文件头是否未空
					ClapBuyerHeader buyerUpdateHeader=new ClapBuyerHeader(line);//生成文件头类
					if (buyerUpdateHeader != null && buyerUpdateHeader.getTotalFileRecord()!=null) {
						long readCount=1;
						while ((line = reader.readLine()) != null) {//读取文件内容
							ClapBuyer buyer = new ClapBuyer(line);
							if (buyer != null && buyer.getVid() != null) {// buyer是否为空
								try {
									ConsumerUser existResult=null;
									boolean isExist = isExistBuyer(buyer,existResult);//buyer 是否存在
									if (!isExist) {
										buyerTmpList.add(line);
										TASK_LOG.INFO("####==This buyer not insert success==#### :"+line);
									}
									
								} catch (Exception e) {//数据插入异常
									log.error(String.format("%s Select db failed ", key), e);
									TASK_LOG.ERROR(String.format("%s  Select db failed", key), e);
								}

							}else{//第[n]条数为空或长度不对
								TASK_LOG.ERROR(String.format("%s read file %s success, The [%d] record is null or length incorrect", key,file.getName(),readCount));
							}
							readCount++;
						}
						TASK_LOG.INFO(String.format("%s read file %s finish, success record --%d/%s--", key,file.getName(),(readCount-1),buyerUpdateHeader.getTotalFileRecord()));
					}else{//文件头格式不正确
						TASK_LOG.ERROR(String.format("%s read file %s failed, file Header formate incorrect", key,file.getName()));
						return false;
					}
				}else{//文件头不存在
					TASK_LOG.ERROR(String.format("%s read file %s failed, file Header missed", key,file.getName()));
					return false;
				}
			}else{//文件不存在
				TASK_LOG.ERROR(String.format("%s read file %s failed, file not exist", key,file.getName()));
				return false;
			}
			return true;
		} catch (Exception e) {//读取文件异常
			log.error(String.format("%s Read buyer file failed", key), e);
			TASK_LOG.ERROR(String.format("%s Read buyer file failed", key), e);
		}finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return false;
	}

	
	
	private boolean wirteFile() {

		BufferedWriter bufferedWriter = null;
		try {
			File file = new File(JOB_CLAP_DIF_FILE_PATH+"DIF_"+buyerFileName);// 指定要写入的文件
			if (!file.getParentFile().exists() && file.getParentFile().isDirectory()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {// 如果文件不存在则创建
				file.createNewFile();
			}
			// 获取该文件的缓冲输出流
			if(buyerTmpList!=null&&buyerTmpList.size()>0){
			bufferedWriter = new BufferedWriter(new FileWriter(file));
				bufferedWriter.write(DateUtil.convertDateToString(new Date(), DateUtil.DATE_FORMAT_FIVE)+","+buyerTmpList.size());
				bufferedWriter.newLine();// 表示换行
				for (String line : buyerTmpList) {
					bufferedWriter.write(line);
					bufferedWriter.newLine();// 表示换行
				}
			
			}
			return true;
		} catch (Exception e) {
			log.error(String.format("%s wirteFile Exception", key), e);
			TASK_LOG.ERROR(String.format("%s wirteFile Exception", key), e);
		} finally {
			try {
				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (IOException e) {
				log.error(String.format("%s flush buffereWriter Exception", key), e);
				TASK_LOG.ERROR(String.format("%s flush buffereWriter Exception", key), e);
			}
		}

		return false;
	}
	
	/**
	 * 判断用户是存在
	 * @param buyer
	 * @param existResult
	 * @return
	 */
	private boolean isExistBuyer(ClapBuyer buyer,ConsumerUser existResult) {
		existResult = consumerUserService.selectByPrimaryKey(getUserId(buyer));
		if(existResult!=null){
			return true;
		}
		return false;
	}


	/**
	 * 获取用户UserId
	 * @param buyer
	 * @return
	 */
	private String getUserId(ClapBuyer buyer){
		if(buyer!=null&&buyer.getVid()!=null){
			return MessageDef.USER_TYPE.CONSUMER_STRING + buyer.getVid();
		}
		return null;
	}
	
	/**
	 * 获取用户更新文件路径
	 * @return
	 */
	private File getClapBuyerFile() {
		
		File file = new File(JOB_CLAP_DOWNLOAD_FILE_PATH + buyerFileName);
		return file;
	}
}
