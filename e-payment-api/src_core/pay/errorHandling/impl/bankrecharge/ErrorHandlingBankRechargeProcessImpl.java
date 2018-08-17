package org.e.payment.core.pay.errorHandling.impl.bankrecharge;

import org.e.payment.core.pay.ProcessType;
import org.e.payment.core.pay.errorHandling.impl.AbsErrorHandlingProcess;
import org.e.payment.core.pay.submit.SubmitPayProcess;
import org.e.payment.core.pay.submit.SubmitPayProcessFactory;
import org.e.payment.core.pay.submit.vo.ErrorHandlingBankErrorRequest;
import org.e.payment.core.pay.submit.vo.ErrorHandlingBankErrorResponse;
import org.e.payment.core.pay.submit.vo.ErrorHandlingRechargeRequest;
import org.e.payment.core.pay.submit.vo.ErrorHandlingRechargeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.api.alarm.alarm.factory.MessageAlarmFactory;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.RabbitmqDef;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.log.BOOKKEEPING_LOG;
import com.zbensoft.e.payment.api.log.ERROR_HANDLING_LOG;
import com.zbensoft.e.payment.api.log.RECONCILIATION_LOG;
import com.zbensoft.e.payment.api.service.api.ErrorHandlingBankService;
import com.zbensoft.e.payment.api.vo.errorHandling.ErrorHandlingVo;
import com.zbensoft.e.payment.db.domain.ErrorHandlingBank;

public class ErrorHandlingBankRechargeProcessImpl extends AbsErrorHandlingProcess {

	private static final Logger log = LoggerFactory.getLogger(ErrorHandlingBankRechargeProcessImpl.class);

	private ErrorHandlingBankService errorHandlingBankService = SpringBeanUtil.getBean(ErrorHandlingBankService.class);

	protected AmqpTemplate rabbitTemplate = SpringBeanUtil.getBean(AmqpTemplate.class);

