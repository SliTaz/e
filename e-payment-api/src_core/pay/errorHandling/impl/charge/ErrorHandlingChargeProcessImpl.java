package org.e.payment.core.pay.errorHandling.impl.charge;

import org.e.payment.core.pay.ProcessType;
import org.e.payment.core.pay.errorHandling.impl.AbsErrorHandlingProcess;
import org.e.payment.core.pay.submit.SubmitPayProcess;
import org.e.payment.core.pay.submit.SubmitPayProcessFactory;
import org.e.payment.core.pay.submit.vo.ErrorHandlingBankChargeErrorRequest;
import org.e.payment.core.pay.submit.vo.ErrorHandlingBankChargeErrorResponse;
import org.e.payment.core.pay.submit.vo.ErrorHandlingChargeRequest;
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
import com.zbensoft.e.payment.api.service.api.ChargeErrorHandlingBankService;
import com.zbensoft.e.payment.api.vo.errorHandling.ErrorHandlingChargeVo;
import com.zbensoft.e.payment.db.domain.ChargeErrorHandlingBank;

public class ErrorHandlingChargeProcessImpl extends AbsErrorHandlingProcess {

	private static final Logger log = LoggerFactory.getLogger(ErrorHandlingChargeProcessImpl.class);
	private ChargeErrorHandlingBankService chargeErrorHandlingBankService = SpringBeanUtil.getBean(ChargeErrorHandlingBankService.class);

	protected AmqpTemplate rabbitTemplate = SpringBeanUtil.getBean(AmqpTemplate.class);

	protected String key = "ErrorHandlingBankCharge-";

	public ErrorHandlingChargeProcessImpl() {

	}

	@Override
	public boolean processErrorHandling(Object ob) {

		ErrorHandlingChargeVo errorHandlingChargeVo = (ErrorHandlingChargeVo) ob;
		if (errorHandlingChargeVo.getBankChargeInfo() == null) {// 银行没有
			if (errorHandlingChargeVo.getTradeInfo() == null) {// E-pay没有
				ERROR_HANDLING_LOG.INFO(String.format("%s bank and epay both null.", key));
			} else {// 只有 E-pay 存在, 写日志
				
				ErrorHandlingBankChargeErrorRequest errorHandlingBankChargeErrorRequest = new ErrorHandlingBankChargeErrorRequest();
				errorHandlingBankChargeErrorRequest.setTradeInfo(errorHandlingChargeVo.getTradeInfo());

				SubmitPayProcess submitPayProcess = SubmitPayProcessFactory.getInstance().get(ProcessType.ERROR_HANDLING_CHARGE_BANK_MISS);
				if (submitPayProcess == null) {
					ERROR_HANDLING_LOG.INFO(String.format("processErrorHandling submitPayProcess CHARGE_ERROR_HANDLING_BANK_ERROR is null %s", errorHandlingChargeVo.toString()));
					return false;
				}
				ResponseRestEntity<ErrorHandlingBankChargeErrorResponse> errorHandlingBankChargeErrorResponse = (ResponseRestEntity<ErrorHandlingBankChargeErrorResponse>) submitPayProcess
						.process(errorHandlingBankChargeErrorRequest);
				if (HttpRestStatus.OK == errorHandlingBankChargeErrorResponse.getStatusCode()) {
					ChargeErrorHandlingBank chargeErrorHandlingBank = new ChargeErrorHandlingBank();
					// 更新差错处理结果
					chargeErrorHandlingBank.setBankId(errorHandlingChargeVo.getBankId());
					chargeErrorHandlingBank.setReconciliationTime(errorHandlingChargeVo.getReconciliationTime());
					chargeErrorHandlingBank.setRefNo(errorHandlingChargeVo.getTradeInfo().getMerchantOrderNo());
					chargeErrorHandlingBank.setTradeSeq(errorHandlingChargeVo.getTradeInfo().getTradeSeq());
					chargeErrorHandlingBank.setReason(MessageDef.ERROR_HANDLING_REASON.BANK_NO_INFO);
					chargeErrorHandlingBank.setHanndlingResult(MessageDef.ERROR_HANDLING_RESULT.MANUAL_HANDLING);
					try {
						chargeErrorHandlingBankService.updateByPrimaryKeySelective(chargeErrorHandlingBank);
						ERROR_HANDLING_LOG.INFO(String.format("processErrorHandling CHARGE_ERROR_HANDLING_BANK_ERROR succ %s", errorHandlingChargeVo));
					}catch (Exception e) {
						MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "ErrorHandlingBankChargeImpl chargeErrorHandlingBankService.updateByPrimaryKeySelective", JSONObject.toJSONString(errorHandlingChargeVo)));
						log.error(String.format("processErrorHandling CHARGE_ERROR_HANDLING_BANK_ERROR chargeErrorHandlingBankService.updateByPrimaryKeySelective not succ %s", chargeErrorHandlingBank.toString()), e);
						ERROR_HANDLING_LOG.INFO(String.format("processErrorHandling CHARGE_ERROR_HANDLING_BANK_ERROR chargeErrorHandlingBankService.updateByPrimaryKeySelective not succ %s", errorHandlingChargeVo.toString()));
						ERROR_HANDLING_LOG.ERROR(String.format("processErrorHandling CHARGE_ERROR_HANDLING_BANK_ERROR chargeErrorHandlingBankService.updateByPrimaryKeySelective not succ %s", errorHandlingChargeVo.toString()), e);
						return false;
					}
				}
				
				ERROR_HANDLING_LOG.INFO(String.format("bank has none data %s", errorHandlingChargeVo.toString()));
				return true;
			}
		} else {// 银行有
			if (errorHandlingChargeVo.getTradeInfo() == null) {// E-pay没有
				
				ErrorHandlingChargeRequest errorHandlingChargeRequest = new ErrorHandlingChargeRequest();

				SubmitPayProcess submitPayProcess = SubmitPayProcessFactory.getInstance().get(ProcessType.ERROR_HANDLING_CHARGE_EPAY_MISS);
				if (submitPayProcess == null) {
					ERROR_HANDLING_LOG.INFO(String.format("processErrorHandling submitPayProcess CHARGE_ERROR_HANDLING_BANK_ERROR is null %s", errorHandlingChargeVo.toString()));
					return false;
				}
				ResponseRestEntity<ErrorHandlingBankChargeErrorResponse> errorHandlingBankChargeErrorResponse = (ResponseRestEntity<ErrorHandlingBankChargeErrorResponse>) submitPayProcess
						.process(errorHandlingChargeRequest);
				if (HttpRestStatus.OK == errorHandlingBankChargeErrorResponse.getStatusCode()) {
					ChargeErrorHandlingBank chargeErrorHandlingBank = new ChargeErrorHandlingBank();
					// 更新差错处理结果
					chargeErrorHandlingBank.setBankId(errorHandlingChargeVo.getBankId());
					chargeErrorHandlingBank.setReconciliationTime(errorHandlingChargeVo.getReconciliationTime());
					chargeErrorHandlingBank.setRefNo(errorHandlingChargeVo.getBankChargeInfo().getRefNo());
					chargeErrorHandlingBank.setReason(MessageDef.ERROR_HANDLING_REASON.EPAY_NO_INFO);
					chargeErrorHandlingBank.setHanndlingResult(MessageDef.ERROR_HANDLING_RESULT.MANUAL_HANDLING);
					try {
						chargeErrorHandlingBankService.updateByPrimaryKeySelective(chargeErrorHandlingBank);
						ERROR_HANDLING_LOG.INFO(String.format("processErrorHandling CHARGE_ERROR_HANDLING_BANK_ERROR succ %s", errorHandlingChargeVo));
					}catch (Exception e) {
						MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "ErrorHandlingBankChargeImpl chargeErrorHandlingBankService.updateByPrimaryKeySelective", JSONObject.toJSONString(errorHandlingChargeVo)));
						log.error(String.format("processErrorHandling CHARGE_ERROR_HANDLING_BANK_ERROR chargeErrorHandlingBankService.updateByPrimaryKeySelective not succ %s", chargeErrorHandlingBank.toString()), e);
						ERROR_HANDLING_LOG.INFO(String.format("processErrorHandling CHARGE_ERROR_HANDLING_BANK_ERROR chargeErrorHandlingBankService.updateByPrimaryKeySelective not succ %s", errorHandlingChargeVo.toString()));
						ERROR_HANDLING_LOG.ERROR(String.format("processErrorHandling CHARGE_ERROR_HANDLING_BANK_ERROR chargeErrorHandlingBankService.updateByPrimaryKeySelective not succ %s", errorHandlingChargeVo.toString()), e);
						return false;
					}
				}
				
				ERROR_HANDLING_LOG.INFO(String.format("E-pay has none data %s", errorHandlingChargeVo.toString()));
				return true;

			} else {// 都存在，但是结果不一致

				ErrorHandlingBankChargeErrorRequest errorHandlingBankChargeErrorRequest = new ErrorHandlingBankChargeErrorRequest();
				errorHandlingBankChargeErrorRequest.setTradeInfo(errorHandlingChargeVo.getTradeInfo());

				SubmitPayProcess submitPayProcess = SubmitPayProcessFactory.getInstance().get(ProcessType.ERROR_HANDLING_CHARGE_REFUND);
				if (submitPayProcess == null) {
					ERROR_HANDLING_LOG.INFO(String.format("processErrorHandling submitPayProcess CHARGE_ERROR_HANDLING_BANK_ERROR is null %s", errorHandlingChargeVo.toString()));
					return false;
				}
				ResponseRestEntity<ErrorHandlingBankChargeErrorResponse> errorHandlingBankChargeErrorResponse = (ResponseRestEntity<ErrorHandlingBankChargeErrorResponse>) submitPayProcess
						.process(errorHandlingBankChargeErrorRequest);
				if (HttpRestStatus.OK == errorHandlingBankChargeErrorResponse.getStatusCode()) {
					ChargeErrorHandlingBank chargeErrorHandlingBank = new ChargeErrorHandlingBank();
					// 更新差错处理结果
					chargeErrorHandlingBank.setBankId(errorHandlingChargeVo.getBankId());
					chargeErrorHandlingBank.setReconciliationTime(errorHandlingChargeVo.getReconciliationTime());
					chargeErrorHandlingBank.setRefNo(errorHandlingChargeVo.getTradeInfo().getMerchantOrderNo());
					chargeErrorHandlingBank.setTradeSeq(errorHandlingChargeVo.getTradeInfo().getTradeSeq());
					chargeErrorHandlingBank.setReason(getRessonNum(errorHandlingChargeVo.getBankChargeInfo().getChargeResult()));
					chargeErrorHandlingBank.setHanndlingResult(MessageDef.ERROR_HANDLING_RESULT.RECV_AMOUNT);

					try {
						chargeErrorHandlingBankService.updateByPrimaryKeySelective(chargeErrorHandlingBank);
						ERROR_HANDLING_LOG.INFO(String.format("processErrorHandling CHARGE_ERROR_HANDLING_BANK_ERROR succ %s", errorHandlingChargeVo));

						try {
							rabbitTemplate.convertAndSend(RabbitmqDef.CHARGE_ERROR_HANDLING_BOOKKEEPING.EXCHANGE, null, errorHandlingChargeVo.getTradeInfo());
							ERROR_HANDLING_LOG.INFO(String.format("send to CHARGE_ERROR_HANDLING CHARGE_ERROR_HANDLING_BANK_ERROR", errorHandlingChargeVo.getTradeInfo()));
							BOOKKEEPING_LOG.INFO(String.format("send info CHARGE_ERROR_HANDLING CHARGE_ERROR_HANDLING_BANK_ERROR =%s", errorHandlingChargeVo.getTradeInfo()));
						} catch (Exception e) {
							MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "ErrorHandlingBankChargeImpl rabbitTemplate.convertAndSend", JSONObject.toJSONString(errorHandlingChargeVo)));
							log.error(String.format("sendToRabbitmq error info =%s", errorHandlingChargeVo.getTradeInfo()), e);
							RECONCILIATION_LOG.INFO(String.format("sendToRabbitmq error info =%s", errorHandlingChargeVo.getTradeInfo()));
							RECONCILIATION_LOG.ERROR(String.format("sendToRabbitmq error info =%s", errorHandlingChargeVo.getTradeInfo()), e);
							BOOKKEEPING_LOG.INFO(String.format("sendToRabbitmq error info =%s", errorHandlingChargeVo.getTradeInfo()));
							BOOKKEEPING_LOG.ERROR(String.format("sendToRabbitmq error info =%s", errorHandlingChargeVo.getTradeInfo()), e);
							return false;
						}
					} catch (Exception e) {
						MessageAlarmFactory.getInstance()
								.add(String.format("%s error,info:%s", "ErrorHandlingBankChargeImpl chargeErrorHandlingBankService.updateByPrimaryKeySelective", JSONObject.toJSONString(errorHandlingChargeVo)));
						log.error(String.format("processErrorHandling CHARGE_ERROR_HANDLING_BANK_ERROR chargeErrorHandlingBankService.updateByPrimaryKeySelective not succ %s", chargeErrorHandlingBank.toString()), e);
						ERROR_HANDLING_LOG
								.INFO(String.format("processErrorHandling CHARGE_ERROR_HANDLING_BANK_ERROR chargeErrorHandlingBankService.updateByPrimaryKeySelective not succ %s", errorHandlingChargeVo.toString()));
						ERROR_HANDLING_LOG
								.ERROR(String.format("processErrorHandling CHARGE_ERROR_HANDLING_BANK_ERROR chargeErrorHandlingBankService.updateByPrimaryKeySelective not succ %s", errorHandlingChargeVo.toString()), e);
						return false;
					}

				} else {
					ERROR_HANDLING_LOG.INFO(String.format("processErrorHandling not succ %s", errorHandlingChargeVo.toString()));
					return false;
				}

			}
		}
		return true;

	}

	/**
	 * 
	 * 获取提现文件结果的数字
	 * @param chargeResult
	 * @return
	 */
	private Integer getRessonNum(String chargeResult) {
		if(chargeResult!=null){
			switch (chargeResult) {
			case MessageDef.CHARGE_DEBITO_RESULT.LACK_OF_FUNDS:
				return MessageDef.CHARGE_DEBITO_RESULT.LACK_OF_FUNDS_INT;
			case MessageDef.CHARGE_DEBITO_RESULT.ORDER_REJECTED:
				return MessageDef.CHARGE_DEBITO_RESULT.ORDER_REJECTED_INT;
			case MessageDef.CHARGE_DEBITO_RESULT.REJECTED_BY_HOST:
				return MessageDef.CHARGE_DEBITO_RESULT.REJECTED_BY_HOST_INT;
			case MessageDef.CHARGE_DEBITO_RESULT.REJECTED_BY_SIP:
				return MessageDef.CHARGE_DEBITO_RESULT.REJECTED_BY_SIP_INT;
			case MessageDef.CHARGE_DEBITO_RESULT.SUCCESS_BUT_PROCESS:
				return MessageDef.CHARGE_DEBITO_RESULT.SUCCESS_BUT_PROCESS_INT;
			case MessageDef.CHARGE_DEBITO_RESULT.SUCCESS:
				return MessageDef.CHARGE_DEBITO_RESULT.SUCCESS_INT;
			default:
				return MessageDef.CHARGE_DEBITO_RESULT.UNKNOW_INT;
			}
		}
		return MessageDef.CHARGE_DEBITO_RESULT.UNKNOW_INT;
	}

}