	@Override
	public boolean processErrorHandling(Object ob) {
		ErrorHandlingVo errorHandlingVo = (ErrorHandlingVo)ob;
		if (errorHandlingVo.getBankTradeInfo() == null) {
			if (errorHandlingVo.getTradeInfo() == null) {
				ERROR_HANDLING_LOG.INFO(String.format("both is null %s", errorHandlingVo.toString()));
				return false;
			} else {
				// 平台有，银行没有
				// 修改当前账单，扣款
				ErrorHandlingBankErrorRequest errorHandlingBankErrorRequest = new ErrorHandlingBankErrorRequest();
				errorHandlingBankErrorRequest.setTradeInfo(errorHandlingVo.getTradeInfo());

				SubmitPayProcess submitPayProcess = SubmitPayProcessFactory.getInstance().get(ProcessType.ERROR_HANDLING_BANK_RECHARGE_REFUND);
				if (submitPayProcess == null) {
					ERROR_HANDLING_LOG.INFO(String.format("processErrorHandling submitPayProcess ERROR_HANDLING_BANK_ERROR is null %s", errorHandlingVo.toString()));
					return false;
				}

				ResponseRestEntity<ErrorHandlingBankErrorResponse> errorHandlingBankErrorResponse = (ResponseRestEntity<ErrorHandlingBankErrorResponse>) submitPayProcess.process(errorHandlingBankErrorRequest);
				if (HttpRestStatus.OK == errorHandlingBankErrorResponse.getStatusCode()) {
					// 更新差错处理结果
					ErrorHandlingBank errorHandlingBank = new ErrorHandlingBank();
					errorHandlingBank.setBankId(errorHandlingVo.getBankId());
					errorHandlingBank.setReconciliationTime(errorHandlingVo.getReconciliationTime());
					errorHandlingBank.setRefNo(errorHandlingVo.getTradeInfo().getMerchantOrderNo());
					errorHandlingBank.setTradeSeq(errorHandlingVo.getTradeInfo().getTradeSeq());
					errorHandlingBank.setHanndlingResult(MessageDef.ERROR_HANDLING_RESULT.PAY_AMOUNT);
					try {
						errorHandlingBankService.updateByPrimaryKeySelective(errorHandlingBank);
						ERROR_HANDLING_LOG.INFO(String.format("processErrorHandling ERROR_HANDLING_BANK_ERROR succ %s", errorHandlingVo));
						try {
							rabbitTemplate.convertAndSend(RabbitmqDef.ERROR_HANDLING_BOOKKEEPING.EXCHANGE, null, errorHandlingVo.getTradeInfo());
							ERROR_HANDLING_LOG.INFO(String.format("send to ERROR_HANDLING ERROR_HANDLING_BANK_ERROR", errorHandlingVo.getTradeInfo()));
							BOOKKEEPING_LOG.INFO(String.format("send info ERROR_HANDLING ERROR_HANDLING_BANK_ERROR =%s", errorHandlingVo.getTradeInfo()));
						} catch (Exception e) {
							MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "ErrorHandlingBankProcessImpl rabbitTemplate.convertAndSend", JSONObject.toJSONString(errorHandlingVo)));
							log.error(String.format("sendToRabbitmq error info =%s", errorHandlingVo.getTradeInfo()), e);

							RECONCILIATION_LOG.INFO(String.format("sendToRabbitmq error info =%s", errorHandlingVo.getTradeInfo()));
							RECONCILIATION_LOG.ERROR(String.format("sendToRabbitmq error info =%s", errorHandlingVo.getTradeInfo()), e);
							BOOKKEEPING_LOG.INFO(String.format("sendToRabbitmq error info =%s", errorHandlingVo.getTradeInfo()));
							BOOKKEEPING_LOG.ERROR(String.format("sendToRabbitmq error info =%s", errorHandlingVo.getTradeInfo()), e);
						}
					} catch (Exception e) {
						MessageAlarmFactory.getInstance()
								.add(String.format("%s error,info:%s", "ErrorHandlingBankProcessImpl errorHandlingBankService.updateByPrimaryKeySelective", JSONObject.toJSONString(errorHandlingVo)));
						log.error(String.format("processErrorHandling ERROR_HANDLING_BANK_ERROR errorHandlingBankService.updateByPrimaryKeySelective not succ %s", errorHandlingBank.toString()), e);

						ERROR_HANDLING_LOG.INFO(String.format("processErrorHandling ERROR_HANDLING_BANK_ERROR errorHandlingBankService.updateByPrimaryKeySelective not succ %s", errorHandlingBank.toString()));
						ERROR_HANDLING_LOG.ERROR(String.format("processErrorHandling ERROR_HANDLING_BANK_ERROR errorHandlingBankService.updateByPrimaryKeySelective not succ %s", errorHandlingBank.toString()), e);
						return false;
					}

				} else {
					ERROR_HANDLING_LOG.INFO(String.format("processErrorHandling not succ %s", errorHandlingVo.toString()));
					return false;
				}

			}
		} else {
			if (errorHandlingVo.getTradeInfo() == null) {
				// 银行有，平台没有
				// 补账单，充值
				ErrorHandlingRechargeRequest errorHandlingRechargeRequest = new ErrorHandlingRechargeRequest();
				errorHandlingRechargeRequest.setBankId(errorHandlingVo.getBankId());
				errorHandlingRechargeRequest.setChannelType(errorHandlingVo.getBankTradeInfo().getChannelType());
				errorHandlingRechargeRequest.setCurrencyType(errorHandlingVo.getBankTradeInfo().getCurrencyType());
				errorHandlingRechargeRequest.setRefNo(errorHandlingVo.getBankTradeInfo().getRefNo());
				errorHandlingRechargeRequest.setTransactionAmount(errorHandlingVo.getBankTradeInfo().getTransactionAmount());
				errorHandlingRechargeRequest.setTransactionDate(errorHandlingVo.getBankTradeInfo().getTransactionDate());
				errorHandlingRechargeRequest.setTransactionType(errorHandlingVo.getBankTradeInfo().getTransactionType());
				errorHandlingRechargeRequest.setVid(errorHandlingVo.getBankTradeInfo().getVid());

				SubmitPayProcess submitPayProcess = SubmitPayProcessFactory.getInstance().get(ProcessType.ERROR_HANDLING_BANK_RECHARGE_RECHARGE);
				if (submitPayProcess == null) {
					ERROR_HANDLING_LOG.INFO(String.format("processErrorHandling submitPayProcess is null %s", errorHandlingVo.toString()));
					return false;
				}
				ResponseRestEntity<ErrorHandlingRechargeResponse> errorHandlingRechargeResponse = (ResponseRestEntity<ErrorHandlingRechargeResponse>) submitPayProcess.process(errorHandlingRechargeRequest);
				if (HttpRestStatus.OK == errorHandlingRechargeResponse.getStatusCode()) {
					// 更新差错处理结果
					ErrorHandlingBank errorHandlingBank = new ErrorHandlingBank();
					errorHandlingBank.setBankId(errorHandlingVo.getBankId());
					errorHandlingBank.setReconciliationTime(errorHandlingVo.getReconciliationTime());
					errorHandlingBank.setRefNo(errorHandlingVo.getBankTradeInfo().getRefNo());
					errorHandlingBank.setTradeSeq(errorHandlingRechargeResponse.getBody().getTradeInfo().getTradeSeq());
					errorHandlingBank.setHanndlingResult(MessageDef.ERROR_HANDLING_RESULT.RECV_AMOUNT);
					try {
						errorHandlingBankService.updateByPrimaryKeySelective(errorHandlingBank);
						ERROR_HANDLING_LOG.INFO(String.format("processErrorHandling succ %s", errorHandlingVo.toString()));
						try {
							rabbitTemplate.convertAndSend(RabbitmqDef.ERROR_HANDLING_BOOKKEEPING.EXCHANGE, null, errorHandlingRechargeResponse.getBody().getTradeInfo());
							ERROR_HANDLING_LOG.INFO(String.format("send to ERROR_HANDLING", errorHandlingVo.toString()));
							BOOKKEEPING_LOG.INFO(String.format("send info =%s", errorHandlingVo.toString()));
						} catch (Exception e) {
							MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "ErrorHandlingBankProcessImpl rabbitTemplate.convertAndSend", JSONObject.toJSONString(errorHandlingVo)));
							log.error(String.format("sendToRabbitmq error info =%s", errorHandlingVo.toString()), e);

							RECONCILIATION_LOG.INFO(String.format("sendToRabbitmq error info =%s", errorHandlingVo.toString()));
							RECONCILIATION_LOG.ERROR(String.format("sendToRabbitmq error info =%s", errorHandlingVo.toString()), e);
							BOOKKEEPING_LOG.INFO(String.format("sendToRabbitmq error info =%s", errorHandlingVo.toString()));
							BOOKKEEPING_LOG.ERROR(String.format("sendToRabbitmq error info =%s", errorHandlingVo.toString()), e);
						}
					} catch (Exception e) {
						MessageAlarmFactory.getInstance()
								.add(String.format("%s error,info:%s", "ErrorHandlingBankProcessImpl errorHandlingBankService.updateByPrimaryKeySelective", JSONObject.toJSONString(errorHandlingVo)));
						log.error("", e);
						ERROR_HANDLING_LOG.INFO(String.format("processErrorHandling errorHandlingBankService.updateByPrimaryKeySelective not succ %s", errorHandlingBank.toString()));
						ERROR_HANDLING_LOG.ERROR(String.format("processErrorHandling errorHandlingBankService.updateByPrimaryKeySelective not succ %s", errorHandlingBank.toString()), e);
						return false;
					}

				} else {
					ERROR_HANDLING_LOG.INFO(String.format("processErrorHandling not succ %s", errorHandlingVo.toString()));
					return false;
				}
			} else {
				ERROR_HANDLING_LOG.INFO(String.format("both not null %s", errorHandlingVo.toString()));
				return false;
			}
		}
		return true;
	}

}
